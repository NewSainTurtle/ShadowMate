import React, { ReactNode } from "react";
import { Box, Modal as MuiModal } from "@mui/material";
import styles from "@styles/common/Modal.module.scss";
import Text from "../Text";

interface ModalProps {
  types: "noBtn" | "oneBtn" | "twoBtn";
  open: boolean;
  onClose?: () => void;
  onClick?: () => void;
  children: ReactNode;
}

const Modal = ({ types, open, onClose, onClick, children, ...rest }: ModalProps) => {
  return (
    <MuiModal open={open} onClose={onClose} disableAutoFocus {...rest}>
      <div className={styles["contents"]}>
        <>{children}</>
        {types != "noBtn" && (
          <div className={styles["button"]}>
            {types == "twoBtn" && (
              <div className={styles["button__cancel"]} onClick={onClose}>
                <Text types="small">취소</Text>
              </div>
            )}
            <div className={styles["button__save"]} onClick={onClick}>
              <Text types="small">저장</Text>
            </div>
          </div>
        )}
      </div>
    </MuiModal>
  );
};

export default Modal;
