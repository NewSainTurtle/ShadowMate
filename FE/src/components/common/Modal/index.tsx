import React, { ReactNode } from "react";
import { Box, Modal as MuiModal } from "@mui/material";
import styles from "@styles/common/Modal.module.scss";
import Text from "../Text";

interface ModalProps {
  types: "noBtn" | "oneBtn" | "twoBtn";
  open: boolean;
  onClose?: () => void;
  onClick?: () => void;
  onClickMessage?: string;
  prevent?: boolean;
  children: ReactNode;
}

const Modal = ({ types, open, onClose, onClick, onClickMessage, prevent, children, ...rest }: ModalProps) => {
  const preventClose = (reason: string) => {
    if (reason && reason === "backdropClick") return;
  };

  return (
    <MuiModal open={open} onClose={prevent ? preventClose : onClose} disableAutoFocus {...rest}>
      <div className={styles["container"]}>
        <>{children}</>
        {types != "noBtn" && (
          <div className={styles["button"]}>
            {types == "twoBtn" && (
              <div className={styles["button__cancel"]} onClick={onClose}>
                <Text types="small">취소</Text>
              </div>
            )}
            <div className={styles["button__save"]} onClick={onClick}>
              <Text types="small">{onClickMessage}</Text>
            </div>
          </div>
        )}
      </div>
    </MuiModal>
  );
};

Modal.defaultProps = {
  onClickMessage: "저장",
  prevent: false,
};

export default Modal;
