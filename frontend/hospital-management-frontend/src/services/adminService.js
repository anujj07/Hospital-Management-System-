import axiosInstance from "../api/axiosConfig";

const isMockSession = () =>
  sessionStorage.getItem("token")?.endsWith(".mock-signature");

const mockAdminData = {
  firstName: "Demo",
  lastName: "Admin",
  email: "admin@demo.com",
};

export const fetchAdminData=async()=>{
   if (isMockSession()) {
    return mockAdminData;
   }
   try {
    const response=await axiosInstance.get("/api/admin/details");
    return response.data;
   } catch (error) {
    throw error;
   }
}
