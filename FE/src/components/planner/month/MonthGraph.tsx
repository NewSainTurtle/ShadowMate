import React from "react";
import styles from "@styles/planner/Month.module.scss";
import { monthStatusColor as statusColor } from "@util/colors";

const MonthGraph = () => {
  return (
    <div className={styles.graph}>
      <span>Less</span>
      <div>
        {statusColor.map((colors: string) => (
          <div style={{ backgroundColor: colors }} />
        ))}
      </div>
      <span>More</span>
    </div>
  );
};

export default MonthGraph;
