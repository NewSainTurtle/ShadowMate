import React from "react";
import styles from "@styles/common/Loading.module.scss";
import { createPortal } from "react-dom";
import { CircularProgress } from "@mui/material";

const Loading = () => {
  return createPortal(
    <div className={styles["loading__background"]}>
      <CircularProgress color="inherit" />
    </div>,
    document.getElementById("loading") as HTMLElement,
  );
};

export default Loading;
