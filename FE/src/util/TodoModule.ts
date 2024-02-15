import { TodoConfig } from "@util/planner.interface";
import { Dispatch, SetStateAction, useMemo } from "react";

function todoModule(todos: TodoConfig[], setTodos: Dispatch<SetStateAction<TodoConfig[]>>) {
  const copyTodos = useMemo(() => [...todos], [todos]);

  const insertTodo = (props: TodoConfig) => {
    setTodos([...todos, { ...props }]);
  };

  const updateTodo = (idx: number, props: TodoConfig) => {
    copyTodos[idx] = { ...props };
    setTodos(copyTodos);
  };

  const deleteTodo = (idx: number) => {
    copyTodos.splice(idx, 1);
    setTodos(copyTodos);
  };

  return {
    insertTodo,
    updateTodo,
    deleteTodo,
  };
}

export default todoModule;
