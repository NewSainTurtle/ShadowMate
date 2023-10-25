import React, { ChangeEvent, Dispatch, KeyboardEvent, RefObject, SetStateAction, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import CategorySelector from "@components/common/CategorySelector";
import { DeleteOutlined } from "@mui/icons-material";
import { TodoConfig } from "@util/planner.interface";
import { categoryDefault } from "@components/planner/day/TodoItem";
import todoModule from "@util/TodoModule";

interface Props {
  todoItems: TodoConfig[];
  setTodoItems: Dispatch<SetStateAction<TodoConfig[]>>;
  item: TodoConfig;
  idx: number;
}

const WeekItem = ({ todoItems, setTodoItems, item, idx }: Props) => {
  /* 카테고리 선택 메뉴 */
  const [Modalopen, setModalOpen] = useState(false);
  const handleClose = () => setModalOpen(false);

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

  const handleClickCategory = (title: string, bgColor: string, emoticon?: string) => {
    if (emoticon) {
      if (!item.category) {
        updateTodo(idx, { ...item, category: { ...categoryDefault, categoryEmoticon: emoticon } });
      } else {
        updateTodo(idx, { ...item, category: { ...item.category, categoryEmoticon: emoticon } });
      }
    }
    setModalOpen(false);
  };

  return (
    <>
      <div className={styles["item__todo-item"]}>
        <div onClick={() => setModalOpen(!Modalopen)}>
          <span>{item.category?.categoryEmoticon}</span>
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
      <Modal open={Modalopen} onClose={handleClose}>
        <CategorySelector handleClick={handleClickCategory} />
      </Modal>
    </>
  );
};

export default WeekItem;
