import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";

interface userInfoConfig {
  email: string;
  nickname: string;
  profile: string;
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
    profile: "",
  },
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setLogin: (state, { payload }: PayloadAction<{ accessToken: string; userId: number }>) => {
      state.accessToken = payload.accessToken;
      state.login = true;
      state.userId = payload.userId;
      sessionStorage.setItem("accessToken", payload.accessToken);
    },
    setLogout: (state) => {
      state.accessToken = "";
      state.login = false;
      state.userId = 0;
      state.userInfo.email = "";
      state.userInfo.nickname = "";
      state.userInfo.profile = "";
      sessionStorage.clear();
    },
    setUserInfo: (state, { payload }: PayloadAction<{ email: string; nickname: string; profile: string }>) => {
      state.userInfo.email = payload.email;
      state.userInfo.nickname = payload.nickname;
      state.userInfo.profile = payload.profile;
    },
  },
});

export const { setLogin, setLogout, setUserInfo } = authSlice.actions;
export const selectUserInfo = (state: rootState) => state.auth.userInfo;
export const selectLoginState = (state: rootState) => state.auth.login;
export const selectAccessToken = (state: rootState) => state.auth.accessToken;
export const selectUserId = (state: rootState) => state.auth.userId;
export default authSlice.reducer;
