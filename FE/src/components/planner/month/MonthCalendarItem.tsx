import React from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";

const MonthCalendarItem = () => {
  return (
    <div className={styles["calendar__item"]}>
      <Text types="small">0</Text>
    </div>
  );
};

export default MonthCalendarItem;
