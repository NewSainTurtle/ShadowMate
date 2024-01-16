import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";

export interface SocialConfig {
  sort: "latest" | "popularity";
  keyWord: string;
  dateRange: {
    startDate: string | Date;
    endDate: string | Date;
  };
}

const initialState: SocialConfig = {
  sort: "latest",
  keyWord: "",
  dateRange: {
    startDate: "",
    endDate: "",
  },
};

const socialSlice = createSlice({
  name: "social",
  initialState,
  reducers: {
    setSocialSort: (state, { payload }: PayloadAction<SocialConfig["sort"]>) => {
      state.sort = payload;
    },
    setSocialKeyWord: (state, { payload }: PayloadAction<string>) => {
      state.keyWord = payload;
    },
    setSocialDateRange: (state, { payload }: PayloadAction<SocialConfig["dateRange"]>) => {
      state.dateRange = payload;
    },
    setSocialClear: (state) => {
      state.sort = initialState.sort;
      state.keyWord = initialState.keyWord;
      state.dateRange = initialState.dateRange;
    },
  },
});

export const { setSocialSort, setSocialKeyWord, setSocialDateRange, setSocialClear } = socialSlice.actions;

export const selectSocialSort = (state: rootState) => state.social.sort;
export const selectSocialKeyword = (state: rootState) => state.social.keyWord;
export const selectSocialDateRange = (state: rootState) => state.social.dateRange;

export default socialSlice.reducer;
