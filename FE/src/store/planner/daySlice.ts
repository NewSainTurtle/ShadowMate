import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";
import { CategoryItemConfig, TimeTableConfig, TodoConfig } from "@util/planner.interface";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import isSameOrAfter from "dayjs/plugin/isSameOrAfter";
dayjs.extend(isSameOrBefore);
dayjs.extend(isSameOrAfter);

interface DayInfoConfig {
  plannerAccessScope: "전체공개" | "친구공개" | "비공개";
  dday: string | null;
  ddayTitle: string;
  like: boolean;
  likeCount: number;
  shareSocial: number;
  dailyTodos: TodoConfig[];
}

interface DayConfig {
  date: string;
  info: DayInfoConfig;
  todoItem: TodoConfig;
}

const initialState: DayConfig = {
  date: dayjs().format("YYYY-MM-DD"),
  info: {
    plannerAccessScope: "전체공개",
    dday: null,
    ddayTitle: "",
    like: false,
    likeCount: 0,
    shareSocial: 0,
    dailyTodos: [],
  },
  todoItem: {
    todoId: 0,
    todoContent: "",
    todoStatus: "공백",
    category: {
      categoryId: 0,
      categoryTitle: "",
      categoryColorCode: "#E9E9EB",
      categoryEmoticon: "",
    },
    timeTables: [],
  },
};

const daySlice = createSlice({
  name: "plannerDay",
  initialState,
  reducers: {
    setDayDate: (state, action: PayloadAction<DayConfig["date"]>) => {
      state.date = action.payload;
    },
    setDayInfo: (state, action: PayloadAction<DayConfig["info"]>) => {
      state.info = action.payload;
    },
    setDayLike: (state, action: PayloadAction<boolean>) => {
      const isLike = action.payload;
      if (isLike) state.info.likeCount += 1;
      else state.info.likeCount -= 1;
      state.info.like = isLike;
    },
    setTodoItem: (state, action: PayloadAction<DayConfig["todoItem"]>) => {
      state.todoItem = action.payload;
    },
    setTodoList: (state, action: PayloadAction<DayInfoConfig["dailyTodos"]>) => {
      state.info.dailyTodos = action.payload;
    },
    setTimeTable: (
      state,
      action: PayloadAction<{
        todoId: number;
        timeTableId: number;
        startTime?: string;
        endTime?: string;
        todoStatus?: TodoConfig["todoStatus"];
      }>,
    ) => {
      const { todoId, timeTableId, startTime, endTime } = action.payload;

      const tempArr = state.info.dailyTodos;
      const findTodoIndex = tempArr.findIndex((item) => item.todoId == todoId);
      const todoInfo = tempArr[findTodoIndex] as TodoConfig;
      const todoStatus = action.payload.todoStatus || todoInfo.todoStatus;
      const todoTimetable = (() => {
        const tempTimeTable = todoInfo.timeTables ?? [];
        if (startTime) return [...tempTimeTable, { timeTableId, startTime, endTime }];
        else {
          const findTimeIndex = tempTimeTable?.findIndex((item) => item.timeTableId === timeTableId);
          tempTimeTable?.splice(findTimeIndex, 1);
          return tempTimeTable;
        }
      })() as TimeTableConfig[];

      tempArr[findTodoIndex] = {
        ...todoInfo,
        todoStatus,
        timeTables: todoTimetable,
      };

      state.info.dailyTodos = tempArr;
      state.todoItem = initialState.todoItem;
    },
  },
});

export const BASIC_TODO_ITEM = initialState.todoItem;
export const BASIC_CATEGORY_ITEM = initialState.todoItem.category as CategoryItemConfig;
export const { setDayDate, setDayInfo, setDayLike, setTodoItem, setTodoList, setTimeTable } = daySlice.actions;
export const selectDayDate = (state: rootState) => state.day.date;
export const selectDayInfo = (state: rootState) => state.day.info;
export const selectTodoItem = (state: rootState) => state.day.todoItem;
export const selectTodoList = (state: rootState) => state.day.info.dailyTodos;

export default daySlice.reducer;
