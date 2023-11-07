import React from "react";
import styles from "@styles/common/Alert.module.scss";
import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";

interface Props {
  types: "success" | "error";
  open: boolean;
  onClose: () => void;
  message: string;
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
        {message}
      </MuiAlert>
    </Snackbar>
  );
};

export default Alert;
