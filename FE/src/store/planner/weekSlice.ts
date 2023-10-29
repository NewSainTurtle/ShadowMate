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
}

const initialState: WeekConfig = {
  plannerAccessScope: "",
  dday: "",
  weeklyTodos: [],
  dayList: [],
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
    setWeeklyTodos: (state, { payload }: PayloadAction<WeekTodoItemConfig[]>) => {
      state.weeklyTodos = payload;
    },
  },
});

export const { setWeekInfo, setWeeklyTodos } = weekSlice.actions;
export const selectDayList = (state: rootState) => state.week.dayList;
export const selectWeeklyTodos = (state: rootState) => state.week.weeklyTodos;

export default weekSlice.reducer;
