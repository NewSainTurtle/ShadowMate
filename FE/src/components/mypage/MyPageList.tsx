import React, { ReactNode } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import AddIcon from "@mui/icons-material/Add";

interface Props {
  children: ReactNode;
  handleAdd: (title: string) => void;
  title: string;
}

/**
 * MyPage - 다이어리, 디데이 설정 중 왼쪽 사이드
 */

const MyPageList = ({ children, handleAdd, title }: Props) => {
  return (
    <div className={styles["frame__container"]}>
      <div className={styles["frame__title"]}>
        <Text>{title} 목록</Text>
      </div>
      {children}
      <div className={styles["frame__button"]}>
        <div className={styles["frame__button--add"]} onClick={() => handleAdd(title)}>
          <AddIcon />
          <Text>새 {title}</Text>
        </div>
      </div>
    </div>
  );
};

export default MyPageList;
