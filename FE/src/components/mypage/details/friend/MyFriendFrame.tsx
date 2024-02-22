import React, { useLayoutEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import FriendProfile from "@components/common/FriendProfile";
import { FollowerType, FollowingType, FollowRequestType, FollowType, FriendSearchType } from "@util/friend.interface";
import { FriendInfoConfig } from "@util/auth.interface";
import { selectUserId } from "@store/authSlice";
import { useAppSelector } from "@hooks/hook";
import { userApi } from "@api/Api";
import { useDebounce } from "@util/EventControlModule";
import { selectFollowState } from "@store/friendSlice";

interface MyFriendListType extends FriendInfoConfig {
  followerId?: number;
  followingId?: number;
  requesterId?: number;
  userId?: number;
  isFollow?: FollowType["types"];
}

interface Props {
  title: string;
  search?: boolean;
  friendList?: (FollowerType | FollowingType | FollowRequestType)[];
}

const MyFriendFrame = ({ title, search, friendList }: Props) => {
  const userId: number = useAppSelector(selectUserId);
  const [searchFriend, setSearchFriend] = useState<FriendSearchType[]>([]);
  const [searchKeyWord, setSearchKeyWord] = useState("");
  const debounceKeyword = useDebounce(searchKeyWord, 400);
  const [alertMessage, setAlertMessage] = useState("");
  const followState = useAppSelector(selectFollowState);

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyWord(e.target.value);
  };

  const typeToFollow = (type: string) => {
    if (type == "EMPTY") {
      if (search) return "친구 신청";
      else return "팔로워 신청";
    }
    if (type == "REQUESTED") return "취소";
    if (search && "FOLLOW") return "팔로잉 삭제";
    return "팔로워 삭제";
  };

  useLayoutEffect(() => {
    if (!debounceKeyword.length) {
      setSearchFriend([]);
      setAlertMessage("닉네임을 통해 검색이 가능합니다.");
    }
  }, [debounceKeyword.length]);

  useLayoutEffect(() => {
    if (!search) {
      setSearchKeyWord("");
      setSearchFriend([]);
    }
  }, [search]);

  useLayoutEffect(() => {
    if (searchKeyWord)
      userApi
        .searches(userId, { nickname: searchKeyWord })
        .then((res) => {
          const response = res.data.data;
          if (response.userId == null) {
            setSearchFriend([]);
            setAlertMessage("일치하는 닉네임을 찾을 수 없습니다.");
            return;
          }

          const isFollow = (() => {
            if (userId == response.userId) return "";
            return typeToFollow(response.isFollow) as unknown as FriendSearchType["isFollow"];
          })();
          setSearchFriend([{ ...response, isFollow }]);
        })
        .catch((err) => console.error(err));
  }, [debounceKeyword, followState]);

  const friendCheck = (friend: MyFriendListType) => {
    if (friend.followerId)
      return {
        id: friend.followerId,
        isFollow: typeToFollow(friend.isFollow ?? "FOLLOW"),
      };
    else if (friend.followingId) return { id: friend.followingId, isFollow: "팔로잉 삭제" };
    else if (friend.requesterId) return { id: friend.requesterId, isFollow: "요청" };
    else return { id: friend.userId, isFollow: friend.isFollow };
  };

  const followList = search ? searchFriend : (friendList as (FollowerType | FollowingType | FollowRequestType)[]);

  return (
    <div className={styles["friend__frame"]}>
      <div className={styles["friend__frame__title"]}>
        <Text bold>{title}</Text>
      </div>
      {search && (
        <Input
          types="search"
          value={searchKeyWord}
          onChange={onChangeHandler}
          placeholder="사용자 닉네임으로 검색"
          maxLength={10}
        />
      )}
      <div className={styles["friend__frame__list"]}>
        <div>
          {followList && followList.length > 0 ? (
            followList.map((item) => {
              const { id, isFollow } = friendCheck(item);
              const { nickname, profileImage, statusMessage } = item;
              const followInfo = {
                userId: id as number,
                nickname,
                statusMessage,
                profileImage,
              };
              return (
                <FriendProfile
                  key={title + id + isFollow}
                  types={isFollow as FollowType["types"]}
                  profile={followInfo}
                />
              );
            })
          ) : (
            <Text types="small">{search ? alertMessage : `${title}이(가) 없습니다.`}</Text>
          )}
        </div>
      </div>
    </div>
  );
};

export default MyFriendFrame;
