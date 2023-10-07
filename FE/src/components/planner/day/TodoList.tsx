import React, { useEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import TodoItem from "@components/planner/day/TodoItem";
import { todoType } from "@util/planner.interface";
import { todoData_list } from "@util/data/DayTodos";

const TodoList = () => {
  const [todos, setTodos] = useState<todoType[]>(todoData_list);
  // const [isAddIconView, setIsAddIconView] = useState(todos.length != 0);
  const todoListSize = useMemo(() => {
    return todos.length + 1 >= 12 ? todos.length + 1 : 12;
  }, [todos]);
  const todoEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (todos.length + 1 >= 12 && todoEndRef.current) {
      todoEndRef.current.scrollTop = todoEndRef.current.scrollHeight;
    }
  }, [todos]);

  const todoModule = (() => {
    const insertTodo = (props: todoType) => {
      setTodos([...todos, { ...props }]);
    };

    const updateTodo = (idx: number, props: todoType) => {
      let copyTodos = [...todos];
      copyTodos[idx] = { ...props };
      setTodos(copyTodos);
    };

    const deleteTodo = (idx: number) => {
      let copyTodos = [...todos];
      copyTodos.splice(idx, 1);
      setTodos(copyTodos);
    };

    return {
      insertTodo: insertTodo,
      updateTodo: updateTodo,
      deleteTodo: deleteTodo,
    };
  })();

  return (
    <div
      ref={todoEndRef}
      className={styles["todo-list"]}
      style={{ gridTemplateRows: `repeat(${todoListSize}, calc(100%/12)` }}
    >
      {todos.map((todo, idx) => (
        <TodoItem key={todo.todoId} idx={idx} item={todo} todoModule={todoModule} />
      ))}
      <TodoItem addTodo todoModule={todoModule} />
      {[...Array(11 - todos.length)].map((item, idx) => (
        <TodoItem key={idx} disable todoModule={todoModule} />
      ))}
    </div>
  );
};

export default TodoList;
