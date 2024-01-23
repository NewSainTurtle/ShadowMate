import React, { useEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import TodoItem from "@components/planner/day/todo/TodoItem";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { BASIC_TODO_ITEM, setTodoList, selectTodoList, selectDayDate, setTimeTable } from "@store/planner/daySlice";
import { TimeTableConfig, TodoConfig } from "@util/planner.interface";
import TodoItemChoice from "./TodoItemChoice";
import { plannerApi } from "@api/Api";
import { selectUserId } from "@store/authSlice";
import { selectFriendId } from "@store/friendSlice";

interface Props {
  clicked: boolean;
}

const TodoList = ({ clicked }: Props) => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const date = useAppSelector(selectDayDate);
  const todoArr: TodoConfig[] = useAppSelector(selectTodoList);
  const listSize = 11;
  const todoEndRef = useRef<HTMLDivElement>(null);
  const copyTodos: TodoConfig[] = useMemo(() => JSON.parse(JSON.stringify(todoArr)), [todoArr]);
  const dragTargetRef = useRef<number | null>(null);
  const dragEndRef = useRef<number | null>(null);
  const draggablesRef = useRef<HTMLDivElement[]>([]);

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
          categoryId: props.category?.categoryId ?? 0,
          todoContent: props.todoContent,
          todoStatus: props.todoStatus,
        })
        .then(() => {
          copyTodos[idx] = { ...props };
          dispatch(setTodoList(copyTodos));
        });
    };

    /**
     * todo 상태를 "미완료"로 변경시 등록된 타임테이블 모두 삭제
     */
    const deleteTimeTable = (todoId: number, timeTables: TimeTableConfig[], todoStatus: TodoConfig["todoStatus"]) => {
      timeTables.map(async (item) => {
        const { timeTableId } = item;
        await plannerApi
          .deleteTimetable(userId, { date, todoId, timeTableId })
          .then(() => dispatch(setTimeTable({ todoId, timeTableId, startTime: "", endTime: "", todoStatus })));
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
      deleteTimeTable,
    };
  })();

  const dragModule = (() => {
    /** 드래그 컴포넌트 스타일 */
    const dragOverClass = styles["todo-draggable--over"];

    /** 드래그 가까운 element 찾기 */
    const getDragAfterElement = (y: number) => {
      const draggableElements = Array.from(
        draggablesRef.current.filter((ele) => !ele.classList.contains(`${dragOverClass}`)),
      );

      const initialObject = { offset: Number.NEGATIVE_INFINITY, element: null as null | Element };
      const result = draggableElements.reduce((closest, element) => {
        const box = element.getBoundingClientRect(); //해당 element top값, height값 담겨져 있는 메소드
        const offset = y - box.top - box.height / 2;
        if (offset < 0 && offset > closest.offset) return { offset: offset, element: element };
        else return closest;
      }, initialObject).element;

      return result;
    };

    const dragTodo = async (todoId: number) => {
      const drageTargetIdx = dragTargetRef.current as number;
      const endTargetIdx = dragEndRef.current as number;
      const upperTodoId = endTargetIdx > 0 ? copyTodos[endTargetIdx].todoId : null;
      await plannerApi
        .todoSequence(userId, {
          date,
          todoId,
          upperTodoId,
        })
        .then(() => {
          const dragItemConotent = copyTodos[drageTargetIdx];
          copyTodos.splice(drageTargetIdx, 1);
          copyTodos.splice(endTargetIdx, 0, dragItemConotent);
          dragTargetRef.current = null;
          dragEndRef.current = null;
          dispatch(setTodoList(copyTodos));
        });
    };

    const containerDragOver = (e: React.DragEvent<HTMLDivElement>) => {
      e.preventDefault();
      const afterElement = getDragAfterElement(e.clientY);
      const draggable = document.querySelector(`.${dragOverClass}`);
      if (draggable) e.currentTarget.insertBefore(draggable, afterElement);
    };

    const dragStart = (e: React.DragEvent<HTMLDivElement>, idx: number) => {
      e.dataTransfer.setDragImage(new Image(), 0, 0);
      e.currentTarget.classList.add(dragOverClass);
      dragTargetRef.current = idx;
    };

    const dragLeave = (e: React.DragEvent<HTMLDivElement>, idx: number) => {
      e.preventDefault();
      dragEndRef.current = idx;
    };

    const dragEnd = async (e: React.DragEvent<HTMLDivElement>, todoId: number) => {
      e.preventDefault();
      e.currentTarget.classList.remove(dragOverClass);
      dragTodo(todoId);
    };

    return {
      containerDragOver,
      dragStart,
      dragLeave,
      dragEnd,
    };
  })();

  return (
    <div ref={todoEndRef} className={styles["todo-list"]}>
      {!clicked ? (
        <>
          <div
            className={`${styles["todo-list__box"]}`}
            style={{ height: `calc((100%/${listSize}) * ${todoArr.length})` }}
            onDragOver={(e) => dragModule.containerDragOver(e)}
          >
            {todoArr.map((item: TodoConfig, idx: number) => (
              <div
                key={item.todoId}
                ref={(el: HTMLDivElement) => (draggablesRef.current[idx] = el)}
                className={styles["todo-list__box--dragable"]}
                draggable
                onDragStart={(e) => dragModule.dragStart(e, idx)}
                onDragEnter={(e) => e.preventDefault()}
                onDragOver={(e) => e.preventDefault()}
                onDragLeave={(e) => dragModule.dragLeave(e, idx)}
                onDragEnd={(e) => dragModule.dragEnd(e, item.todoId)}
              >
                <TodoItem idx={idx} todoItem={item} todoModule={todoModule} />
              </div>
            ))}
          </div>

          <div className={styles["todo-list__box"]} style={{ height: `calc((100%/${listSize})` }}>
            <TodoItem addTodo todoItem={BASIC_TODO_ITEM} todoModule={todoModule} disable={userId != friendId} />
          </div>

          <div
            className={styles["todo-list__box"]}
            style={{ height: `calc((100%/${listSize} * ${listSize - todoArr.length - 1})` }}
          >
            {Array.from({ length: listSize - todoArr.length - 1 }).map((_, idx) => (
              <TodoItem key={idx} disable todoItem={BASIC_TODO_ITEM} todoModule={todoModule} />
            ))}
          </div>
        </>
      ) : (
        <>
          {todoArr.map((item: TodoConfig, idx: number) => (
            <TodoItemChoice
              key={item.todoId}
              idx={idx}
              todoItem={item}
              possible={item.todoStatus === "완료" || item.todoStatus === "진행중"}
            />
          ))}
          {Array.from({ length: listSize - todoArr.length }).map((_, idx) => (
            <TodoItemChoice key={idx} todoItem={BASIC_TODO_ITEM} possible={false} disable />
          ))}
        </>
      )}
    </div>
  );
};

export default TodoList;
