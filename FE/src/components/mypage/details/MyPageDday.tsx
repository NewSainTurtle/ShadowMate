import React, { useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import { ddayType } from "@util/planner.interface";

interface Props {
  click: number;
  ddayList: ddayType[];
}

const MyPageDday = ({ click, ddayList }: Props) => {
  const [title, setTitle] = useState<string>(ddayList[click].ddayTitle);

  useEffect(() => {
    setTitle(ddayList[click].ddayTitle);
  }, [click]);

  return (
    <div className={styles["frame__contents"]}>
      <div className={styles["frame__line"]}>
        <Text>디데이 이름</Text>
        <Input name="title" value={title} placeholder="디데이 이름을 입력하세요." />
      </div>
      <div className={styles["frame__line"]}>
        <Text>디데이 날짜</Text>
      </div>
    </div>
  );
};

export default MyPageDday;
