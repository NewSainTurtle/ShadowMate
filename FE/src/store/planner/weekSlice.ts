import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { DayTodoConfig, WeekTodoItemConfig, CategoryConfig } from "@util/planner.interface";

export interface DailyTodoConfig {
  todoId: number;
  category?: CategoryConfig;
  todoContent: string;
  todoStatus: string;
  todoUpdate?: boolean;
}

export interface DayListConfig {
  date: string;
  retrospection: string | null;
  dailyTodo: DayTodoConfig[] | null;
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
      console.log("날짜 갱신: ", payload);
      state.thisWeek = payload;
    },
    setWeeklyTodos: (state, { payload }: PayloadAction<WeekTodoItemConfig[]>) => {
      state.weeklyTodos = payload;
    },
  },
});

export const { setWeekInfo, setWeeklyTodos, setThisWeek } = weekSlice.actions;
export const selectDayList = (state: rootState) => state.week.dayList;
export const selectWeeklyTodos = (state: rootState) => state.week.weeklyTodos;
export const selectThisWeek = (state: rootState) => state.week.thisWeek;

export default weekSlice.reducer;
