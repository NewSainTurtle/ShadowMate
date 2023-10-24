import React, { ChangeEvent, Dispatch, KeyboardEvent, RefObject, SetStateAction, useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import { DeleteOutlined } from "@mui/icons-material";
import { TodoConfig } from "@util/planner.interface";
import todoModule from "@util/data/TodoModule";
import CategorySelector from "@components/common/CategorySelector";

interface Props {
  todoItems: TodoConfig[];
  setTodoItems: Dispatch<SetStateAction<TodoConfig[]>>;
  item: TodoConfig;
  idx: number;
  menuRef: RefObject<HTMLDivElement>;
}

const WeekItem = ({ todoItems, setTodoItems, item, idx, menuRef }: Props) => {
  /* 카테고리 선택 메뉴 */
  const dropMenuRef = useRef<HTMLDivElement>(null);
  const [isDropdownView, setIsDropdownView] = useState(false);

  const { updateTodo, deleteTodo } = todoModule(todoItems, setTodoItems);
  const [todo, setTodo] = useState({
    newTodo: "",
    oldTodo: "",
  });

  const handleEnter = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      (document.activeElement as HTMLElement).blur();
    }
  };

  const handleUpdateState = () => {
    setTodo((prev) => {
      return { ...prev, oldTodo: item.todoContent };
    });
    updateTodo(idx, { ...item, todoUpdate: !item.todoUpdate });
  };

  const handleUpdateSave = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.value === "") e.target.value = todo.oldTodo;

    updateTodo(idx, { ...item, todoContent: e.target.value, todoUpdate: !item.todoUpdate });
  };

  const handleDelete = () => {
    deleteTodo(idx);
  };

  return (
    <>
      <div className={styles["item__todo-item"]}>
        <div ref={dropMenuRef} onClick={() => setIsDropdownView(!isDropdownView)}>
          <span>{item.category?.categoryEmoticon}</span>
          {isDropdownView && <CategorySelector target={menuRef} handleClick={() => console.log("open")} />}
        </div>
        {item.todoUpdate ? (
          <input
            className={styles["item__edit-input"]}
            autoFocus
            type="text"
            defaultValue={item.todoContent}
            onChange={(e) =>
              setTodo((prev) => {
                return { ...prev, newTodo: e.target.value };
              })
            }
            onKeyDown={(e) => handleEnter(e)}
            onBlur={(e) => handleUpdateSave(e)}
          />
        ) : (
          <div onClick={handleUpdateState}>
            <Text types="small">{item.todoContent}</Text>
          </div>
        )}
        <DeleteOutlined onClick={handleDelete} />
        <div>{item.todoStatus ? "O" : "X"}</div>
      </div>
    </>
  );
};

export default WeekItem;
