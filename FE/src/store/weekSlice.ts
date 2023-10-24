import { rootState } from "@hooks/configStore";
import { createSlice } from "@reduxjs/toolkit";
import { TodoConfig, WeekTodoItemConfig, categoryType } from "@util/planner.interface";

export interface DailyTodoConfig {
  todoId: number;
  category?: categoryType | null;
  todoContent: string;
  todoStatus: string;
  todoUpdate?: boolean;
}

export interface DayListConfig {
  date: string;
  retrospection: string;
  dailyTodo: TodoConfig[];
}

export interface WeekConfig {
  plannerAccessScope: string;
  dday: string;
  weeklyTodo?: WeekTodoItemConfig[];
  dayList: DayListConfig[];
}

const initialState: WeekConfig = {
  plannerAccessScope: "",
  dday: "",
  weeklyTodo: [],
  dayList: [],
};

const weekSlice = createSlice({
  name: "week",
  initialState,
  reducers: {
    setWeekInfo: (state, { payload: { plannerAccessScope, dday, weeklyTodo, dayList } }) => {
      state.plannerAccessScope = plannerAccessScope;
      state.dday = dday;
      state.weeklyTodo = weeklyTodo;
      state.dayList = dayList;
    },
  },
});

export const { setWeekInfo } = weekSlice.actions;
export const selectDayList = (state: rootState) => state.week.dayList;

export default weekSlice.reducer;
