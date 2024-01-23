import React, { useEffect, useRef } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
  selectRoutineIsInit,
import { dateFormat } from "@util/getThisWeek";

interface Props {
  idx: number;
  item: RoutineItemConfig;
}

const RoutineItem = ({ idx, item }: Props) => {
  const dispatch = useAppDispatch();
  const click: number = useAppSelector(selectRoutineClick);
  const isInit = useAppSelector(selectRoutineIsInit);
  const endRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }, [click]);

  return (
    <div
      ref={idx === click ? endRef : null}
      className={styles[`routine__item${clicked}`]}
      onClick={() => dispatch(setRoutineClick(idx))}
    >
      <div>
        <Text>{item.routineContent}</Text>
        <div>
          {item.days?.map((day: string, i: number) => (
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
      <div style={{ backgroundColor: item.category?.categoryColorCode }}></div>
    </div>
  );
};

export default RoutineItem;
