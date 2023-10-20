import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import Button from "@components/common/Button";
import FriendProfile from "@components/common/FriendProfile";
import { NavigateBefore, NavigateNext } from "@mui/icons-material";
import { useDispatch } from "react-redux";
import { useAppSelector } from "@hooks/hook";
import { setDate, selectDate } from "@store/planner/daySlice";
import { todoData_friend } from "@util/data/DayTodos";
import dayjs from "dayjs";
import "dayjs/locale/ko";
dayjs.locale("ko");

const FriendHeader = ({ heart }: { heart: number }) => {
  const navigate = useNavigate();
  const [isHeartClick, setIsHeartClick] = useState(false);
  const [heartNum, setHeartNum] = useState(heart);

  function heartClick() {
    setIsHeartClick(!isHeartClick);
    if (isHeartClick) setHeartNum(heartNum - 1);
    else setHeartNum(heartNum + 1);
  }

  function socialClick() {
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
        <Button types="gray" onClick={() => socialClick()}>
          주간보기
        </Button>
      </div>
      <FriendProfile types={"아이콘"} profile={todoData_friend} />
    </div>
  );
};

const MyHeader = ({ heart }: { heart: number }) => {
  const [isSocialClick, setIsSocialClick] = useState(false);

  function socialClick() {
    setIsSocialClick(!isSocialClick);
  }

  return (
    <div className={styles["planner-header__default"]}>
      <Button types="red">♥ {heart}</Button>
      <div className={`${isSocialClick && styles["button__visit"]}`}>
        <Button types="blue" onClick={() => socialClick()}>
          소설공유
        </Button>
      </div>
    </div>
  );
};

const Header = () => {
  const dispatch = useDispatch();
  const date = useAppSelector(selectDate);
  const titleDay = dayjs(date).format("YYYY년 M월 DD일 ddd요일");
  const heartNum = 50; // 임시 좋아요 수
  const isFriend = false;

  const moveDate = (n: -1 | 0 | 1) => {
    const newDate = n == 0 ? dayjs() : dayjs(date).add(n, "day");
    dispatch(setDate(newDate));
  };

  return (
    <div className={styles["planner-header"]}>
      <div className={styles["planner-header__date"]}>
        <Dday comparedDate={date} />
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
      {isFriend ? <FriendHeader heart={heartNum} /> : <MyHeader heart={heartNum} />}
    </div>
  );
};

export default Header;
