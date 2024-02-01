import React, { useEffect, useRef } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectRoutineClick, setRoutineClick, RoutineItemConfig } from "@store/mypage/routineSlice";
import { dateFormat } from "@util/getThisWeek";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";

interface Props {
  idx: number;
  item: RoutineItemConfig;
}

interface DaysSorterConfig {
  [key: string]: number;
}
const DaysSorter: DaysSorterConfig = { 월: 1, 화: 2, 수: 3, 목: 4, 금: 5, 토: 6, 일: 7 };

const RoutineItem = ({ idx, item }: Props) => {
  const dispatch = useAppDispatch();
  const click: number = useAppSelector(selectRoutineClick);
  const endRef = useRef<HTMLDivElement | null>(null);
  const clicked = click === idx ? "--clicked" : "";
  const sortDays = () => {
    const days = [...item.days];
    return days?.sort((a: string, b: string) => {
      return DaysSorter[a] - DaysSorter[b];
    });
  };

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }, [click]);

  return (
    <div
      ref={idx === click ? endRef : null}
      className={styles[`routine__item${clicked}`]}
      onClick={() => {
        dispatch(setRoutineClick(idx));
      }}
    >
      <div className={styles["routine__info"]}>
        <Text>{item.routineContent}</Text>
        <div>
          {sortDays()?.map((day: string, i: number) => (
            <Text types="small" key={i}>
              {i === item.days.length - 1 ? day : day + ", "}
            </Text>
          ))}
        </div>
        <div>
          <Text types="small">{dateFormat(item.startDay)} ~ </Text>
          <Text types="small">{dateFormat(item.endDay)}</Text>
        </div>
      </div>
      <div
        style={{
          backgroundColor: item.category ? item.category.categoryColorCode : BASIC_CATEGORY_ITEM.categoryColorCode,
        }}
      ></div>
    </div>
  );
};

export default RoutineItem;
