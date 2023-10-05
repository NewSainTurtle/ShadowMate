import React, { useEffect, useState } from "react";
import styles from "../MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import { DdayConfig } from "../MyPageFrame";

interface Props {
  click: number;
  ddayList: DdayConfig[];
}

const MyPageDday = ({ click, ddayList }: Props) => {
  const [title, setTitle] = useState<string>(ddayList[click].title);

  useEffect(() => {
    setTitle(ddayList[click].title);
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
