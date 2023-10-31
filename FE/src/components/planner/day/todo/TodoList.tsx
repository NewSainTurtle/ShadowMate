import React, { useEffect, useMemo, useRef } from "react";
import styles from "@styles/planner/day.module.scss";
import TodoItem from "@components/planner/day/todo/TodoItem";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { BASIC_TODO_ITEM, setTodoList, selectTodoList, selectDate } from "@store/planner/daySlice";
import { TodoConfig } from "@util/planner.interface";
import TodoItemChoice from "./TodoItemChoice";
import { plannerApi } from "@api/Api";
import { selectUserId } from "@store/authSlice";

interface Props {
  clicked: boolean;
}

const TodoList = ({ clicked }: Props) => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const date = useAppSelector(selectDate);
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
    const insertTodo = async (props: TodoConfig) => {
      await plannerApi
        .addDailyTodos(userId, {
          date,
          todoContent: props.todoContent,
          categoryId: props.category!.categoryId,
        })
        .then((res) => {
          const todoId = res.data.data["todoId"];
          copyTodos.push({ ...props, todoId });
          dispatch(setTodoList(copyTodos));
        });
    };

    const updateTodo = async (idx: number, props: TodoConfig) => {
      await plannerApi
        .editDailyTodos(userId, {
          date,
          todoId: props.todoId,
          categoryId: props.category!.categoryId,
          todoContent: props.todoContent,
          todoStatus: props.todoStatus,
        })
        .then(() => {
          copyTodos[idx] = { ...props };
          dispatch(setTodoList(copyTodos));
        });
    };

    const deleteTodo = async (idx: number, todoId: number) => {
      await plannerApi
        .deleteDailyTodos(userId, {
          date,
          todoId,
        })
        .then(() => {
          copyTodos.splice(idx, 1);
          dispatch(setTodoList(copyTodos));
        });
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
          {todoArr.map((item: TodoConfig, idx: number) => (
            <TodoItem key={item.todoId} idx={idx} todoItem={item} todoModule={todoModule} />
          ))}
          <TodoItem addTodo todoItem={BASIC_TODO_ITEM} todoModule={todoModule} />
          {Array.from({ length: listSize - todoArr.length - 1 }).map((_, idx) => (
            <TodoItem key={idx} disable todoItem={BASIC_TODO_ITEM} todoModule={todoModule} />
          ))}
        </>
      ) : (
        <>
          {todoArr.map((item: TodoConfig, idx: number) => (
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
