import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";

interface userInfoConfig {
  userId: number;
  email: string;
  nickname: string;
  profile: string;
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
      state.userInfo.userId = payload.userId;
      sessionStorage.setItem("accessToken", payload.accessToken);
    },
    setLogout: (state) => {
      state.accessToken = "";
      state.login = false;
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

export default authSlice.reducer;
