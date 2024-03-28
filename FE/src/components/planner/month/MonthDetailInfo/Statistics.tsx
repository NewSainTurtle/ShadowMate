import React from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";
import { useAppSelector } from "@hooks/hook";
import { MonthConfig, selectFollowCount, selectStatistics } from "@store/planner/monthSlice";

interface Props {
  types: "월별통계" | "팔로우" | "좋아요";
}

const Statistics = ({ types }: Props) => {
  const statisticsInfo: MonthConfig["statistics"] = useAppSelector(selectStatistics);
  const followCountInfo: MonthConfig["followCount"] = useAppSelector(selectFollowCount);

  const statistics = {
    월별통계: [
      { id: 0, title: "전체", count: statisticsInfo.todoTotal },
      { id: 1, title: "완료", count: statisticsInfo.todoComplete },
      { id: 2, title: "미완료", count: statisticsInfo.todoIncomplete },
    ],
    팔로우: [
      { id: 3, title: "팔로워", count: followCountInfo.followerCount },
      { id: 4, title: "팔로잉", count: followCountInfo.followingCount },
    ],
    좋아요: [{ id: 5, title: " 월별", count: statisticsInfo.plannerLikeCount }],
  };

  return (
    <div className={styles["stats"]}>
      {statistics[types].map((item) => (
        <div className={styles["stats__contents"]} key={item.id}>
          <Text types="small">{item.title}</Text>
          <Text types="small">{item.count}</Text>
        </div>
      ))}
    </div>
  );
};

export default Statistics;
