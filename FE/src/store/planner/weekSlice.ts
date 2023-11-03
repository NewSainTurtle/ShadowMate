import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { TodoConfig, WeekTodoItemConfig } from "@util/planner.interface";

export interface DayListConfig {
  date: string;
  retrospection: string | null;
  dailyTodos: TodoConfig[] | null;
}

export interface WeekConfig {
  plannerAccessScope: string;
  dday: string;
  weeklyTodos: WeekTodoItemConfig[];
  dayList: DayListConfig[];
  thisWeek: Date | string;
}

const initialState: WeekConfig = {
  plannerAccessScope: "",
  dday: "",
  weeklyTodos: [],
  dayList: [],
  thisWeek: new Date(),
};

const weekSlice = createSlice({
  name: "week",
  initialState,
  reducers: {
    setWeekInfo: (state, { payload }: PayloadAction<WeekConfig>) => {
      state.plannerAccessScope = payload.plannerAccessScope;
      state.dday = payload.dday;
      state.weeklyTodos = payload.weeklyTodos;
      state.dayList = payload.dayList;
    },
    setThisWeek: (state, { payload }: PayloadAction<Date>) => {
      state.thisWeek = payload;
    },
    setWeeklyTodos: (state, { payload }: PayloadAction<WeekTodoItemConfig[]>) => {
      state.weeklyTodos = payload;
    },
    setDayList: (state, { payload }: PayloadAction<DayListConfig[]>) => {
      state.dayList = payload;
    },
  },
});

export const { setWeekInfo, setWeeklyTodos, setThisWeek, setDayList } = weekSlice.actions;
export const selectDayList = (state: rootState) => state.week.dayList;
export const selectWeeklyTodos = (state: rootState) => state.week.weeklyTodos;
export const selectDailyTodos = (state: rootState) => state.week.dayList.dailyTodos;
export const selectThisWeek = (state: rootState) => state.week.thisWeek;
export const selectWeekDday = (state: rootState) => state.week.dday;

export default weekSlice.reducer;
