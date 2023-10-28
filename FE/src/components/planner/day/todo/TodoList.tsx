import React, { useEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import TodoItem from "@components/planner/day/todo/TodoItem";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { BASIC_TODO_ITEM, todoType, setTodoList, selectTodoList } from "@store/planner/daySlice";
import TodoItemChoice from "./TodoItemChoice";

interface Props {
  clicked: boolean;
}

const TodoList = ({ clicked }: Props) => {
  const dispatch = useAppDispatch();
  const todoArr = useAppSelector(selectTodoList);
  const listSize = 11;
  const todoListSize = useMemo(() => {
    return todoArr.length + 1 >= listSize ? todoArr.length + 1 : listSize;
  }, [todoArr]);
  const todoEndRef = useRef<HTMLDivElement>(null);
  const nextId = useRef(todoArr.length + 1);
  const copyTodos = useMemo(() => JSON.parse(JSON.stringify(todoArr)), [todoArr]);

  useEffect(() => {
    if (todoArr.length + 1 >= listSize && todoEndRef.current) {
      todoEndRef.current.scrollTop = todoEndRef.current.scrollHeight;
    }
  }, [todoArr.length]);

  const todoModule = (() => {
    const insertTodo = (props: todoType) => {
      copyTodos.push({ ...props, todoId: nextId.current });
      dispatch(setTodoList(copyTodos));
      nextId.current += 1;
    };

    const updateTodo = (idx: number, props: todoType) => {
      copyTodos[idx] = { ...props };
      dispatch(setTodoList(copyTodos));
    };

    const deleteTodo = (idx: number) => {
      copyTodos.splice(idx, 1);
      dispatch(setTodoList(copyTodos));
    };

    return {
      insertTodo,
      updateTodo,
      deleteTodo,
    };
  })();

  return (
    <div
      ref={todoEndRef}
      className={styles["todo-list"]}
      style={{ gridTemplateRows: `repeat(${todoListSize}, calc(100%/${listSize})` }}
    >
      {!clicked ? (
        <>
          {todoArr.map((item: todoType, idx: number) => (
            <TodoItem key={item.todoId} idx={idx} todoItem={item} todoModule={todoModule} />
          ))}
          <TodoItem addTodo todoItem={BASIC_TODO_ITEM} todoModule={todoModule} />
          {Array.from({ length: listSize - todoArr.length - 1 }).map((_, idx) => (
            <TodoItem key={idx} disable todoItem={BASIC_TODO_ITEM} todoModule={todoModule} />
          ))}
        </>
      ) : (
        <>
          {todoArr.map((item: todoType, idx: number) => (
            <TodoItemChoice key={item.todoId} idx={idx} todoItem={item} possible={item.todoStatus === "완료"} />
          ))}
          {Array.from({ length: listSize - todoArr.length }).map((_, idx) => (
            <TodoItemChoice key={idx} todoItem={BASIC_TODO_ITEM} possible={false} />
          ))}
        </>
      )}
    </div>
  );
};

export default TodoList;
