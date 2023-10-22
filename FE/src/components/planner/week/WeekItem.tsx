import React, { KeyboardEvent, useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import { DeleteOutlined } from "@mui/icons-material";
import { DailyTodoConfig, DayListConfig } from "@store/weekSlice";
import CategorySelector from "@components/common/CategorySelector";

interface Props {
  dayInfo: DayListConfig;
  item: DailyTodoConfig;
  key: number;
}

const WeekItem = ({ dayInfo, item, key }: Props) => {
  /* 카테고리 선택 메뉴 */
  const dropMenuRef = useRef<HTMLDivElement>(null);
  const [isDropdownView, setIsDropdownView] = useState(false);

  const handleEnter = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      (document.activeElement as HTMLElement).blur();
    }
  };

  return (
    <div className={styles["item__todo-item"]} key={key}>
      <div onClick={() => setIsDropdownView(!isDropdownView)}>
        <span>💻</span>
        {isDropdownView && <CategorySelector handleClick={() => console.log("open")} />}
      </div>
      {item.todoUpdate ? (
        <input
          className={styles["item__edit-input"]}
          autoFocus
          type="text"
          defaultValue={item.todoContent}
          onKeyDown={(e) => handleEnter(e)}
        />
      ) : (
        <div>
          <Text types="small">{item.todoContent}</Text>
        </div>
      )}
      <DeleteOutlined />
      <div>{item.todoStatus ? "O" : "X"}</div>
    </div>
  );
};

export default WeekItem;
