import React, { useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import FriendProfile, { ProfileConfig } from "@components/common/FriendProfile";
import {
  followerType,
  followingType,
  followRequestType,
  followType,
  friendInfo,
  friendSearchType,
} from "@util/friend.interface";
import { selectUserId } from "@store/authSlice";
import { useAppSelector } from "@hooks/hook";
import { userApi } from "@api/Api";
import { useDebounce } from "@util/EventControlModule";

interface MyFriendListType extends friendInfo {
  followId?: number;
  followerId?: number;
  followRequestId?: number;
  receiveId?: number;
  userId?: number;
  isFollow?: followType["types"];
}

interface Props {
  title: string;
  search?: boolean;
  friendList?: (followerType | followingType | followRequestType)[];
}

const MyFriendFrame = ({ title, search, friendList }: Props) => {
  const userId: number = useAppSelector(selectUserId);
  const [searchFriend, setSearchFriend] = useState<friendSearchType[]>([]);
  const [searchKeyWord, setSearchKeyWord] = useState("");
  const debounceKeyword = useDebounce(searchKeyWord, 500);
  const [alertMessage, setAlertMessage] = useState("닉네임을 통해 검색이 가능합니다.");

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyWord(e.target.value);
  };

  useEffect(() => {
    if (!!searchKeyWord.length)
      userApi
        .searches(userId, { nickname: searchKeyWord })
        .then((res) => {
          const response = res.data.data;
          const isFollow: friendSearchType["isFollow"] | "" = (() => {
            if (userId == response.userId) return "";
            let type = response.isFollow;
            if (type == "EMPTY") return "친구 신청";
            else if (type == "REQUESTED") return "취소";
            else return "팔로잉 삭제";
          })();
          setSearchFriend([{ ...response, isFollow }]);
        })
        .catch((err) => {
          const response = err.response.data;
          if (response.code == "NOT_FOUND_NICKNAME") setAlertMessage("일치하는 닉네임을 찾을 수 없습니다.");
          else console.error(err);
        });
  }, [debounceKeyword]);

  const friendCheck = (friend: MyFriendListType) => {
    if (friend.followId) {
      if (friend.isFollow) return { id: friend.followId, isFollow: friend.isFollow }; // 팔로워 목록
      else return { id: friend.followId, isFollow: "팔로잉 삭제" }; // 팔로잉 목록
    } else if (friend.followRequestId) return { id: friend.followRequestId, isFollow: "요청" }; // 팔로우 요청
    else return { id: friend.userId, isFollow: friend.isFollow }; // 친구 검색
  };

  const followList: (followerType | followingType | followRequestType | friendSearchType)[] = search
    ? searchFriend
    : friendList!;

  return (
    <div className={styles["friend__frame"]}>
      <div className={styles["friend__frame__title"]}>
        <Text bold>{title}</Text>
      </div>
      {search && (
        <Input types="search" value={searchKeyWord} onChange={onChangeHandler} placeholder="사용자 닉네임으로 검색" />
      )}
      <div className={styles["friend__frame__list"]}>
        {followList && followList.length > 0 ? (
          followList.map((item, index) => {
            const { id, isFollow } = friendCheck(item);
            const followInfo: ProfileConfig = {
              userId: id!,
              nickname: item.nickname,
              message: item.statusMessage,
              src: item.profileImage,
            };
            return <FriendProfile key={id} types={isFollow as followType["types"]} profile={followInfo} />;
          })
        ) : (
          <Text types="small">{search ? alertMessage : `${title}이(가) 없습니다.`}</Text>
        )}
      </div>
    </div>
  );
};

export default MyFriendFrame;
