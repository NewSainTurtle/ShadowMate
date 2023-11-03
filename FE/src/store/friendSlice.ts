import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";

interface friendConfig {
  followState: number;
}

const initialState: friendConfig = {
  followState: 0,
};

const friendSlice = createSlice({
  name: "friend",
  initialState,
  reducers: {
    setFollowState: (state, { payload }: PayloadAction<friendConfig>) => {
      state.followState = payload.followState;
    },
  },
});

export const { setFollowState } = friendSlice.actions;
export const selectFollowState = (state: rootState) => state.friend.followState;
export default friendSlice.reducer;
