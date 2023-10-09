import React, { ChangeEvent, Dispatch, SetStateAction, useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import { ddayType } from "@util/planner.interface";
import { dateFormat } from "@util/getThisWeek";

interface Props {
  click: number;
  ddayList: ddayType[];
  input: ddayType;
  setInput: Dispatch<SetStateAction<ddayType>>;
  error: boolean;
}

const MyPageDday = ({ click, ddayList, input, setInput, error }: Props) => {
  const { ddayId, ddayTitle, ddayDate } = input;

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    setInput({ ...input, [name]: value });
  };

  useEffect(() => {
    setInput(ddayList[click]);
  }, [click]);

  return (
    <div className={styles["frame__contents"]}>
      <div className={styles["frame__line"]}>
        <Text>디데이 이름</Text>
        <Input
          error={error}
          name="ddayTitle"
          value={ddayTitle}
          placeholder="디데이 이름을 입력하세요."
          onChange={onChangeInput}
          helperText={"2 ~ 20자의 이름을 입력할 수 있습니다."}
        />
      </div>
      <div className={styles["frame__line"]}>
        <Text>디데이 날짜</Text>
        <Input name="ddayDate" value={dateFormat(ddayDate)} />
      </div>
    </div>
  );
};

export default MyPageDday;
