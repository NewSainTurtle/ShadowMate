import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import Button from "@components/common/Button";
import FriendProfile from "@components/common/FriendProfile";
import { NavigateBefore, NavigateNext } from "@mui/icons-material";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { setDate, selectDayDate, selectDayInfo } from "@store/planner/daySlice";
import { todoData_friend } from "@util/data/DayTodos";
import dayjs from "dayjs";
import "dayjs/locale/ko";
import { selectFriendInfo } from "@store/friendSlice";
import { plannerApi } from "@api/Api";
import { selectUserId } from "@store/authSlice";
dayjs.locale("ko");

const FriendHeader = () => {
  const navigate = useNavigate();
  const userId = useAppSelector(selectUserId);
  const date = useAppSelector(selectDayDate);
  const friendUserId = useAppSelector(selectFriendInfo).userId;
  const { likeCount, like } = useAppSelector(selectDayInfo);
  const [heartNum, setHeartNum] = useState(likeCount);
  const [isHeartClick, setIsHeartClick] = useState(like);

  function heartClick() {
    if (isHeartClick) {
      plannerApi
        .cancelLikes(friendUserId, { date })
        .then(() => setHeartNum(heartNum - 1))
        .catch((err) => console.error(err));
    } else {
      plannerApi
        .likes(friendUserId, { date, anotherUserId: userId })
        .then(() => setHeartNum(heartNum + 1))
        .catch((err) => console.error(err));
    }
    setIsHeartClick(!isHeartClick);
  }

  function weekClick() {
    navigate("/week");
  }

  return (
    <div className={`${styles["planner-header__friend"]} ${isHeartClick && styles["button__visit"]}`}>
      <div>
        <div className={`${isHeartClick && styles["button__visit"]}`}>
          <Button types="red" onClick={() => heartClick()}>
            ♥ {heartNum}
          </Button>
        </div>
        <Button types="gray" onClick={() => weekClick()}>
          주간보기
        </Button>
      </div>
      <FriendProfile types={"아이콘"} profile={todoData_friend} />
    </div>
  );
};

const MyHeader = ({ socialClick }: { socialClick: () => Promise<void> }) => {
  const plannerAccessScope = useAppSelector(selectDayInfo).plannerAccessScope;
  const shareSocial = useAppSelector(selectDayInfo).shareSocial;
  const [isSocialClick, setIsSocialClick] = useState(shareSocial);
  const { likeCount } = useAppSelector(selectDayInfo);

  function handleClick() {
    if (!isSocialClick) {
      socialClick();
    }
    setIsSocialClick(!isSocialClick);
  }

  return (
    <div className={styles["planner-header__default"]}>
      <Button types="red" disabled>
        ♥ {likeCount}
      </Button>
      {plannerAccessScope == "전체공개" && (
        <div className={`${isSocialClick ? styles["button__visit"] : ""}`}>
          <Button types="blue" onClick={() => handleClick()}>
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
    const newDate = n == 0 ? dayjs().format("YYYY-MM-DD") : dayjs(date).add(n, "day").format("YYYY-MM-DD");
    dispatch(setDate(newDate));
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
