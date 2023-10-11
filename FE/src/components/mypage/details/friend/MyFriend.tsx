import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import FriendProfile from "@components/common/FriendProfile";
import { followerListData, followingListData, followRequestData, FriendSearch } from "@util/data/FriendData";
import { followerType, followingType, followRequestType } from "@util/friend.interface";

interface Props {
  title: string;
  search?: boolean;
  friendList?: (followerType | followingType | followRequestType)[];
}

const MyPageFriendFrame = ({ title, search, friendList }: Props) => {
  return (
    <div className={styles["friend__frame"]}>
      <div className={styles["friend__frame__title"]}>
        <Text bold>{title}</Text>
      </div>
      {search && <Input types="search" placeholder="사용자 닉네임으로 검색" />}
      <div className={styles["friend__frame__list"]}>
        {friendList && friendList.length > 0 ? (
          friendList.map((item, index) => {
            let followInfo = {
              nickname: item.nickname,
              message: item.statusMessage,
              src: item.profileImage,
            };
            return <FriendProfile key={index} types="기본" profile={followInfo} />;
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
                  <MyPageFriendFrame title="팔로우 요청 목록" friendList={followRequestData} />
                )}
                <MyPageFriendFrame title="내 팔로워" friendList={followerListData} />
              </>
            ),
            팔로잉: <MyPageFriendFrame title="내 팔로잉" friendList={followingListData} />,
            친구검색: <MyPageFriendFrame title="친구 검색" search />,
          }[title]
        }
      </div>
    </div>
  );
};

export default MyFriend;
