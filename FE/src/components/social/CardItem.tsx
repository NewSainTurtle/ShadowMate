import React, { MouseEventHandler } from "react";
import styles from "@styles/social/Social.module.scss";
import Text from "@components/common/Text";
import { SocialListType } from "@components/social/CardList";
import { ProfileConfig } from "@components/common/FriendProfile";
import { DeleteOutline } from "@mui/icons-material";
import { Avatar } from "@mui/material";
import { useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo } from "@store/authSlice";
import { socialApi } from "@api/Api";

interface Props {
  item: SocialListType;
}

const SocialProfile = ({ item }: Props) => {
  const userId = useAppSelector(selectUserId);
  const userName = useAppSelector(selectUserInfo).nickname;
  const { socialId, socialImage, dailyPlannerDay, ...user } = item;
  const mine = user.nickname == userName;
  const { profileImage, statusMessage, nickname } = user;

  const handleDelete = () => {
    socialApi.delete(userId, socialId).catch((err) => console.error(err));
  };

  return (
    <div className={styles["social-profile__container"]}>
      <div className={styles["social-profile__img"]}>
        <Avatar src={profileImage} />
      </div>
      <div className={styles["social-profile__content"]}>
        <Text types="semi-medium" bold>
          {nickname}
        </Text>
        <Text types="default">{statusMessage}</Text>
      </div>
      <div className={styles["social-profile__button"]}>
        {mine && (
          <div onClick={handleDelete}>
            <DeleteOutline />
          </div>
        )}
      </div>
    </div>
  );
};

const CardItem = ({ item }: Props) => {
  return (
    <div className={styles["card-item"]}>
      <div className={styles["card-item__image-box"]}>
        <img src={item.socialImage} />
      </div>
      <div className={styles["card-item__profile"]}>
        <SocialProfile item={item} />
      </div>
    </div>
  );
};

export default CardItem;
