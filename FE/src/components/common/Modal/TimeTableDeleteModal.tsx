import React from "react";
import styles from "@styles/common/Modal.module.scss";
import Text from "@components/common/Text";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";

const TimeTableDeleteModal = () => {
  return (
    <div className={styles["contents"]}>
      <WarningAmberRoundedIcon />
      <Text types="small">상태 변경 시 기록된 타임테이블이 삭제됩니다.</Text>
      <Text types="small">계속하시겠습니까?</Text>
    </div>
  );
};

export default TimeTableDeleteModal;
