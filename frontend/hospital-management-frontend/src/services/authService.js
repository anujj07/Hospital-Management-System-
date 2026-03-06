import axiosInstance from "../api/axiosConfig";

const localUsers = [
  { email: "patient@local.test", password: "patient123", role: "ROLE_PATIENT" },
  { email: "admin@local.test", password: "admin123", role: "ROLE_ADMIN" },
  { email: "doctor@local.test", password: "doctor123", role: "ROLE_DOCTOR" },
  { email: "nurse@local.test", password: "nurse123", role: "ROLE_NURSE" },
  { email: "staff@local.test", password: "staff123", role: "ROLE_STAFF" },
];
const universalDemoPassword = "Demo@12345X";

const toBase64Url = (value) =>
  btoa(JSON.stringify(value))
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/, "");

const createMockJwt = (email, role) => {
  const header = { alg: "HS256", typ: "JWT" };
  const now = Math.floor(Date.now() / 1000);
  const payload = {
    sub: email,
    role,
    iat: now,
    exp: now + 24 * 60 * 60,
  };

  return `${toBase64Url(header)}.${toBase64Url(payload)}.mock-signature`;
};

export const loginUser = async (credentials) => {
  const localUser = localUsers.find(
    (user) =>
      user.email.toLowerCase() === credentials.email.toLowerCase() &&
      user.password === credentials.password
  );

  if (localUser) {
    return {
      token: createMockJwt(localUser.email, localUser.role),
      role: localUser.role,
    };
  }

  // Universal local login for quick UI testing without backend users.
  if (credentials.password === universalDemoPassword && credentials.email) {
    return {
      token: createMockJwt(credentials.email, "ROLE_PATIENT"),
      role: "ROLE_PATIENT",
    };
  }

  try {
    const response = await axiosInstance.post("api/login", credentials);
    return response.data;
  } catch (error) {
    if (error.response) {
      throw error.response.data.message || "Invalid email or password";
    } else {
      throw error.message || "Unknown error occurred during login";
    }
  }
};
