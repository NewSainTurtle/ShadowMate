import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";
import dayjs from "dayjs";

interface todoConfig {
  todoId: number;
  categoryColorCode: string;
  categoryTitle: string;
  todoContent: string;
  todoStatus: 0 | 1 | 2;
}

interface doneTimeConfig {
  startTime: string;
  endTime: string;
}

interface dayConfig {
  date: string | Date | dayjs.Dayjs;
  todo: todoConfig;
  doneTodoList: { todo: todoConfig; time: doneTimeConfig }[];
}

const initialState: dayConfig = {
  date: new Date(),
  todo: {
    todoId: 0,
    categoryTitle: "",
    categoryColorCode: "#E9E9EB",
    todoContent: "",
    todoStatus: 0,
  },
  doneTodoList: [],
};

const daySlice = createSlice({
  name: "plannerDay",
  initialState,
  reducers: {
    setDate: (state, action: PayloadAction<dayConfig["date"]>) => {
      state.date = action.payload;
    },
    setTodo: (state, action: PayloadAction<dayConfig["todo"]>) => {
      state.todo = action.payload;
    },
    removeTodo: (state) => {
      state.todo = initialState.todo;
    },
    setDoneTodoList: (state, action: PayloadAction<todoConfig[]>) => {
      state.doneTodoList = action.payload
        .filter((todo) => todo.todoStatus == 1)
        .map((todo) => ({ todo, time: { startTime: "", endTime: "" } }));
    },
    setDoneTodoTime: (state, action: PayloadAction<{ index: number; time: doneTimeConfig }>) => {
      const changedIdx = action.payload.index;
      state.doneTodoList[changedIdx].time = action.payload.time;
    },
  },
});

export const { setDate, setTodo, removeTodo, setDoneTodoList, setDoneTodoTime } = daySlice.actions;
export const selectDate = (state: rootState) => state.day.date;
export const selectTodo = (state: rootState) => state.day.todo;

export default daySlice.reducer;
