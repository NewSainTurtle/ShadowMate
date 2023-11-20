import React from "react";
import styles from "@styles/planner/Month.module.scss";
import Profile from "@components/common/Profile";
import MonthFriends from "@components/planner/month/MonthFriends";
import MonthIconBox from "@components/planner/month/MonthIconBox";
import { useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";

const MonthDetail = () => {
  const userInfo = useAppSelector(selectUserInfo);

  return (
    <div className={styles["detail"]}>
      <MonthFriends />
      <Profile types="기본" profile={userInfo} />
      <MonthIconBox types="소개글" />
      <div className={styles["detail__stats"]}>
        <MonthIconBox types="월별통계" />
        <MonthIconBox types="팔로우" />
        <MonthIconBox types="좋아요" />
      </div>
      <MonthIconBox types="방명록" />
    </div>
  );
};

export default MonthDetail;
