import React from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";

interface Props {
  types: "월별통계" | "팔로우" | "좋아요";
}

const statistics = {
  월별통계: ["전체", "완료", "미완료"],
  팔로우: ["팔로워", "팔로잉"],
  좋아요: ["월별"],
};

const Statistics = ({ types }: Props) => {
  return (
    <div className={styles["stats"]}>
      {statistics[types].map((item, idx) => (
        <div className={styles["stats__contents"]} key={idx}>
          <Text types="small">{item}</Text>
          <Text types="small">{idx}</Text>
        </div>
      ))}
    </div>
  );
};

export default Statistics;
