import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import Button from "@components/common/Button";
import FriendProfile from "@components/common/FriendProfile";
import { NavigateBefore, NavigateNext } from "@mui/icons-material";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { setDayDate, selectDayDate, selectDayInfo, setDayLike, setDayInfo } from "@store/planner/daySlice";
import dayjs from "dayjs";
import "dayjs/locale/ko";
import { selectFriendInfo } from "@store/friendSlice";
import { plannerApi } from "@api/Api";
import { selectUserId } from "@store/authSlice";
dayjs.locale("ko");

const FriendHeader = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const date = useAppSelector(selectDayDate);
  const friendUserId = useAppSelector(selectFriendInfo).userId;
  const { likeCount, like } = useAppSelector(selectDayInfo);
  const friendInfo = useAppSelector(selectFriendInfo);

  function heartClick() {
    if (like) {
      plannerApi
        .cancelLikes(friendUserId, { date })
        .then(() => dispatch(setDayLike(false)))
        .catch((err) => console.error(err));
    } else {
      plannerApi
        .likes(friendUserId, { date, anotherUserId: userId })
        .then(() => dispatch(setDayLike(true)))
        .catch((err) => console.error(err));
    }
  }

  function weekClick() {
    navigate("/week");
  }

  return (
    <div className={`${styles["planner-header__friend"]} ${like ? styles["button__visit"] : ""}`}>
      <div>
        <div className={`${like ? styles["button__visit"] : ""}`}>
          <Button types="red" onClick={() => heartClick()}>
            ♥ {likeCount}
          </Button>
        </div>
        <Button types="gray" onClick={() => weekClick()}>
          주별보기
        </Button>
      </div>
      <FriendProfile types={"기본"} profile={friendInfo} />
    </div>
  );
};

const MyHeader = ({ socialClick }: { socialClick: () => Promise<void> }) => {
  const dispatch = useAppDispatch();
  const dayPlannerInfo = useAppSelector(selectDayInfo);
  const { plannerAccessScope, likeCount, shareSocial, dailyTodos } = dayPlannerInfo;

  function handleClick() {
    if (!shareSocial) {
      dispatch(setDayInfo({ ...dayPlannerInfo, shareSocial: true }));
      socialClick();
    } else dispatch(setDayInfo({ ...dayPlannerInfo, shareSocial: false }));
  }

  return (
    <div className={styles["planner-header__default"]}>
      <Button types="red" disabled>
        ♥ {likeCount}
      </Button>
      {plannerAccessScope == "전체공개" && (
        <div className={`${shareSocial ? styles["button__visit"] : ""}`}>
          <Button types="blue" onClick={() => handleClick()} disabled={!dailyTodos.length}>
            소셜공유
          </Button>
        </div>
      )}
    </div>
  );
};

interface Props {
  isFriend?: boolean;
  socialClick: () => Promise<void>;
}

const Header = ({ isFriend, socialClick }: Props) => {
  const dispatch = useAppDispatch();
  const date = useAppSelector(selectDayDate);
  const { dday: nearDate } = useAppSelector(selectDayInfo);
  const titleDay = dayjs(date).format("YYYY년 M월 DD일 ddd요일");

  const moveDate = (n: -1 | 0 | 1) => {
    const newDate = n == 0 ? dayjs() : dayjs(date).add(n, "day");
    dispatch(setDayDate(newDate.format("YYYY-MM-DD")));
  };

  return (
    <div className={styles["planner-header"]}>
      <div className={styles["planner-header__date"]}>
        <Dday nearDate={nearDate} comparedDate={date} />
        <div>
          <Text types="semi-large" bold>
            {titleDay}
          </Text>
          <div className={styles["date-move"]}>
            <div onClick={() => moveDate(-1)}>
              <NavigateBefore />
            </div>
            <div onClick={() => moveDate(0)}>
              <Text bold>today</Text>
            </div>
            <div onClick={() => moveDate(1)}>
              <NavigateNext />
            </div>
          </div>
        </div>
      </div>
      {isFriend ? <FriendHeader /> : <MyHeader socialClick={socialClick} />}
    </div>
  );
};

export default Header;
