import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";
import { PURGE } from "redux-persist";

export interface UserInfoConfig {
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
  plannerAccessScope: string;
}

interface AuthConfig {
  accessToken: string;
  userId: number;
  login: boolean;
  isGoogle: boolean;
  userInfo: UserInfoConfig;
  type: string;
  autoLogin?: boolean;
}

const initialState: AuthConfig = {
  accessToken: "",
  userId: 0,
  login: false,
  isGoogle: false,
  userInfo: {
    email: "",
    nickname: "",
    profileImage: "",
    statusMessage: "",
    plannerAccessScope: "",
  },
  type: "",
  autoLogin: false,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setLogin: (state, { payload }: PayloadAction<{ accessToken: string; userId: number; type: string }>) => {
      state.login = true;
      state.userId = payload.userId;
      state.accessToken = payload.accessToken;
      state.type = payload.type;
    },
    setAutoLogin: (state, { payload }: PayloadAction<boolean>) => {
      state.autoLogin = payload;
    },
    setIsGoogle: (state, { payload }: PayloadAction<boolean>) => {
      state.isGoogle = payload;
    },
    setLogout: (state) => {
      state.login = false;
    },
    setUserInfo: (state, { payload }: PayloadAction<UserInfoConfig>) => {
      const [statusMessage, profileImage] = [payload.statusMessage || "", payload.profileImage || ""];
      state.userInfo = { ...payload, statusMessage, profileImage };
    },
    setAccessToken: (state, { payload }: PayloadAction<string>) => {
      state.accessToken = payload;
    },
  },
  extraReducers: (builder) => {
    // redux-persist 초기화
    builder.addCase(PURGE, () => initialState);
  },
});

export const { setLogin, setAutoLogin, setLogout, setUserInfo, setIsGoogle, setAccessToken } = authSlice.actions;
export const selectUserInfo = (state: rootState) => state.auth.userInfo;
export const selectLoginState = (state: rootState) => state.auth.login;
export const selectAutoLogin = (state: rootState) => state.auth.autoLogin;
export const selectAccessToken = (state: rootState) => state.auth.accessToken;
export const selectUserId = (state: rootState) => state.auth.userId;
export const selectIsGoogle = (state: rootState) => state.auth.isGoogle;
export const selectPlannerAccessScope = (state: rootState) => state.auth.userInfo.plannerAccessScope;
export const selectType = (state: rootState) => state.auth.type;

export default authSlice.reducer;
