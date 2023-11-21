import React from "react";
import styles from "@styles/common/Profile.module.scss";
import { ProfileConfig } from "@components/common/FriendProfile";
import Text from "@components/common/Text";
import Button from "@components/common/Button";
import { Avatar } from "@mui/material";
import { persistor } from "@hooks/configStore";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId, setLogout } from "@store/authSlice";
import { selectFriendId } from "@store/friendSlice";
import SettingsIcon from "@mui/icons-material/Settings";

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
              기본:
                friendId === userId ? (
                  <div className={styles.profile_button}>
                    <SettingsIcon />
                  </div>
                ) : (
                  <></>
                ),
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
