import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";

export interface MonthDayConfig {
  date: string;
  todoCount: number;
  dayStatus: number;
}

export interface MonthConfig {
  plannerAccessScope: "전체공개" | "친구공개" | "비공개";
  dayList: MonthDayConfig[];
  statistics: {
    plannerLikeCount: number;
    todoComplete: number;
    todoIncomplete: number;
    todoTotal: number;
  };
  followCount: {
    followerCount: number;
    followingCount: number;
  };
}

const initialState: MonthConfig = {
  plannerAccessScope: "전체공개",
  dayList: [],
  statistics: {
    plannerLikeCount: 0,
    todoComplete: 0,
    todoIncomplete: 0,
    todoTotal: 0,
  },
  followCount: {
    followerCount: 0,
    followingCount: 0,
  },
};

const monthSlice = createSlice({
  name: "month",
  initialState,
  reducers: {
    setMonthInfo: (
      state,
      { payload }: PayloadAction<{ plannerAccessScope: MonthConfig["plannerAccessScope"]; dayList: MonthDayConfig[] }>,
    ) => {
      state.plannerAccessScope = payload.plannerAccessScope;
      state.dayList = payload.dayList;
    },
    setPlannerAccessScope: (state, { payload }: PayloadAction<MonthConfig["plannerAccessScope"]>) => {
      state.plannerAccessScope = payload;
    },
    setStatistics: (state, { payload }: PayloadAction<MonthConfig["statistics"]>) => {
      state.statistics = payload;
    },
    setFollowCount: (state, { payload }: PayloadAction<MonthConfig["followCount"]>) => {
      state.followCount = payload;
    },
  },
});

export const { setMonthInfo, setPlannerAccessScope, setStatistics, setFollowCount } = monthSlice.actions;
export const selectMonthDayList = (state: rootState) => state.month.dayList;
export const selectPlannerAccessScope = (state: rootState) => state.month.plannerAccessScope;
export const selectStatistics = (state: rootState) => state.month.statistics;
export const selectFollowCount = (state: rootState) => state.month.followCount;

export default monthSlice.reducer;
