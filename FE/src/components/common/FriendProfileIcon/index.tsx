import React from "react";
import styles from "@styles/common/Profile.module.scss";
import Text from "@components/common/Text";
import Avatar from "@components/common/Avatar";
import { useAppDispatch } from "@hooks/hook";
import { setFriendInfo } from "@store/friendSlice";
import { useNavigate } from "react-router-dom";

export interface ProfileIconInfo {
  userId: number;
  nickname: string;
  profileImage: string;
  statusMessage: string;
}

interface Props {
  profile: ProfileIconInfo;
}

const FriendProfileIcon = ({ profile }: Props) => {
  const dispatch = useAppDispatch();
  const navigator = useNavigate();

  const handleMoveToFriendProfile = () => {
    dispatch(setFriendInfo(profile));
    navigator("/month");
  };

  return (
    <div className={styles["f-icon"]} onClick={handleMoveToFriendProfile}>
      <Avatar src={profile.profileImage} />
      <div className={styles["f-icon__nickname"]}>
        <Text types="small">{profile.nickname}</Text>
      </div>
    </div>
  );
};

export default FriendProfileIcon;
