import baseAxios, { AxiosError, AxiosResponse } from "axios";
import { store } from "@hooks/configStore";
import { authApi } from "@api/Api";
import { setAccessToken, setAutoLogin } from "@store/authSlice";
import { setModalOpen } from "@store/modalSlice";
import { setAlertOpen } from "@store/alertSlice";
import axios from "axios";

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
    if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`;
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
  (response: AxiosResponse): AxiosResponse => {
    const { status } = response;
    const message = response.data as string;

    if (status === 202) {
      store.dispatch(setAlertOpen({ type: "success", message }));
    }

    return response;
  },
  async (error: AxiosError | Error): Promise<AxiosError> => {
    if (baseAxios.isAxiosError(error)) {
      const response = error.response as AxiosResponse;
      const { status } = response;
      if (status === 401) {
        if (response.data.code === "EXPIRED_ACCESS_TOKEN") {
          const { userId, type } = store.getState().auth;
          const res = await authApi.token(userId, { type });
          if (res.status === 200) {
            const headers = res.headers["authorization"] as string;
            const accessToken = headers.replace("Bearer ", "");
            store.dispatch(setAccessToken(accessToken));
            if (error?.config !== undefined) {
              error.config.headers.Authorization = `Bearer ${accessToken}`;
              return Axios(error.config);
            }
          }
        } else if (response.data.code === "EXPIRED_REFRESH_TOKEN") {
          store.dispatch(setModalOpen());
        }
      }
    }

    return Promise.reject(error);
  },
);

export default Axios;
