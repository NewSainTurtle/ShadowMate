import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { TodoConfig, WeekTodoItemConfig, CategoryConfig } from "@util/planner.interface";

export interface DailyTodoConfig {
  todoId: number;
  category?: CategoryConfig | null;
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
    setWeekInfo: (state, { payload }: PayloadAction<WeekConfig>) => {
      state.plannerAccessScope = payload.plannerAccessScope;
      state.dday = payload.dday;
      state.weeklyTodo = payload.weeklyTodo;
      state.dayList = payload.dayList;
    },
  },
});

export const { setWeekInfo } = weekSlice.actions;
export const selectDayList = (state: rootState) => state.week.dayList;

export default weekSlice.reducer;
