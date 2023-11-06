import React, { useEffect, useRef } from "react";
import styles from "@styles/common/Modal.module.scss";
import Text from "@components/common/Text";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import { useNavigate } from "react-router-dom";

const TokenExpiration = () => {
  return (
    <div className={styles["contents"]}>
      <WarningAmberRoundedIcon />
      <Text types="small">세션이 만료되었습니다.</Text>
      <Text types="small">계속하려면 다시 로그인하세요.</Text>
    </div>
  );
};

export default TokenExpiration;
