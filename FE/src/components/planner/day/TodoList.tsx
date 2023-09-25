import React, { useEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { AddCircleOutline, DeleteOutlined } from "@mui/icons-material";
import { todoListType, categoryType } from "@util/planner.interface";
import { todoData_list as data, todoData_category } from "@util/data/DayTodos";

interface TodoItemProps {
  item: todoListType;
  insertTodo: () => void;
  updateTodo: (todo: todoListType) => void;
  deleteTodo: (id: number) => void;
}

const TodoDataDefalut: todoListType = {
  todoId: -1,
  categoryName: "",
  categoryColorCode: "",
  todoContent: "",
  todoStatus: 0,
  isPossible: false,
};

const TodoItem = ({ item, insertTodo, updateTodo, deleteTodo }: TodoItemProps) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const { todoId, categoryName, todoContent, todoStatus, isPossible } = item;
  const checkedArr = [" ", "O", "X"];
  const [text, setText] = useState(todoContent);
  const modifyText = (e: React.ChangeEvent<HTMLInputElement>) => {
    setText(e.target.value);
  };

  const handleOnBlur = () => {
    saveTodo();
  };

  const handleOnKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      saveTodo();
    }
  };

  const addTodo = (possible: boolean | undefined) => {
    if (possible != undefined && !possible) insertTodo();
  };

  const removeTodo = () => {
    deleteTodo(todoId);
    setText("");
  };

  const saveTodo = () => {
    updateTodo({ ...item, todoContent: text });
  };

  return (
    <div
      className={`${styles["todo-item"]} ${!isPossible && styles["todo-item--disable"]}`}
      onClick={() => addTodo(isPossible)}
    >
      <div className={styles["todo-item__category"]}>
        {isPossible ? <Text>{categoryName}</Text> : <AddCircleOutline />}
      </div>
      <div className={styles["todo-item__context"]}>
        {isPossible ? (
          <div className={styles["todo-item__context__possiable"]}>
            <input
              ref={inputRef}
              value={text}
              onChange={modifyText}
              onBlur={handleOnBlur}
              onKeyDown={handleOnKeyPress}
            />
            <div onClick={removeTodo}>
              <DeleteOutlined />
            </div>
          </div>
        ) : (
          <AddCircleOutline />
        )}
      </div>
      <div className={styles["todo-item__checked"]}>
        <Text types="semi-medium"> {checkedArr[todoStatus]}</Text>
      </div>
    </div>
  );
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
      categoryName: "",
      categoryColorCode: "",
      todoContent: "",
      todoStatus: 0,
      isPossible: true,
    });
    setTodos(nextTodoList);
    nextId.current += 1; // id 값 1씩 증가
  };

  const updateTodo = ({ todoId, todoContent }: todoListType) => {
    setTodos(
      todos.map((todo) => {
        if (todo.todoId === todoId) {
          return {
            ...todo,
            todoContent: todoContent,
          };
        }
        return todo;
      }),
    );
  };

  const deleteTodo = (id: number) => {
    setTodos(todos.filter((todo) => todo.todoId !== id));
  };

  useEffect(() => {
    console.log(todos);
  }, [todos]);

  return (
    <div className={styles["todo-list"]}>
      {todolistView.map((todo, idx) => (
        <TodoItem key={idx} item={todo} insertTodo={insertTodo} updateTodo={updateTodo} deleteTodo={deleteTodo} />
      ))}
    </div>
  );
};

export default TodoList;
