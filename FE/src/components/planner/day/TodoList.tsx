import React, { useEffect, useMemo, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { Add } from "@mui/icons-material";

interface TodoType {
  id: number;
  category: string;
  context: string;
  checked: 0 | 1 | 2; // none, O, X
  isPossible?: boolean;
}

interface TodoItemType {
  item: TodoType;
  saveText?: React.ChangeEventHandler<HTMLInputElement>;
}

const TodoItemDefalut: TodoType = {
  id: 0,
  category: "",
  context: "",
  checked: 0,
  isPossible: false,
};

const TodoItem = ({ item, saveText }: TodoItemType) => {
  const { id, category, context, checked, isPossible } = item;
  const checkedArr = [" ", "O", "X"];
  const [text, setText] = useState(context);
  const modifyText = (e: React.ChangeEvent<HTMLInputElement>) => {
    setText(e.target.value);
  };

  return (
    <div className={`${styles["todo-item"]} ${!isPossible && styles["todo-item--disable"]}`}>
      <div className={styles["todo-item__category"]}>{isPossible ? <Text>{category}</Text> : <Add />}</div>
      <div className={styles["todo-item__context"]}>
        {isPossible ? <input value={text} onChange={modifyText} onBlur={saveText} /> : <Add />}
      </div>
      <div className={styles["todo-item__checked"]}>
        <Text types="semi-medium"> {checkedArr[checked]}</Text>
      </div>
    </div>
  );
};

TodoItem.defaultProps = TodoItemDefalut;

const TodoList = () => {
  const data: TodoType[] = [
    {
      id: 0,
      category: "수학",
      context: "수능완성 수학 과목별 10문제",
      checked: 2,
      isPossible: true,
    },
  ];

  const [todos, setTodos] = useState<TodoType[]>(() => {
    for (let idx = data.length; idx < 13; idx++) {
      data.push({ ...TodoItemDefalut, id: idx });
    }
    return data;
  });

  const saveText = (id: number, e: React.ChangeEvent<HTMLInputElement>) => {
    todos[id].context = e.target.value;
    setTodos([...todos]);
  };

  return (
    <div className={styles["todo-list"]}>
      {todos.map((todo) => (
        <TodoItem
          key={todo.id}
          item={todo}
          saveText={(e) => {
            saveText(todo.id, e);
          }}
        />
      ))}
    </div>
  );
};

export default TodoList;
