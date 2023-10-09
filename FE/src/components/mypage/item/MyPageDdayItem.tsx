import React, { Dispatch, SetStateAction } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { ddayType } from "@util/planner.interface";
import Dday from "@components/common/Dday";
import { dateFormat } from "@util/getThisWeek";

interface Props {
  item: ddayType;
  index: number;
  click: number;
  setClick: Dispatch<SetStateAction<number>>;
}

const MyPageDdayItem = ({ item, index, click, setClick }: Props) => {
  const clicked = click === index ? "--clicked" : "";
  return (
    <div className={styles[`dday__item${clicked}`]} onClick={() => setClick(index)}>
      <div className={styles["dday__item__title"]}>
        <Text>{item.ddayTitle}</Text>
        <Text types="small">{dateFormat(item.ddayDate)}</Text>
      </div>
      <Dday comparedDate={item.ddayDate}></Dday>
    </div>
  );
};

export default MyPageDdayItem;
