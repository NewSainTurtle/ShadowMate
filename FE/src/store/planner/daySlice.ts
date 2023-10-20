import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";
import { CategoryConfig, DayTodoConfig, TimeTableConfig } from "@util/planner.interface";
import { todoData_list } from "@util/data/DayTodos";
import dayjs from "dayjs";

export interface todoType extends DayTodoConfig {
  category: CategoryConfig;
  timeTable: TimeTableConfig;
}

interface dayConfig {
  date: string | Date | dayjs.Dayjs;
  todoItem: todoType;
  todoList: todoType[];
}

const initialState: dayConfig = {
  date: new Date(),
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
    timeTable: {
      timeTableId: 0,
      startTime: "",
      endTime: "",
    },
  },
  todoList: todoData_list,
};

const daySlice = createSlice({
  name: "plannerDay",
  initialState,
  reducers: {
    setDate: (state, action: PayloadAction<dayConfig["date"]>) => {
      state.date = action.payload;
    },
    setTodo: (state, action: PayloadAction<dayConfig["todoItem"]>) => {
      state.todoItem = action.payload;
    },
    removeTodo: (state) => {
      state.todoItem = initialState.todoItem;
    },
    setTodoList: (state, action: PayloadAction<dayConfig["todoList"]>) => {
      state.todoList = action.payload;
    },
  },
});

export const BASIC_TODO_ITEM = initialState.todoItem;
export const { setDate, setTodo, removeTodo, setTodoList } = daySlice.actions;
export const selectDate = (state: rootState) => state.day.date;
export const selectTodoItem = (state: rootState) => state.day.todoItem;
export const selectTodoList = (state: rootState) => state.day.todoList;

export default daySlice.reducer;
