import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import Button from "@components/common/Button";
import FriendProfile from "@components/common/FriendProfile";
import { NavigateBefore, NavigateNext } from "@mui/icons-material";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { setDate, selectDate, selectDayInfo } from "@store/planner/daySlice";
import { todoData_friend } from "@util/data/DayTodos";
import dayjs from "dayjs";
import "dayjs/locale/ko";
dayjs.locale("ko");

const FriendHeader = () => {
  const navigate = useNavigate();
  const { likeCount, like } = useAppSelector(selectDayInfo);
  const [heartNum, setHeartNum] = useState(likeCount);
  const [isHeartClick, setIsHeartClick] = useState(like);

  function heartClick() {
    setIsHeartClick(!isHeartClick);
    if (isHeartClick) setHeartNum(heartNum - 1);
    else setHeartNum(heartNum + 1);
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
  const [isSocialClick, setIsSocialClick] = useState(false);
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
      <div className={`${isSocialClick && styles["button__visit"]}`}>
        <Button types="blue" onClick={() => handleClick()}>
          소설공유
        </Button>
      </div>
    </div>
  );
};

interface Props {
  isFriend?: boolean;
  socialClick: () => Promise<void>;
}

const Header = ({ isFriend, socialClick }: Props) => {
  const dispatch = useAppDispatch();
  const date = useAppSelector(selectDate);
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
