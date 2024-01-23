import React, { ReactNode } from "react";
import styles from "@styles/common/Modal.module.scss";
import Text from "@components/common/Text";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";

interface Props {
  children: ReactNode;
  types: "수정" | "삭제";
}

const RoutineUpdateModal = ({ children, types }: Props) => {
  return (
    <div className={styles["contents"]}>
      <WarningAmberRoundedIcon />
      <Text types="small">
        <>루틴을 {types}합니다.</>
        <br />
        <>루틴을 통해 작성했던 할 일도 {types}하시겠습니까?</>
      </Text>
      <div>{children}</div>
    </div>
  );
};

export default RoutineUpdateModal;
