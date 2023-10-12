import { createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";

interface dayConfig {
  todo: todoConfig;
}

interface todoConfig {
  todoId: number;
  categoryColorCode: string;
  categoryTitle: string;
  todoContent: string;
  todoStatus: 0 | 1 | 2;
}

const initialState: dayConfig = {
  todo: {
    todoId: 0,
    categoryTitle: "",
    categoryColorCode: "#E9E9EB",
    todoContent: "",
    todoStatus: 0,
  },
};

const daySlice = createSlice({
  name: "plannerDay",
  initialState,
  reducers: {
    setTodo: (state, { payload: todo }) => {
      state.todo = todo;
      console.log(todo);
    },
  },
});

export const { setTodo } = daySlice.actions;
export const selectTodo = (state: rootState) => state.day.todo;

export default daySlice.reducer;
