import React from "react";
import styles from "@styles/common/Alert.module.scss";
import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";

interface Props {
  types: "success" | "error" | "other";
  open: boolean;
  onClose: () => void;
  message?: string;
}

const Alert = ({ types, open, onClose, message, ...rest }: Props) => {
  return (
    <Snackbar
      anchorOrigin={{ vertical: "top", horizontal: "center" }}
      open={open}
      onClose={onClose}
      autoHideDuration={2000}
    >
      <MuiAlert icon={false} className={styles[`alert__${types}`]} onClose={onClose}>
        {{ success: "저장되었습니다.", error: "저장에 실패했습니다. 다시 시도해주세요.", other: `${message}` }[types]}
      </MuiAlert>
    </Snackbar>
  );
};

export default Alert;
