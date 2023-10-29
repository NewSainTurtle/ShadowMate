import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";

interface userInfoConfig {
  userId: number;
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
  plannerAccessScope: string;
}

interface authConfig {
  accessToken: string;
  login: boolean;
  userInfo: userInfoConfig;
}

const initialState: authConfig = {
  accessToken: "",
  login: false,
  userInfo: {
    userId: 0,
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
      state.accessToken = payload.accessToken;
      state.login = true;
      state.userInfo.userId = payload.userId;
      sessionStorage.setItem("accessToken", payload.accessToken);
    },
    setLogout: (state) => {
      state = initialState;
      sessionStorage.clear();
    },
    setUserInfo: (
      state,
      {
        payload,
      }: PayloadAction<{
        email: string;
        nickname: string;
        profileImage: string | null;
        statusMessage: string | null;
        plannerAccessScope: string;
      }>,
    ) => {
      const { email, nickname, profileImage, statusMessage, plannerAccessScope } = payload;
      state.userInfo = {
        ...state.userInfo,
        email,
        nickname,
        profileImage: profileImage || "",
        statusMessage: statusMessage || "",
        plannerAccessScope,
      };
    },
  },
});

export const { setLogin, setLogout, setUserInfo } = authSlice.actions;
export const selectUserInfo = (state: rootState) => state.auth.userInfo;
export const selectLoginState = (state: rootState) => state.auth.login;
export const selectAccessToken = (state: rootState) => state.auth.accessToken;

export default authSlice.reducer;
