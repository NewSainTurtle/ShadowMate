import React, { useEffect, useState } from "react";
import styles from "@styles/common/Profile.module.scss";
import Text from "@components/common/Text";
import Button from "@components/common/Button";
import Avatar from "@components/common/Avatar";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import { ProfileConfig, UserInfoConfig } from "@util/auth.interface";
import { FollowingType } from "@util/friend.interface";
import { persistor } from "@hooks/configStore";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectType, selectUserId, setLogout } from "@store/authSlice";
import {
  clearFollowingList,
  clearFriendInfo,
  selectFollowingList,
  selectFriendId,
  setFollowingList,
} from "@store/friendSlice";
import { authApi, followApi, userApi } from "@api/Api";

interface Props {
  types: "기본" | "로그아웃";
  profile: ProfileConfig | UserInfoConfig;
}

const Profile = ({ types, profile }: Props) => {
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const dispatch = useAppDispatch();
  const { profileImage, nickname, statusMessage } = profile;
  const [isFollow, setIsFollow] = useState<boolean>(false);
  const followingList = useAppSelector(selectFollowingList);
  const type = useAppSelector(selectType);

  const followRequested = () => {
    followApi
      .addRequested(userId, { followingId: friendId })
      .then((res) => {
        const followId: number = res.data.data.followId;
        const followingInfo: FollowingType = {
          followingId: friendId,
          nickname,
          profileImage,
          statusMessage,
          followId: followId,
          email: "",
        };
        dispatch(setFollowingList([...followingList, followingInfo]));
      })
      .catch((err) => console.error(err));
  };

  const handleLogout = () => {
    const autoLoginKey = localStorage.getItem("AL");
    const headers = {
      // 자동로그인 한 경우 header에 key값, 아닌 경우 "".
      "Auto-Login": autoLoginKey ?? "",
    };
    authApi
      .logout({ userId, type }, headers)
      .then(() => {
        dispatch(setLogout());
        dispatch(clearFriendInfo());
        dispatch(clearFollowingList());
      })
      .then(() => {
        persistor.purge();
        localStorage.removeItem("AL");
      })
      .catch((e) => console.error(e));
  };

  useEffect(() => {
    if (types == "기본" && friendId != userId) {
      userApi.searches(userId, { nickname }).then((res) => {
        if (res.data.data.isFollow == "EMPTY") setIsFollow(false);
        else setIsFollow(true);
      });
    } else setIsFollow(false);
  }, [friendId]);

  return (
    <>
      <div className={styles.profile_container}>
        <div className={styles.profile_img}>
          <Avatar src={profileImage} />
        </div>
        <div className={styles.profile_content}>
          <Text types="semi-medium" bold>
            {nickname}
          </Text>
          <Text types="default">{statusMessage}</Text>
        </div>
        <>
          {
            {
              기본: friendId != userId && !isFollow && (
                <div className={styles["profile_button"]} onClick={() => setIsFollow(true)}>
                  <div onClick={followRequested}>
                    <PersonAddIcon />
                  </div>
                </div>
              ),
              로그아웃: (
                <div className={styles["profile_button"]}>
                  <Button types="gray" onClick={handleLogout}>
                    로그아웃
                  </Button>
                </div>
              ),
            }[types]
          }
        </>
      </div>
    </>
  );
};

export default Profile;
