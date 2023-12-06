import React from "react";
import styles from "@styles/common/Modal.module.scss";
import Text from "@components/common/Text";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";

const DiaryConfirmModal = () => {
  return (
    <div className={styles["contents"]}>
      <WarningAmberRoundedIcon />
      <Text types="small">
        <>전체 공개 및 친구 공개 상태에서 비공개 전환 시,</>
        <br />
        <>소셜에 공유된 게시글이 숨김 처리됩니다.</>
      </Text>
      <Text types="small">계속 진행하시겠습니까?</Text>
    </div>
  );
};

export default DiaryConfirmModal;
