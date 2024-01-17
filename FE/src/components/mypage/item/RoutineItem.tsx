import React, { useRef } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { RoutineItemConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import Text from "@components/common/Text";
import { selectRoutineClick, selectRoutineList, setRoutineClick } from "@store/mypage/routineSlice";

interface Props {
  idx: number;
  item: RoutineItemConfig;
}

const RoutineItem = ({ idx, item }: Props) => {
  const dispatch = useAppDispatch();
  const click: number = useAppSelector(selectRoutineClick);
  const clicked = click === idx ? "--clicked" : "";
  const endRef = useRef<HTMLDivElement | null>(null);

  return (
    <div
      ref={idx === click ? endRef : null}
      className={styles[`routine__item${clicked}`]}
      onClick={() => dispatch(setRoutineClick(idx))}
    >
      <div>
        <Text>{item.routineContent}</Text>
        <div>
          {item.days.map((day: string, i: number) => (
            <Text types="small" key={i}>
              {i === item.days.length - 1 ? day : day + ", "}
            </Text>
          ))}
        </div>
        <div>
          <Text types="small">{item.startDay} ~ </Text>
          <Text types="small">{item.endDay}</Text>
        </div>
      </div>
      <div style={{ backgroundColor: item.category?.categoryColorCode }}></div>
    </div>
  );
};

export default RoutineItem;
