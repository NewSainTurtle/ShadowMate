import React from "react";
import Header from "@components/planner/day/Header";
import styles from "@styles/planner/day.module.scss";
import Ment from "@components/planner/day/Ment";

const DayPage = () => {
  return (
    <div>
      <div className={styles["page-container"]}>
        <Header />
        <Ment title={"오늘의 다짐"} children={undefined} />
        <div className={styles["item__schedule-table"]}>시간표</div>
        <Ment title={"오늘의 회고"} children={undefined} fileImg />
        <div className={styles["item__driver"]} />
        <div className={styles["item__time"]}>
          <span>{4}</span>
          <span>시간</span>
          <span>{10}</span>
          <span>분</span>
        </div>
        <div className={styles["item__time-table"]}>타임테이블</div>
        <Ment title={"내일 다짐"} children={undefined} />
      </div>
    </div>
  );
};

export default DayPage;
