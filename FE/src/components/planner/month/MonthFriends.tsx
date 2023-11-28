import React, { MouseEvent, useEffect, useRef, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import FriendProfileIcon, { ProfileIconInfo } from "@components/common/FriendProfileIcon";
import AddIcon from "@mui/icons-material/Add";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import { useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo } from "@store/authSlice";
import { followingType } from "@util/friend.interface";
import { followApi } from "@api/Api";
import { throttle } from "@util/EventControlModule";
import { useNavigate } from "react-router-dom";

const MonthFriends = () => {
  const navigator = useNavigate();
  const userId = useAppSelector(selectUserId);
  const userInfo = useAppSelector(selectUserInfo);
  const userProfile: ProfileIconInfo = {
    userId: userId,
    nickname: userInfo.nickname,
    profileImage: userInfo.profileImage,
    statusMessage: userInfo.statusMessage,
  };
  const [followingData, setFollowingData] = useState<followingType[]>([]);
  const scrollRef = useRef<HTMLDivElement | null>(null);
  const [isDrag, setIsDrag] = useState<boolean>(false);
  const [startX, setStartX] = useState<number>(0);

  const onDragStart = (e: MouseEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDrag(true);
    if (scrollRef.current) setStartX(e.pageX + scrollRef.current.scrollLeft);
  };

  const onDragEnd = () => {
    setIsDrag(false);
  };

  const onDragMove = (e: MouseEvent<HTMLDivElement>) => {
    if (isDrag && scrollRef.current) {
      scrollRef.current.scrollLeft = startX - e.pageX;
    }
  };

  const delay = 100;
  const onThrottleDragMove = throttle(onDragMove, delay);

  const getFollowing = () => {
    followApi
      .getFollowing(userId)
      .then((res) => {
        const response = res.data.data;
        setFollowingData(response);
      })
      .catch((err) => console.error(err));
  };

  const handleBtnAdd = () => {
    navigator("/search");
  };

  useEffect(() => {
    getFollowing();
  }, []);

  return (
    <div className={styles["friend"]}>
      {/* <div className={styles["friend__btn--left"]}>
        <ChevronLeftIcon />
      </div> */}
      <div
        ref={scrollRef}
        className={styles["friend__container"]}
        onMouseDown={onDragStart}
        onMouseMove={(e) => {
          if (isDrag) onThrottleDragMove(e);
        }}
        onMouseUp={onDragEnd}
        onMouseLeave={onDragEnd}
      >
        <FriendProfileIcon profile={userProfile} />
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
        <div className={styles["friend__btn--add"]} onClick={handleBtnAdd}>
          <div>
            <AddIcon />
          </div>
        </div>
      </div>
      {/* <div className={styles["friend__btn--right"]}>
        <ChevronRightIcon />
      </div> */}
    </div>
  );
};

export default MonthFriends;
