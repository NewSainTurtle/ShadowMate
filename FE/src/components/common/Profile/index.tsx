import React, { MouseEventHandler } from "react";
import styles from "@styles/common/Profile.module.scss";
import { ProfileConfig } from "../FriendProfile";
import Text from "../Text";
import Button from "../Button";
import { Avatar } from "@mui/material";
import { useAppDispatch } from "@hooks/hook";
import { clearFriendInfo } from "@store/friendSlice";

interface Props {
  types: "기본" | "로그아웃";
  profile: ProfileConfig;
  onClick?: MouseEventHandler<HTMLDivElement>;
}

const Profile = ({ types, profile, ...rest }: Props) => {
  const dispatch = useAppDispatch();
  const { profileImage, nickname, statusMessage } = profile;

  const handleClick = () => {
    dispatch(clearFriendInfo());
  };

  return (
    <>
      <div className={styles.profile_container}>
        <div className={styles.profile_img} onClick={handleClick}>
          <Avatar src={profileImage} sx={{ width: 80, height: 80 }} />
        </div>
        <div className={styles.profile_content} onClick={handleClick}>
          <Text types="semi-medium" bold>
            {nickname}
          </Text>
          <Text types="default">{statusMessage}</Text>
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
