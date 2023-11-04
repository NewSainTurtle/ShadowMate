import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";
import { userInfoConfig } from "./authSlice";

interface friendInfoConfig extends userInfoConfig {
  userId: number;
}

interface friendConfig {
  friendInfo: friendInfoConfig;
  followState: number;
}

const initialState: friendConfig = {
  followState: 0,
  friendInfo: {
    userId: 0,
    email: "",
    nickname: "",
    profileImage: "",
    statusMessage: "",
    plannerAccessScope: "",
  },
};

const friendSlice = createSlice({
  name: "friend",
  initialState,
  reducers: {
    setFriendInfo: (state, { payload }: PayloadAction<friendInfoConfig>) => {
      const [statusMessage, profileImage] = [payload.statusMessage || "", payload.profileImage || ""];
      state.friendInfo = { ...payload, statusMessage, profileImage };
    },
    setFollowState: (state, { payload }: PayloadAction<friendConfig["followState"]>) => {
      state.followState = payload;
    },
  },
});

export const { setFriendInfo, setFollowState } = friendSlice.actions;
export const selectFriendInfo = (state: rootState) => state.friend.friendInfo;
export const selectFollowState = (state: rootState) => state.friend.followState;
export default friendSlice.reducer;
