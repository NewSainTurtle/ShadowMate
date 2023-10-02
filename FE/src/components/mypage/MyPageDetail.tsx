import React, { ReactNode } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import SaveIcon from "@mui/icons-material/Save";

interface Props {
  children: ReactNode;
  saveEditInfo: () => void;
}

/**
 * MyPage - 다이어리, 디데이 설정 중 오른쪽 사이드
 */

const MyPageDetail = ({ children, saveEditInfo }: Props) => {
  return (
    <div className={styles["frame__container"]}>
      <div className={styles["frame__title"]}>
        <Text>상세 설정</Text>
      </div>
      {children}
      <div className={styles["frame__button"]} onClick={saveEditInfo}>
        <SaveIcon />
        <Text>저장</Text>
      </div>
    </div>
  );
};

export default MyPageDetail;
