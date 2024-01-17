import React, { useEffect, useState } from "react";
import styles from "@styles/common/Profile.module.scss";
import Text from "@components/common/Text";
import Button from "@components/common/Button";
import Avatar from "@components/common/Avatar";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import { ProfileConfig } from "@components/common/FriendProfile";
import { FollowingType } from "@util/friend.interface";
import { persistor } from "@hooks/configStore";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId, setLogout } from "@store/authSlice";
import {
  clearFollowingList,
  clearFriendInfo,
  selectFollowingList,
  selectFriendId,
  setFollowingList,
} from "@store/friendSlice";
import { followApi, userApi } from "@api/Api";

interface Props {
  types: "기본" | "로그아웃";
  profile: ProfileConfig;
}

const Profile = ({ types, profile }: Props) => {
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const dispatch = useAppDispatch();
  const { profileImage, nickname, statusMessage } = profile;
  const [isFollow, setIsFollow] = useState<boolean>(false);
  const followingList = useAppSelector(selectFollowingList);

  const followRequested = () => {
    followApi
      .addRequested(userId, { followingId: friendId })
      .then((res) => {
        const followId = res.data.data.followId;
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
    dispatch(setLogout());
    dispatch(clearFriendInfo());
    dispatch(clearFollowingList());
    localStorage.removeItem("AL");
    setTimeout(() => {
      persistor.purge();
    }, 200);
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
                  <Button children="로그아웃" types="gray" onClick={() => handleLogout()} />
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
