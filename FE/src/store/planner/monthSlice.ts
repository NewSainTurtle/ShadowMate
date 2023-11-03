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
}

const initialState: MonthConfig = {
  plannerAccessScope: "전체공개",
  dayList: [],
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
  },
});

export const { setMonthInfo, setPlannerAccessScope } = monthSlice.actions;
export const selectMonthDayList = (state: rootState) => state.month.dayList;
export const selectPlannerAccessScope = (state: rootState) => state.month.plannerAccessScope;

export default monthSlice.reducer;
