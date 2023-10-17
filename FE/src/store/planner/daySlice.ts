import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";

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
  todo: todoConfig;
  doneTodoList: { todo: todoConfig; time: doneTimeConfig }[];
}

const initialState: dayConfig = {
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

export const { setTodo, removeTodo, setDoneTodoList, setDoneTodoTime } = daySlice.actions;
export const selectTodo = (state: rootState) => state.day.todo;

export default daySlice.reducer;
