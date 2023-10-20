import React, { useEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import TodoItem from "@components/planner/day/todo/TodoItem";
import { todoData_list } from "@util/data/DayTodos";
import { useDispatch } from "react-redux";
import { BASIC_TODO_ITEM, todoType, setTodoList } from "@store/planner/daySlice";
import TodoItemChoice from "./TodoItemChoice";

interface Props {
  clicked: boolean;
}

const TodoList = ({ clicked }: Props) => {
  const dispatch = useDispatch();
  const [todoArr, setTodoArr] = useState<todoType[]>(todoData_list);
  const listSize = 11;
  const todoListSize = useMemo(() => {
    return todoArr.length + 1 >= listSize ? todoArr.length + 1 : listSize;
  }, [todoArr]);
  const todoEndRef = useRef<HTMLDivElement>(null);
  const nextId = useRef(todoArr.length + 1);

  useEffect(() => {
    if (todoArr.length + 1 >= listSize && todoEndRef.current) {
      todoEndRef.current.scrollTop = todoEndRef.current.scrollHeight;
    }
  }, [todoArr.length]);

  useEffect(() => {
    if (clicked) dispatch(setTodoList(todoArr));
  }, [clicked]);

  const todoModule = (() => {
    const insertTodo = (props: todoType) => {
      setTodoArr([...todoArr, { ...props, todoId: nextId.current }]);
      nextId.current += 1;
    };

    const updateTodo = (idx: number, props: todoType) => {
      let copyTodos = [...todoArr];
      copyTodos[idx] = { ...props };
      setTodoArr(copyTodos);
    };

    const deleteTodo = (idx: number) => {
      let newTodos = todoArr.filter((item, i) => idx != i);
      setTodoArr(newTodos);
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
          {todoArr.map((item, idx) => (
            <TodoItem key={item.todoId} idx={idx} item={item} todoModule={todoModule} />
          ))}
          <TodoItem addTodo item={BASIC_TODO_ITEM} todoModule={todoModule} />
          {Array.from({ length: listSize - todoArr.length - 1 }).map((_, idx) => (
            <TodoItem key={idx} disable item={BASIC_TODO_ITEM} todoModule={todoModule} />
          ))}
        </>
      ) : (
        <>
          {todoArr.map((item, idx) => (
            <TodoItemChoice key={item.todoId} idx={idx} item={item} possible={item.todoStatus === "완료"} />
          ))}
          {Array.from({ length: listSize - todoArr.length }).map((item, idx) => (
            <TodoItemChoice key={idx} item={BASIC_TODO_ITEM} possible={false} />
          ))}
        </>
      )}
    </div>
  );
};

export default TodoList;
