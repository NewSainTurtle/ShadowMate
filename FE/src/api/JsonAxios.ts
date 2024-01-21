import baseAxios from "axios";
import { store } from "@hooks/configStore";
import { authApi } from "@api/Api";
import { setAccessToken, setAutoLogin } from "@store/authSlice";
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
    const isAutoLogin = store.getState().auth.autoLogin;
    if (accessToken) config.headers.Authorization = accessToken ?? "";
    if (isAutoLogin) {
      config.headers["Auto-Login"] = "true";
      store.dispatch(setAutoLogin(false));
    }

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
  async (err) => {
    const {
      config,
      response: { status },
    } = err;

    if (status === 401) {
      if (err.response.data.code === "EXPIRED_ACCESS_TOKEN") {
        const userId = store.getState().auth.userId;
        const type = store.getState().auth.type;
        const res = await authApi.token(userId, type);
        if (res.status === 200) {
          const accessToken = res.headers["authorization"].replace("Bearer ", "");
          store.dispatch(setAccessToken(accessToken));
          config.headers.Authorization = `Bearer ${accessToken}`;
          return Axios(config);
        }
      } else if (err.response.data.code === "EXPIRED_REFRESH_TOKEN") {
        store.dispatch(setModalOpen());
      }
    }
    return Promise.reject(err);
  },
);

export default Axios;
