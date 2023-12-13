import baseAxios from "axios";
import { store } from "@hooks/configStore";
import { setModalOpen } from "@store/modalSlice";
import { setAlertOpen } from "@store/alertSlice";

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
    if (res.status === 202) {
      store.dispatch(setAlertOpen({ type: "success", message: res.data.message }));
    }
    return res;
  },
  (err) => {
    if (err.response.data.code === "FAIL_VALIDATE_TOKEN") {
      store.dispatch(setModalOpen());
    }
    return Promise.reject(err);
  },
);

export default Axios;
