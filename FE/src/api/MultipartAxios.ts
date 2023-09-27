import baseAxios from "axios";

export const baseURL = process.env.REACT_APP_API_URL;

const Axios = baseAxios.create({
  baseURL,
  headers: {
    "content-type": "multipart/form-data",
  },
});

Axios.interceptors.request.use((config) => {
  const accessToken = sessionStorage.getItem("accessToken");
  config.headers.Authorization = accessToken ? `Bearer ${accessToken}` : "";
  return config;
});

export default Axios;
