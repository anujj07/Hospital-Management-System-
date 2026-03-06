import axiosInstance from "../api/axiosConfig";

const isMockSession = () =>
  sessionStorage.getItem("token")?.endsWith(".mock-signature");

const mockPatientDetails = {
  patientId: 1001,
  firstName: "Demo",
  lastName: "Patient",
  email: "test@demo.com",
  phoneNumber: "9999999999",
  gender: "Other",
  dateOfBirth: "1998-01-01",
  city: "Bengaluru",
  state: "Karnataka",
  country: "India",
};

const mockPatients = [
  mockPatientDetails,
  {
    patientId: 1002,
    firstName: "Asha",
    lastName: "Verma",
    email: "asha@demo.com",
    phoneNumber: "8888888888",
    gender: "Female",
    dateOfBirth: "1994-05-14",
    city: "Pune",
    state: "Maharashtra",
    country: "India",
  },
  {
    patientId: 1003,
    firstName: "Rohan",
    lastName: "Mehta",
    email: "rohan@demo.com",
    phoneNumber: "7777777777",
    gender: "Male",
    dateOfBirth: "1992-11-23",
    city: "Ahmedabad",
    state: "Gujarat",
    country: "India",
  },
];

// Register a new patient
export const registerPatient = async (patientData) => {
  try {
    const response = await axiosInstance.post('api/patients/register', patientData);
    return response.data; // On successful registration
  } catch (error) {
    

    if (error.response) {
      // Structured error response from the server
      const errorData = error.response.data;

      // Check if the error message is related to email already existing
      if (errorData === "Email is already registered.") {
        throw new Error("Email is already registered");
      } else {
        throw new Error(errorData.message || 'An unknown error occurred during registration');
      }
    } else if (error.request) {
      // Network error or no response from server
      throw new Error('No response from server. Please check your network connection.');
    } else {
      // Other types of errors (like config errors)
      throw new Error(error.message || 'An error occurred during registration');
    }
  }
};

// Update existing patient details
export const updatePatient = async (email, patientData) => {
  try {
    const response = await axiosInstance.put(`api/patients/update/${email}`, patientData);
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const fetchPatientDetails = async () => {
  if (isMockSession()) {
    return mockPatientDetails;
  }
  try {
    const response = await axiosInstance.get("/api/patients/mydetails");
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Fetch patient details by email
export const fetchPatientByEmail = async (email) => {
  try {
    const response = await axiosInstance.get(`/patients/email/${email}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching patient by email:", error);

    if (error.response) {
      const errorData = error.response.data;
      throw new Error(errorData.message || 'Error fetching patient details');
    } else if (error.request) {
      throw new Error('No response from server. Please check your network connection.');
    } else {
      throw new Error(error.message || 'An error occurred while fetching patient details');
    }
  }
};

// Fetch all patients
export const fetchAllPatients = async () => {
  if (isMockSession()) {
    return mockPatients;
  }
  try {
    const response = await axiosInstance.get('/api/patients/fetchAllPatients');
    return response.data;
  } catch (error) {
    console.error("Error fetching all patients:", error);

    if (error.response) {
      const errorData = error.response.data;
      throw new Error(errorData.message || 'Error fetching patient list');
    } else if (error.request) {
      throw new Error('No response from server. Please check your network connection.');
    } else {
      throw new Error(error.message || 'An error occurred while fetching all patients');
    }
  }
};

export const deletePatient = async (email) => {
  try {
    const response = await axiosInstance.delete(`api/patients/delete${email}`);
    return response.data;
  } catch (error) {
    throw error;
  }
};
