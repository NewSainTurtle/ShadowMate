import React, { DOMAttributes, MouseEventHandler } from "react";
import styles from "@styles/common/Profile.module.scss";
import { ProfileConfig } from "../FriendProfile";
import Text from "../Text";
import Button from "../Button";
import { Avatar } from "@mui/material";

interface Props {
  types: "기본" | "로그아웃";
  profile: ProfileConfig;
  onClick?: MouseEventHandler<HTMLDivElement>;
}

const Profile = ({ types, profile, ...rest }: Props) => {
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
        <>
          {
            {
              기본: <></>,
              로그아웃: (
                <div className={styles.profile_button} {...rest}>
                  <Button children="로그아웃" types="gray" />
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
