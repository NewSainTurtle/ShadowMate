import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";
import { ProfileConfig } from "@components/common/FriendProfile";
import dayjs from "dayjs";

interface friendConfig {
  followState: number;
  friendDate: string;
  friendInfo: ProfileConfig;
}

const initialState: friendConfig = {
  followState: 0,
  friendDate: dayjs().format("YYYY-MM-DD"),
  friendInfo: {
    userId: 0,
    nickname: "",
    profileImage: "",
    statusMessage: "",
  },
};

const friendSlice = createSlice({
  name: "friend",
  initialState,
  reducers: {
    setFriendDate: (state, { payload }: PayloadAction<friendConfig["friendDate"]>) => {
      state.friendDate = payload;
    },
    setFriendInfo: (state, { payload }: PayloadAction<ProfileConfig>) => {
      const [statusMessage, profileImage] = [payload.statusMessage || "", payload.profileImage || ""];
      state.friendInfo = { ...payload, statusMessage, profileImage };
    },
    setFollowState: (state, { payload }: PayloadAction<friendConfig["followState"]>) => {
      state.followState = payload;
    },
    clearFriendInfo: (state) => {
      state.friendInfo = initialState.friendInfo;
    },
  },
});

export const { setFriendDate, setFriendInfo, setFollowState, clearFriendInfo } = friendSlice.actions;
export const selectFriendDate = (state: rootState) => state.friend.friendDate;
export const selectFriendInfo = (state: rootState) => state.friend.friendInfo;
export const selectFriendId = (state: rootState) => state.friend.friendInfo.userId;
export const selectFollowState = (state: rootState) => state.friend.followState;
export default friendSlice.reducer;
