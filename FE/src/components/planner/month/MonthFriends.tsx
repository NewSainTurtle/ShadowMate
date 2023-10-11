import React from "react";
import styles from "@styles/planner/Month.module.scss";
import Profile from "@components/common/Profile";
import { profileInfo } from "@pages/commonPage";
import FriendProfile, { ProfileConfig } from "@components/common/FriendProfile";

const FRIENDS_LIST: ProfileConfig[] = [
  { src: "", nickname: "토롱이", message: "인생은 생각하는대로 흘러간다." },
  { src: "", nickname: "yyyysu", message: "아좌아좌~" },
  { src: "", nickname: "곰돌이 푸", message: "오늘 점심은 꿀이다" },
  { src: "", nickname: "mung", message: "멍멍이는 멍멍하지" },
  { src: "", nickname: "mung", message: "멍멍이는 멍멍하지" },
  { src: "", nickname: "mung", message: "멍멍이는 멍멍하지" },
  { src: "", nickname: "mung", message: "멍멍이는 멍멍하지" },
  { src: "", nickname: "mung", message: "멍멍이는 멍멍하지" },
];

const MonthFriends = () => {
  return (
    <div className={styles["friend"]}>
      <div className={styles["friend__mine"]}>
        <Profile types="기본" profile={profileInfo} />
      </div>
      <div className={styles["friend__list"]}>
        {FRIENDS_LIST.map((item: ProfileConfig, key: number) => (
          <FriendProfile types="기본" profile={item} />
        ))}
      </div>
    </div>
  );
};

export default MonthFriends;
