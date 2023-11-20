import React, { useEffect, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import FriendProfileIcon from "@components/common/FriendProfileIcon";
import AddIcon from "@mui/icons-material/Add";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { followingType } from "@util/friend.interface";
import { followApi } from "@api/Api";

const MonthFriends = () => {
  const userId = useAppSelector(selectUserId);
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
      <div className={styles["friend__btn--left"]}>
        <ChevronLeftIcon />
      </div>
      <div className={styles["friend__container"]}>
        {followingData && followingData.length > 0 && (
          <>
            {followingData.map((item: followingType, key: number) => {
              const { followingId, nickname, profileImage, statusMessage } = item;
              const followInfo = {
                userId: followingId,
                nickname,
                profileImage,
                statusMessage,
              };
              return <FriendProfileIcon profile={followInfo} key={key} />;
            })}
          </>
        )}
        <div className={styles["friend__btn--add"]}>
          <div>
            <AddIcon />
          </div>
        </div>
      </div>
      <div className={styles["friend__btn--right"]}>
        <ChevronRightIcon />
      </div>
    </div>
  );
};

export default MonthFriends;
