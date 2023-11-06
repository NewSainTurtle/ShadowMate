import baseAxios from "axios";
import { store } from "@hooks/configStore";
import { setModalOpen } from "@store/modalSlice";

export const baseURL = process.env.REACT_APP_API_URL;

const Axios = baseAxios.create({
  baseURL: baseURL,
  headers: {
    "content-type": "application/json",
  },
});

Axios.interceptors.request.use(
  (config) => {
    const accessToken = store.getState().auth.accessToken;
    if (accessToken) config.headers.Authorization = accessToken ? `Bearer ${accessToken}` : "";
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

Axios.interceptors.response.use(
  (res) => {
    return res;
  },
  (err) => {
    if (err.response.status === 403) {
      store.dispatch(setModalOpen());
    }
    return Promise.reject(err);
  },
);

export default Axios;
