import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import FriendProfile from "@components/common/FriendProfile";
import { followerListData, followingListData, followRequestData, FriendSearch } from "@util/data/FriendData";
import { followerType, followingType, followRequestType } from "@util/friend.interface";

interface MyFriendListType {
  followId?: number;
  followerId?: number;
  followRequestId?: number;
  receiveId?: number;
  userId?: number;
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
  isFollow?: "삭제" | "친구 신청" | "팔로워 신청" | "요청" | "취소";
}

interface Props {
  title: string;
  search?: boolean;
  friendList?: (followerType | followingType | followRequestType)[];
}

const MyFriendFrame = ({ title, search, friendList }: Props) => {
  const [searchKeyWord, setSearchKeyWord] = useState("");

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyWord(e.target.value);
  };

  const friendCheck = (friend: MyFriendListType) => {
    if (friend.followId) {
      if (friend.isFollow) return { id: friend.followId, isFollow: friend.isFollow };
      else return { id: friend.followId, isFollow: "삭제" };
    } else if (friend.followRequestId) return { id: friend.followRequestId, isFollow: "요청" };
    else return { id: friend.userId, isFollow: friend.isFollow };
  };

  return (
    <div className={styles["friend__frame"]}>
      <div className={styles["friend__frame__title"]}>
        <Text bold>{title}</Text>
      </div>
      {search && (
        <Input types="search" value={searchKeyWord} onChange={onChangeHandler} placeholder="사용자 닉네임으로 검색" />
      )}
      <div className={styles["friend__frame__list"]}>
        {friendList && friendList.length > 0 ? (
          friendList.map((item, index) => {
            const { id, isFollow } = friendCheck(item);
            const followInfo = {
              nickname: item.nickname,
              message: item.statusMessage,
              src: item.profileImage,
            };
            return (
              <FriendProfile
                key={id}
                types={isFollow as "삭제" | "친구 신청" | "팔로워 신청" | "요청" | "취소"}
                profile={followInfo}
              />
            );
          })
        ) : (
          <Text types="small">{title}이(가) 없습니다.</Text>
        )}
      </div>
    </div>
  );
};

const MyFriend = ({ title }: { title: "팔로워" | "팔로잉" | "친구검색" }) => {
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
                <MyFriendFrame title="내 팔로워" friendList={followerListData} />
              </>
            ),
            팔로잉: <MyFriendFrame title="내 팔로잉" friendList={followingListData} />,
            친구검색: <MyFriendFrame title="친구 검색" search />,
          }[title]
        }
      </div>
    </div>
  );
};

export default MyFriend;
