import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";
import { PURGE } from "redux-persist";

export interface userInfoConfig {
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
  plannerAccessScope: string;
}

interface authConfig {
  accessToken: string;
  userId: number;
  login: boolean;
  userInfo: userInfoConfig;
}

const initialState: authConfig = {
  accessToken: "",
  userId: 0,
  login: false,
  userInfo: {
    email: "",
    nickname: "",
    profileImage: "",
    statusMessage: "",
    plannerAccessScope: "",
  },
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setLogin: (state, { payload }: PayloadAction<{ accessToken: string; userId: number }>) => {
      state.login = true;
      state.userId = payload.userId;
      state.accessToken = payload.accessToken;
    },
    setLogout: (state) => {
      state.login = false;
      localStorage.removeItem("id");
      localStorage.removeItem("accessToken");
    },
    setUserInfo: (state, { payload }: PayloadAction<userInfoConfig>) => {
      const [statusMessage, profileImage] = [payload.statusMessage || "", payload.profileImage || ""];
      state.userInfo = { ...payload, statusMessage, profileImage };
    },
  },
  extraReducers: (builder) => {
    // redux-persist 초기화
    builder.addCase(PURGE, () => initialState);
  },
});

export const { setLogin, setLogout, setUserInfo } = authSlice.actions;
export const selectUserInfo = (state: rootState) => state.auth.userInfo;
export const selectLoginState = (state: rootState) => state.auth.login;
export const selectAccessToken = (state: rootState) => state.auth.accessToken;
export const selectUserId = (state: rootState) => state.auth.userId;
export default authSlice.reducer;
