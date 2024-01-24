import React, { Dispatch, SetStateAction } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Category from "@components/mypage/details/diary/Category";
import Dday from "@components/mypage/details/diary/Dday";
import Routine from "@components/mypage/details/diary/Routine";
import { RoutineErrorConfig } from "@components/mypage/MyPageFrame";

interface Props {
  title: string;
  routineError: RoutineErrorConfig;
  setRoutineError: Dispatch<SetStateAction<RoutineErrorConfig>>;
}

const NewItemContainer = ({ title, routineError, setRoutineError }: Props) => {
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
            루틴: <Routine newItem routineError={routineError} setRoutineError={setRoutineError} />,
          }[title]
        }
      </div>
    </div>
  );
};

export default NewItemContainer;
