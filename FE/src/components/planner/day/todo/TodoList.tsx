import React, { useEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import TodoItem from "@components/planner/day/todo/TodoItem";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { BASIC_TODO_ITEM, setTodoList, selectTodoList, selectDayDate } from "@store/planner/daySlice";
import { TodoConfig } from "@util/planner.interface";
import TodoItemChoice from "./TodoItemChoice";
import { selectUserId } from "@store/authSlice";
import { selectFriendId } from "@store/friendSlice";
import dragModule from "@util/DragModule";

interface Props {
  clicked: boolean;
}

const TodoList = ({ clicked }: Props) => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const date = useAppSelector(selectDayDate);
  const dailyTodos = useAppSelector(selectTodoList);
  const [todos, setTodos] = useState<TodoConfig[]>([...dailyTodos]);
  const listSize = 11;
  const todoEndRef = useRef<HTMLDivElement>(null);
  const draggablesRef = useRef<HTMLDivElement[]>([]);

  useEffect(() => {
    if (dailyTodos.length + 1 >= listSize && todoEndRef.current) {
      todoEndRef.current.scrollTop = todoEndRef.current.scrollHeight;
    }
  }, [dailyTodos.length]);

  useEffect(() => {
    dispatch(setTodoList(todos));
  }, [todos]);

  const dragHoverStyle = styles["todo-draggable"];
  const { containerDragOver, dragStart, dragEnter, dragEnd, ...childMouseEvent } = dragModule({
    date,
    todos: dailyTodos,
    setTodos,
    dragClassName: dragHoverStyle,
    draggablesRef,
  });

  return (
    <div ref={todoEndRef} className={styles["todo-list"]}>
      {!clicked ? (
        <>
          <div
            className={`${styles["todo-list__box"]}`}
            style={{ height: `calc((100%/${listSize}) * ${dailyTodos.length})` }}
            onDragOver={(e) => containerDragOver(e)}
          >
            {dailyTodos.map((item: TodoConfig, idx: number) => (
              <div
                key={item.todoId}
                ref={(el: HTMLDivElement) => (draggablesRef.current[idx] = el)}
                className={styles["todo-list__box--dragable"]}
                draggable
                onDragStart={(e) => dragStart(e, idx)}
                onDragEnter={(e) => dragEnter(e, idx)}
                onDragOver={(e) => e.preventDefault()}
                onDragLeave={(e) => e.preventDefault()}
                onDragEnd={(e) => dragEnd(e, item.todoId)}
              >
                <TodoItem
                  idx={idx}
                  item={item}
                  dragModule={childMouseEvent}
                  dailyTodos={dailyTodos}
                  setDailyTodos={setTodos}
                />
              </div>
            ))}
          </div>

          <div className={styles["todo-list__box"]} style={{ height: `calc((100%/${listSize})` }}>
            <TodoItem
              addTodo
              item={BASIC_TODO_ITEM}
              disable={userId != friendId}
              dailyTodos={dailyTodos}
              setDailyTodos={setTodos}
            />
          </div>

          <div
            className={styles["todo-list__box"]}
            style={{ height: `calc((100%/${listSize} * ${listSize - dailyTodos.length - 1})` }}
          >
            {Array.from({ length: listSize - dailyTodos.length - 1 }).map((_, idx) => (
              <TodoItem key={idx} disable item={BASIC_TODO_ITEM} dailyTodos={dailyTodos} setDailyTodos={setTodos} />
            ))}
          </div>
        </>
      ) : (
        <>
          <div className={`${styles["todo-list__box"]}`}>
            {dailyTodos.map((item: TodoConfig, idx: number) => (
              <TodoItemChoice
                key={item.todoId}
                idx={idx}
                todoItem={item}
                possible={item.todoStatus === "완료" || item.todoStatus === "진행중"}
              />
            ))}
            {Array.from({ length: listSize - dailyTodos.length }).map((_, idx) => (
              <TodoItemChoice key={idx} todoItem={BASIC_TODO_ITEM} possible={false} disable />
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export default TodoList;
