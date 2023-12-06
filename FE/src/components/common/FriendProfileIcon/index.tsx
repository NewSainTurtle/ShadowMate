import React from "react";
import styles from "@styles/common/Profile.module.scss";
import Text from "@components/common/Text";
import Avatar from "@components/common/Avatar";
import { ProfileConfig } from "@components/common/FriendProfile";
import { useAppDispatch } from "@hooks/hook";
import { setFriendInfo } from "@store/friendSlice";
import { useNavigate } from "react-router-dom";

interface Props {
  profile: ProfileConfig;
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
