import React from "react";
import styles from "@styles/social/Social.module.scss";
import Text from "@components/common/Text";
import { SocialListType } from "@components/social/CardList";
import { DeleteOutline } from "@mui/icons-material";
import { Avatar } from "@mui/material";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";
import { useNavigate } from "react-router-dom";
import { setFriendDate, setFriendInfo } from "@store/friendSlice";

interface Props {
  item: SocialListType;
  idx: number;
  handleDelete: (idx: number, socialId: number) => void;
}

const SocialProfile = ({ idx, item, handleDelete }: Props) => {
  const navigator = useNavigate();
  const dispatch = useAppDispatch();
  const userName = useAppSelector(selectUserInfo).nickname;
  const { socialId, socialImage, dailyPlannerDay, ...user } = item;
  const mine = user.nickname == userName;
  const { profileImage, statusMessage, nickname } = user;

  const handleClickProfile = () => {
    dispatch(setFriendInfo(user));
    navigator("/month");
  };

  return (
    <div className={styles["social-profile__container"]} onClick={handleClickProfile}>
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
          <div onClick={() => handleDelete(idx, socialId)}>
            <DeleteOutline />
          </div>
        )}
      </div>
    </div>
  );
};

const CardItem = ({ idx, item, handleDelete }: Props) => {
  const navigator = useNavigate();
  const dispatch = useAppDispatch();
  const { socialId, socialImage, dailyPlannerDay, ...user } = item;

  const handleClickImage = () => {
    dispatch(setFriendDate(dailyPlannerDay));
    dispatch(setFriendInfo(user));
    navigator("/day");
  };

  return (
    <div className={styles["card-item"]}>
      <div className={styles["card-item__image-box"]} onClick={handleClickImage}>
        <img src={item.socialImage} />
      </div>
      <div className={styles["card-item__profile"]}>
        <SocialProfile idx={idx} item={item} handleDelete={handleDelete} />
      </div>
    </div>
  );
};

export default CardItem;
