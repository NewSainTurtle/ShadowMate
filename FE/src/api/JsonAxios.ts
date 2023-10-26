import baseAxios from "axios";

export const baseURL = process.env.REACT_APP_API_URL;

const Axios = baseAxios.create({
  baseURL: baseURL,
  headers: {
    "content-type": "application/json",
  },
});

Axios.interceptors.request.use((config) => {
  const accessToken = sessionStorage.getItem("accessToken");
  config.headers.Authorization = accessToken ? `Bearer ${accessToken}` : "";
  return config;
});

Axios.interceptors.response.use(
  (res) => {
    return res;
  },
  (err) => {
    return Promise.reject(err);
  },
);

export default Axios;
