import { TodoConfig } from "@util/planner.interface";
import { Dispatch, SetStateAction } from "react";

interface Props {
  todos: TodoConfig[];
  setTodos: Dispatch<SetStateAction<TodoConfig[]>>;
}

const todoModule = (todos: TodoConfig[], setTodos: Dispatch<SetStateAction<TodoConfig[]>>) => {
  const insertTodo = (props: TodoConfig) => {
    setTodos([...todos, { ...props }]);
  };

  const updateTodo = (idx: number, props: TodoConfig) => {
    let copyTodos = [...todos];
    copyTodos[idx] = { ...props };
    setTodos(copyTodos);
  };

  const deleteTodo = (idx: number) => {
    let newTodos = todos.filter((item, i) => idx != i);
    setTodos(newTodos);
  };

  return {
    insertTodo,
    updateTodo,
    deleteTodo,
  };
};

export default todoModule;
