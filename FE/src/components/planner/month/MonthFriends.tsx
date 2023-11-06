import React, { useEffect, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";
import Profile from "@components/common/Profile";
import FriendProfile, { ProfileConfig } from "@components/common/FriendProfile";
import { useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo } from "@store/authSlice";
import { followingType } from "@util/friend.interface";
import { followApi } from "@api/Api";

const MonthFriends = () => {
  const userId = useAppSelector(selectUserId);
  const profileInfo = useAppSelector(selectUserInfo);
  const [followingData, setFollowingData] = useState<followingType[]>([]);

  const getFollowing = () => {
    followApi
      .getFollowing(userId)
      .then((res) => {
        const response = res.data.data;
        setFollowingData(response);
      })
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    getFollowing();
  }, []);

  return (
    <div className={styles["friend"]}>
      <div className={styles["friend__mine"]}>
        <Profile types="기본" profile={profileInfo} />
      </div>
      <div className={styles["friend__list"]}>
        {followingData && followingData.length > 0 ? (
          <>
            {followingData.map((item: followingType, key: number) => {
              const { followId, nickname, profileImage, statusMessage } = item;
              const followInfo: ProfileConfig = {
                userId: followId,
                nickname,
                statusMessage,
                profileImage,
              };
              return <FriendProfile types="기본" profile={followInfo} key={key} />;
            })}
          </>
        ) : (
          <div className={styles["friend__none"]}>
            <Text>팔로잉 목록이 없습니다.</Text>
          </div>
        )}
      </div>
    </div>
  );
};

export default MonthFriends;
