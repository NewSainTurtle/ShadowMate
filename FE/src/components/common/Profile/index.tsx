import React, { DOMAttributes } from "react";
import styles from "@styles/common/Profile.module.scss";
import { ProfileProps } from "../FriendProfile";
import Text from "../Text";
import Button from "../Button";
import { Avatar } from "@mui/material";

interface Props {
  types: "기본" | "로그아웃";
  profile: ProfileProps;
  onClick?: DOMAttributes<HTMLDivElement>;
}

const buttons = {
  기본: <></>,
  로그아웃: (
    <div className={styles.profile_button}>
      <Button children="로그아웃" types="gray" />
    </div>
  ),
};

const Profile = ({ types, profile, onClick }: Props) => {
  return (
    <>
      <div className={styles.profile_container}>
        <div className={styles.profile_img}>
          <Avatar src={profile.src} sx={{ width: 80, height: 80 }} />
        </div>
        <div className={styles.profile_content}>
          <Text types="semi-medium" bold>
            {profile.nickname}
          </Text>
          <Text types="default">{profile.message}</Text>
        </div>
        {buttons[types]}
      </div>
    </>
  );
};

export default Profile;
