import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Category from "@components/mypage/details/diary/Category";
import Dday from "@components/mypage/details/diary/Dday";
import Routine from "@components/mypage/details/diary/Routine";

interface Props {
  title: string;
  routineError: boolean;
}

const NewItemContainer = ({ title, routineError }: Props) => {
  return (
    <div className={styles["frame__container"]}>
      <div className={styles["frame__title"]}>
        <Text>새 {title} 생성</Text>
      </div>
      <div className={styles["frame__modal"]}>
        {
          {
            카테고리: <Category newItem />,
            디데이: <Dday newItem />,
            루틴: <Routine newItem dayError={routineError} />,
          }[title]
        }
      </div>
    </div>
  );
};

export default NewItemContainer;
