import React, { useLayoutEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { followerType, followingType, followRequestType } from "@util/friend.interface";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { selectFollowState } from "@store/friendSlice";
import { followApi } from "@api/Api";
import MyFriendFrame from "./MyFriendFrame";

const MyFriend = ({ title }: { title: "팔로워" | "팔로잉" | "친구검색" }) => {
  const userId: number = useAppSelector(selectUserId);
  const [followRequestData, setFollowRequestData] = useState<followRequestType[]>([]); // 팔로우 요청
  const [followerData, setFolloweData] = useState<followerType[]>([]); // 내 팔로워
  const [followingData, setFollowingData] = useState<followingType[]>([]); // 내 팔로잉
  const followState: boolean = useAppSelector(selectFollowState);

  useLayoutEffect(() => {
    switch (title) {
      case "팔로워":
        followApi
          .receiveList(userId)
          .then((res) => {
            const response = res.data.data;
            setFollowRequestData(response);
          })
          .catch((err) => console.error(err));
        followApi
          .getFollwers(userId)
          .then((res) => {
            const response = res.data.data;
            setFolloweData(response);
          })
          .catch((err) => console.error(err));
        break;
      case "팔로잉":
        followApi
          .getFollowing(userId)
          .then((res) => {
            const response = res.data.data;
            setFollowingData(response);
          })
          .catch((err) => console.error(err));
        break;
    }
  }, [title, followState]);

  return (
    <div className={styles["friend__cantainer"]}>
      <div className={styles["friend__content"]}>
        {
          {
            팔로워: (
              <>
                {followRequestData.length != 0 && (
                  <MyFriendFrame title="팔로우 요청 목록" friendList={followRequestData} />
                )}
                <MyFriendFrame title="내 팔로워" friendList={followerData} />
              </>
            ),
            팔로잉: <MyFriendFrame title="내 팔로잉" friendList={followingData} />,
            친구검색: <MyFriendFrame title="친구 검색" search />,
          }[title]
        }
      </div>
    </div>
  );
};

export default MyFriend;
