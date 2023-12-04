import React from "react";
import styles from "@styles/common/Profile.module.scss";
import { ProfileConfig } from "@components/common/FriendProfile";
import Text from "@components/common/Text";
import Button from "@components/common/Button";
import Avatar from "@components/common/Avatar";
import { persistor } from "@hooks/configStore";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId, setLogout } from "@store/authSlice";
import { clearFriendInfo, selectFriendId } from "@store/friendSlice";
import SettingsIcon from "@mui/icons-material/Settings";
import { useNavigate } from "react-router-dom";

interface Props {
  types: "기본" | "로그아웃";
  profile: ProfileConfig;
}

const Profile = ({ types, profile }: Props) => {
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const dispatch = useAppDispatch();
  const navigator = useNavigate();
  const { profileImage, nickname, statusMessage } = profile;

  const handleLogout = () => {
    dispatch(setLogout());
    dispatch(clearFriendInfo());
    setTimeout(() => {
      persistor.purge();
    }, 200);
  };

  const handleProfileBtn = () => {
    navigator("/mypage");
  };

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
              기본:
                friendId === userId ? (
                  <div className={styles.profile_button} onClick={handleProfileBtn}>
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
