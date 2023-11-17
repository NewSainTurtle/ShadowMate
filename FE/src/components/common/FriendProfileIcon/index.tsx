import React from "react";
import styles from "@styles/common/Profile.module.scss";
import Text from "@components/common/Text";
import { Avatar } from "@mui/material";

export interface ProfileIconInfo {
  userId: number;
  nickname: string;
  profileImage: string;
}

interface Props {
  profile: ProfileIconInfo;
}

const FriendProfileIcon = ({ profile }: Props) => {
  return (
    <div className={styles["f-icon"]}>
      <Avatar src={profile.profileImage} sx={{ width: 40, height: 40 }} />
      <div className={styles["f-icon__nickname"]}>
        <Text types="small">{profile.nickname}</Text>
      </div>
    </div>
  );
};

export default FriendProfileIcon;
