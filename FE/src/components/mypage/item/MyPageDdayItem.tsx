import React, { Dispatch, SetStateAction } from "react";
import styles from "../MyPage.module.scss";
import Text from "@components/common/Text";
import { DdayConfig } from "../MyPageFrame";

interface Props {
  item: DdayConfig;
  index: number;
  click: number;
  setClick: Dispatch<SetStateAction<number>>;
}

const MyPageDdayItem = ({ item, index, click, setClick }: Props) => {
  return (
    <div
      className={click === index ? styles["dday-item--clicked"] : styles["dday-item"]}
      onClick={() => setClick(index)}
    >
      <div className={styles["dday-item__title"]}>
        <Text>{item.title}</Text>
        <Text types="small">{item.date}</Text>
      </div>
      <Text types="semi-medium" bold>
        D-234
      </Text>
    </div>
  );
};

export default MyPageDdayItem;
