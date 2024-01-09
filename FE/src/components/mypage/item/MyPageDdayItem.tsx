import React, { useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { DdayItemConfig } from "@util/planner.interface";
import { dateFormat } from "@util/getThisWeek";
import dayjs, { Dayjs } from "dayjs";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectDdayClick, setDdayClick } from "@store/mypage/ddaySlice";

interface Props {
  item: DdayItemConfig;
  idx: number;
}

const MyPageDdayItem = ({ item, idx }: Props) => {
  const dispatch = useAppDispatch();
  const click = useAppSelector(selectDdayClick);
  const clicked = click === idx ? "--clicked" : "";
  const endRef = useRef<HTMLDivElement | null>(null);
  const [calc, setCalc] = useState<number>(0);

  const ddayCalculate = (comparedDate: Date | string | Dayjs) => {
    let today = dayjs(new Date()).format("YYYY-MM-DD");
    let date = dayjs(comparedDate).format("YYYY-MM-DD");
    setCalc(dayjs(today).diff(dayjs(date), "day")); // 오늘 기준으로 비교
  };

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }, [click]);

  useEffect(() => {
    ddayCalculate(item.ddayDate);
  }, [item]);

  const ddayStyle = calc == 0 ? "--today" : calc < 0 ? "--minus" : "--plus";

  return (
    <div
      ref={idx === click ? endRef : null}
      className={styles[`dday__item${clicked}`]}
      onClick={() => dispatch(setDdayClick(idx))}
    >
      <div className={styles["dday__item__title"]}>
        <Text>{item.ddayTitle}</Text>
        <Text types="small">{dateFormat(item.ddayDate)}</Text>
      </div>
      <div className={styles[`dday__item${ddayStyle}`]}>
        <Text types="semi-medium" bold>
          {calc == 0 ? "D-Day" : calc < 0 ? "D" + calc : "D+" + calc}
        </Text>
      </div>
    </div>
  );
};

export default MyPageDdayItem;
