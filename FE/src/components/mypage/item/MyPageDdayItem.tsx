import React, { Dispatch, SetStateAction } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { ddayType } from "@util/planner.interface";
import Dday from "@components/common/Dday";

interface Props {
  item: ddayType;
  index: number;
  click: number;
  setClick: Dispatch<SetStateAction<number>>;
}

const dateFormat = (dateInfo: Date | string) => {
  const dayList = ["일", "월", "화", "수", "목", "금", "토"];

  let today = new Date(dateInfo);
  let year = today.getFullYear();
  let month = String(today.getMonth() + 1);
  let date = String(today.getDate());
  let day = dayList[today.getDay()];

  month = month.length > 1 ? month : "0" + month;
  date = date.length > 1 ? date : "0" + date;

  return year + "." + month + "." + date + "(" + day + ")";
};

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
