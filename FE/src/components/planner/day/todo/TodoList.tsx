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
  const todoListSize = useMemo(() => {
    return todoArr.length + 1 >= listSize ? todoArr.length + 1 : listSize;
  }, [todoArr]);
  const todoEndRef = useRef<HTMLDivElement>(null);
  const copyTodos: TodoConfig[] = useMemo(() => JSON.parse(JSON.stringify(todoArr)), [todoArr]);
  const dragItem = useRef<number | null>(null);
  const dragOverItem = useRef<number | null>(null);

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

    const dragTodo = async (todoId: number) => {
      const drageTargetIdx = dragItem.current as number;
      const endTargetIdx = dragOverItem.current as number;
      const upperTodoId = endTargetIdx - 1 > 0 ? copyTodos[endTargetIdx - 1].todoId : null;
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
          dragItem.current = null;
          dragOverItem.current = null;
          dispatch(setTodoList(copyTodos));
        });
    };

    return {
      insertTodo,
      updateTodo,
      deleteTodo,
      deleteTimeTable,
      dragTodo,
    };
  })();

  const dragModule = (() => {
    const style = `border: 2px dashed red !important;`;
    /** 드래그 시작할 때 */
    const dragStart = (e: React.DragEvent<HTMLDivElement>, idx: number) => {
      e.dataTransfer.setDragImage(new Image(), 0, 0);
      e.currentTarget.style.cssText = style;
      dragItem.current = idx;
    };
    /** 드래그 객체 위로 진입 할 때 */
    const dragEnter = (e: React.DragEvent<HTMLDivElement>, idx: number) => {
      e.preventDefault();
      e.currentTarget.style.cssText = style;
      dragOverItem.current = idx;
    };

    /** 드래그 끝났을 때 */
    const dragLeaveAndDrop = (e: React.DragEvent<HTMLDivElement>) => {
      e.preventDefault();
      e.currentTarget.style.cssText = "";
    };

    return {
      dragStart,
      dragEnter,
      dragLeaveAndDrop,
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
            <div
              key={item.todoId}
              draggable
              className={styles["todo--draggable"]}
              onDragStart={(e) => dragModule.dragStart(e, idx)}
              onDragEnter={(e) => dragModule.dragEnter(e, idx)}
              onDragOver={(e) => e.preventDefault()}
              onDragLeave={(e) => dragModule.dragLeaveAndDrop(e)}
              onDrop={(e) => dragModule.dragLeaveAndDrop(e)}
              onDragEnd={(e) => {
                dragModule.dragLeaveAndDrop(e);
                todoModule.dragTodo(item.todoId);
              }}
            >
              <TodoItem idx={idx} todoItem={item} todoModule={todoModule} />
            </div>
          ))}
          <TodoItem addTodo todoItem={BASIC_TODO_ITEM} todoModule={todoModule} disable={userId != friendId} />
          {Array.from({ length: listSize - todoArr.length - 1 }).map((_, idx) => (
            <TodoItem key={idx} disable todoItem={BASIC_TODO_ITEM} todoModule={todoModule} />
          ))}
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
