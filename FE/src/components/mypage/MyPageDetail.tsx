import React, { ReactNode } from "react";
import styles from "./MyPage.module.scss";
import Text from "@components/common/Text";
import SaveIcon from "@mui/icons-material/Save";

interface Props {
  children: ReactNode;
}

/**
 * MyPage - 다이어리, 디데이 설정 중 오른쪽 사이드
 */

const MyPageDetail = ({ children }: Props) => {
  return (
    <div className={styles["frame__container"]}>
      <div className={styles["frame__title"]}>
        <Text>상세 설정</Text>
      </div>
      {children}
      <div className={styles["frame__button"]}>
        <SaveIcon />
        <Text>저장</Text>
      </div>
    </div>
  );
};

export default MyPageDetail;
