import React, { Dispatch, SetStateAction, useState } from "react";
import styles from "@styles/social/Social.module.scss";
import Text from "@components/common/Text";
import { SocialListType } from "@components/social/CardList";
import { DeleteOutline } from "@mui/icons-material";
import Avatar from "@components/common/Avatar";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";
import { useNavigate } from "react-router-dom";
import { setFriendInfo } from "@store/friendSlice";
import { setDayDate } from "@store/planner/daySlice";

interface Props {
  item: SocialListType;
  idx: number;
  setDeleteItem: Dispatch<SetStateAction<{ id: number; idx: number }>>;
  handleDeleteModalOpen: () => void;
}

const SocialProfile = ({ idx, item, setDeleteItem, handleDeleteModalOpen }: Props) => {
  const navigator = useNavigate();
  const dispatch = useAppDispatch();
  const userName = useAppSelector(selectUserInfo).nickname;
  const { socialId, ...friend } = item;
  const mine = userName == friend.nickname;
  const { profileImage, statusMessage, nickname } = friend;

  const handleClickProfile = () => {
    dispatch(setFriendInfo(friend));
    navigator("/month");
  };

  return (
    <div className={styles["social-profile__container"]}>
      <div className={styles["social-profile__img"]} onClick={handleClickProfile}>
        <Avatar src={profileImage} />
      </div>
      <div className={styles["social-profile__content"]} onClick={handleClickProfile}>
        <Text types="semi-medium" bold>
          {nickname}
        </Text>
        <Text types="default">{statusMessage}</Text>
      </div>
      <div
        className={styles["social-profile__button"]}
        onClick={() => {
          setDeleteItem({ id: socialId, idx });
          handleDeleteModalOpen();
        }}
      >
        {mine && (
          <div>
            <DeleteOutline />
          </div>
        )}
      </div>
    </div>
  );
};

const CardItem = ({ idx, item, setDeleteItem, handleDeleteModalOpen }: Props) => {
  const navigator = useNavigate();
  const dispatch = useAppDispatch();
  const { dailyPlannerDay, ...user } = item;
  const [isLoading, setIsLoading] = useState(true);

  const handleClickImage = () => {
    dispatch(setDayDate(dailyPlannerDay));
    dispatch(setFriendInfo(user));
    navigator("/day");
  };

  return (
    <div className={styles["card-item"]}>
      <div className={styles["card-item__image-box"]} onClick={handleClickImage}>
        <img src={item.socialImage} onLoad={() => setIsLoading(false)} />
      </div>
      {isLoading && <div className={styles["skeleton-image"]} />}
      <div className={styles["card-item__profile"]}>
        <SocialProfile
          idx={idx}
          item={item}
          setDeleteItem={setDeleteItem}
          handleDeleteModalOpen={handleDeleteModalOpen}
        />
      </div>
    </div>
  );
};

export default CardItem;
