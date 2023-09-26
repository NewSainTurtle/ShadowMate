import React, { MouseEventHandler } from "react";
import styles from "@styles/social/social.module.scss";
import Text from "@components/common/Text";
import { SocialDataType } from "@components/social/CardList";
import { ProfileConfig } from "@components/common/FriendProfile";
import { DeleteOutline } from "@mui/icons-material";
import { Avatar } from "@mui/material";
import { userNickname } from "@util/data/SocialData";

interface Props {
  item: SocialDataType;
}

interface SocialProfileProps {
  mine: boolean;
  profile: ProfileConfig;
  onClick?: MouseEventHandler<HTMLDivElement>;
}

const SocialProfile = ({ mine, profile, ...rest }: SocialProfileProps) => {
  const { src, message, nickname } = profile;

  return (
    <div className={styles["social-profile__container"]}>
      <div className={styles["social-profile__img"]}>
        <Avatar src={src} />
      </div>
      <div className={styles["social-profile__content"]}>
        <Text types="semi-medium" bold>
          {nickname}
        </Text>
        <Text types="default">{message}</Text>
      </div>
      <div className={styles["social-profile__button"]} {...rest}>
        {mine && (
          <div>
            <DeleteOutline />
          </div>
        )}
      </div>
    </div>
  );
};

const CardItem = ({ item }: Props) => {
  const { socialImage, user } = item;
  const userName = userNickname;

  return (
    <div className={styles["card-item"]}>
      <div className={styles["card-item__image-box"]}>
        <img src={socialImage} />
      </div>
      <div className={styles["card-item__profile"]}>
        <SocialProfile profile={user} mine={userName == user.nickname} />
      </div>
    </div>
  );
};

export default CardItem;
