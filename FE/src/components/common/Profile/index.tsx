import React, { MouseEventHandler } from "react";
import styles from "@styles/common/Profile.module.scss";
import { ProfileConfig } from "../FriendProfile";
import Text from "../Text";
import Button from "../Button";
import { Avatar } from "@mui/material";
import { persistor } from "@hooks/configStore";
import { useAppDispatch } from "@hooks/hook";
import { setLogout } from "@store/authSlice";

interface Props {
  types: "기본" | "로그아웃";
  profile: ProfileConfig;
}

const Profile = ({ types, profile }: Props) => {
  const dispatch = useAppDispatch();
  const { profileImage, nickname, statusMessage } = profile;

  const handleLogout = () => {
    dispatch(setLogout());
    setTimeout(() => {
      persistor.purge();
    }, 200);
  };

  return (
    <>
      <div className={styles.profile_container}>
        <div className={styles.profile_img}>
          <Avatar src={profileImage} sx={{ width: 80, height: 80 }} />
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
              기본: <></>,
              로그아웃: (
                <div className={styles.profile_button}>
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
