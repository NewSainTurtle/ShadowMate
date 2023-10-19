import { rootState } from "@hooks/configStore";
import { createSlice } from "@reduxjs/toolkit";
import { WeekTodoItemConfig, categoryType } from "@util/planner.interface";

interface DailyTodoConfig {
  todoId: number;
  category: categoryType;
  todoContent: string;
  todoStatus: string;
  todoUpdate?: boolean;
}

interface DayListConfig {
  date: string;
  retrospection: string;
  dailyTodo: DailyTodoConfig[];
}

interface WeekConfig {
  weekInfo: {
    plannerAccessScope: string;
    dday: string;
    weeklyTodo?: WeekTodoItemConfig[];
    dayList: DayListConfig[];
  };
}

const initialState: WeekConfig = {
  weekInfo: {
    plannerAccessScope: "",
    dday: "",
    weeklyTodo: [],
    dayList: [],
  },
};

const weekSlice = createSlice({
  name: "week",
  initialState,
  reducers: {
    setWeekInfo: (state, { payload: { weekInfo } }) => {
      state.weekInfo = weekInfo;
    },
  },
});

export const { setWeekInfo } = weekSlice.actions;
export const selectWeekInfo = (state: rootState) => state.week.weekInfo;

export default weekSlice.reducer;
