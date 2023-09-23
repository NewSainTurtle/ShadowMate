import React, { useEffect, useMemo, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { AddCircleOutline } from "@mui/icons-material";
import { todoType, categoryType } from "@util/planner.interface";
import { todoData_list as data, todoData_category } from "@util/data/DayTodos";

interface todoListType extends todoType {
  isPossible?: boolean;
}

interface TodoItemType {
  item: todoListType;
  saveText?: React.ChangeEventHandler<HTMLInputElement>;
}

const TodoItemDefalut: todoListType = {
  todoId: 0,
  categoryName: "",
  categoryColorCode: "",
  todoContent: "",
  todoStatus: 0,
  isPossible: false,
};

const TodoItem = ({ item, saveText }: TodoItemType) => {
  const { categoryName, todoContent, todoStatus, isPossible } = item;
  const checkedArr = [" ", "O", "X"];
  const [text, setText] = useState(todoContent);
  const modifyText = (e: React.ChangeEvent<HTMLInputElement>) => {
    setText(e.target.value);
  };

  return (
    <div className={`${styles["todo-item"]} ${!isPossible && styles["todo-item--disable"]}`}>
      <div className={styles["todo-item__category"]}>
        {isPossible ? <Text>{categoryName}</Text> : <AddCircleOutline />}
      </div>
      <div className={styles["todo-item__context"]}>
        {isPossible ? <input value={text} onChange={modifyText} onBlur={saveText} /> : <AddCircleOutline />}
      </div>
      <div className={styles["todo-item__checked"]}>
        <Text types="semi-medium"> {checkedArr[todoStatus]}</Text>
      </div>
    </div>
  );
};

TodoItem.defaultProps = TodoItemDefalut;

const TodoList = () => {
  const [todos, setTodos] = useState<todoListType[]>(() => {
    for (let idx = data.length; idx < 14; idx++) {
      data.push({ ...TodoItemDefalut, todoId: idx });
    }
    return data;
  });

  const saveText = (id: number, e: React.ChangeEvent<HTMLInputElement>) => {
    todos[id].todoContent = e.target.value;
    setTodos([...todos]);
  };

  return (
    <div className={styles["todo-list"]}>
      {todos.map((todo) => (
        <TodoItem
          key={todo.todoId}
          item={todo}
          saveText={(e) => {
            saveText(todo.todoId, e);
          }}
        />
      ))}
    </div>
  );
};

export default TodoList;
