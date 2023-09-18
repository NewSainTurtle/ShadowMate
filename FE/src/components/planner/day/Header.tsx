import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import Button from "@components/common/Button";
import dayjs from "dayjs";
import "dayjs/locale/ko";
dayjs.locale("ko");

const Header = () => {
  const { state } = useLocation();
  const date = state?.date || new Date();

  const titleDay = dayjs(date).format("YYYY년 M월 DD일 ddd요일");
  const [isHeartClick, setIsHeartClick] = useState(false);
  const [heartNum, setHeartNum] = useState(50); // 임시 좋아요 수
  const [isSocialClick, setIsSocialClick] = useState(false);

  function heartClick() {
    setIsHeartClick(!isHeartClick);
    if (isHeartClick) setHeartNum(heartNum - 1);
    else setHeartNum(heartNum + 1);
  }

  function socialClick() {
    setIsSocialClick(!isSocialClick);
  }

  return (
    <div className={styles["planner-header"]}>
      <div className={styles["planner-header__date"]}>
        <Dday comparedDate={date} />
        <Text types="semi-large" bold>
          {titleDay}
        </Text>
      </div>
      <div className={`${styles["planner-header__button"]} ${isHeartClick && styles["button__visit"]}`}>
        <div className={`${isHeartClick && styles["button__visit"]}`}>
          <Button types="red" onClick={() => heartClick()}>
            ♥ {heartNum}
          </Button>
        </div>
        <div className={`${isSocialClick && styles["button__visit"]}`}>
          <Button types="blue" onClick={() => socialClick()}>
            소설공유
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Header;
