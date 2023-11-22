import React from "react";
import styles from "@styles/planner/Month.module.scss";
import Profile from "@components/common/Profile";
import MonthFriends from "@components/planner/month/MonthFriends";
import MonthIconBox from "@components/planner/month/MonthIconBox";
import { useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo } from "@store/authSlice";
import { selectFriendId, selectFriendInfo } from "@store/friendSlice";

interface Props {
  isOpen: boolean;
}

const MonthDetail = ({ isOpen }: Props) => {
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const userInfo = useAppSelector(selectUserInfo);
  const friendInfo = useAppSelector(selectFriendInfo);
  const close = isOpen ? "" : "--close";

  return (
    <div className={styles[`detail${close}`]}>
      <MonthFriends />
      <Profile types="기본" profile={userId != friendId ? friendInfo : userInfo} />
      <MonthIconBox types="소개글" />
      {isOpen && (
        <div className={styles["detail__stats"]}>
          <MonthIconBox types="월별통계" />
          <MonthIconBox types="팔로우" />
          <MonthIconBox types="좋아요" />
        </div>
      )}
      <MonthIconBox types="방명록" />
    </div>
  );
};

export default MonthDetail;
