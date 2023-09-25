import React, { useEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import TodoItem from "@components/planner/day/TodoItem";
import { todoListType } from "@util/planner.interface";
import { todoData_list as data } from "@util/data/DayTodos";

const TodoDataDefalut: todoListType = {
  todoId: -1,
  categoryTitle: "",
  categoryColorCode: "",
  todoContent: "",
  todoStatus: 0,
  isPossible: false,
};

const TodoList = () => {
  const [todos, setTodos] = useState<todoListType[]>(data);
  const todolistView = useMemo<todoListType[]>(() => {
    let disableNum = 1;
    let disableTodo = [];
    if (13 - todos.length > 1) disableNum = 13 - todos.length;

    for (let idx = 0; idx < disableNum; idx++) {
      disableTodo.push({ ...TodoDataDefalut, isPossible: false });
    }

    return [...todos, ...disableTodo];
  }, [todos]);
  const nextId = useRef(data.length);

  const insertTodo = () => {
    const nextTodoList = todos.concat({
      todoId: nextId.current,
      categoryTitle: "",
      categoryColorCode: "",
      todoContent: "",
      todoStatus: 0,
      isPossible: true,
    });
    setTodos(nextTodoList);
    nextId.current += 1; // id 값 1씩 증가
  };

  const updateTodo = (props: todoListType) => {
    const { todoId, todoContent, todoStatus } = props;
    setTodos(
      todos.map((todo) => {
        if (todo.todoId === todoId) {
          return {
            ...todo,
            todoContent: todoContent,
            todoStatus: todoStatus,
          };
        }
        return todo;
      }),
    );
  };

  const deleteTodo = (id: number) => {
    setTodos(todos.filter((todo) => todo.todoId !== id));
  };

  return (
    <div className={styles["todo-list"]}>
      {todolistView.map((todo, idx) => (
        <TodoItem
          key={idx}
          item={todo}
          addIndex={idx == data.length}
          insertTodo={insertTodo}
          updateTodo={updateTodo}
          deleteTodo={deleteTodo}
        />
      ))}
    </div>
  );
};

export default TodoList;
