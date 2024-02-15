import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";
import dayjs from "dayjs";
import { ProfileConfig } from "@util/auth.interface";
import { FollowingType } from "@util/friend.interface";

interface FriendConfig {
  followState: number;
  friendDate: string;
  friendInfo: ProfileConfig;
  followingList: FollowingType[];
}

const initialState: FriendConfig = {
  followState: 0,
  friendDate: dayjs().format("YYYY-MM-DD"),
  friendInfo: {
    userId: 0,
    nickname: "",
    profileImage: "",
    statusMessage: "",
  },
  followingList: [],
};

const friendSlice = createSlice({
  name: "friend",
  initialState,
  reducers: {
    setFriendDate: (state, { payload }: PayloadAction<FriendConfig["friendDate"]>) => {
      state.friendDate = payload;
    },
    setFriendInfo: (state, { payload }: PayloadAction<ProfileConfig>) => {
      const [statusMessage, profileImage] = [payload.statusMessage || "", payload.profileImage || ""];
      state.friendInfo = { ...payload, statusMessage, profileImage };
    },
    setFollowState: (state, { payload }: PayloadAction<FriendConfig["followState"]>) => {
      state.followState = payload;
    },
    setFollowingList: (state, { payload }: PayloadAction<FollowingType[]>) => {
      state.followingList = payload;
    },
    clearFriendInfo: (state) => {
      state.friendInfo = initialState.friendInfo;
    },
    clearFollowingList: (state) => {
      state.followingList = initialState.followingList;
    },
  },
});

export const { setFriendDate, setFriendInfo, setFollowState, clearFriendInfo, setFollowingList, clearFollowingList } =
  friendSlice.actions;
export const selectFriendDate = (state: rootState) => state.friend.friendDate;
export const selectFriendInfo = (state: rootState) => state.friend.friendInfo;
export const selectFriendId = (state: rootState) => state.friend.friendInfo.userId;
export const selectFollowState = (state: rootState) => state.friend.followState;
export const selectFollowingList = (state: rootState) => state.friend.followingList;
export default friendSlice.reducer;
