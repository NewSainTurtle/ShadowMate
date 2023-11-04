import React from "react";
import styles from "@styles/planner/Month.module.scss";
import Profile from "@components/common/Profile";
import FriendProfile, { ProfileConfig } from "@components/common/FriendProfile";
import { useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";

const FRIENDS_LIST: ProfileConfig[] = [
  { userId: 0, profileImage: "", nickname: "토롱이", statusMessage: "인생은 생각하는대로 흘러간다." },
  { userId: 0, profileImage: "", nickname: "yyyysu", statusMessage: "아좌아좌~" },
  { userId: 0, profileImage: "", nickname: "곰돌이 푸", statusMessage: "오늘 점심은 꿀이다" },
  { userId: 0, profileImage: "", nickname: "mung", statusMessage: "멍멍이는 멍멍하지" },
  { userId: 0, profileImage: "", nickname: "mung", statusMessage: "멍멍이는 멍멍하지" },
  { userId: 0, profileImage: "", nickname: "mung", statusMessage: "멍멍이는 멍멍하지" },
  { userId: 0, profileImage: "", nickname: "mung", statusMessage: "멍멍이는 멍멍하지" },
  { userId: 0, profileImage: "", nickname: "mung", statusMessage: "멍멍이는 멍멍하지" },
];

const MonthFriends = () => {
  const profileInfo = useAppSelector(selectUserInfo);

  return (
    <div className={styles["friend"]}>
      <div className={styles["friend__mine"]}>
        <Profile types="기본" profile={profileInfo} />
      </div>
      <div className={styles["friend__list"]}>
        {FRIENDS_LIST.map((item: ProfileConfig, key: number) => (
          <FriendProfile types="기본" profile={item} key={key} />
        ))}
      </div>
    </div>
  );
};

export default MonthFriends;
