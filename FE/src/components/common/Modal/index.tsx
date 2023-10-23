import { Box, Modal as MuiModal } from "@mui/material";
import React, { ReactNode } from "react";

interface ModalProps {
  open: boolean;
  onClose: () => void;
  children: ReactNode;
}

const style = {
  position: "absolute" as "absolute",
  top: "45%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 400,
  bgcolor: "background.paper",
};

const Modal = ({ open, onClose, children, ...rest }: ModalProps) => {
  return (
    <MuiModal open={open} onClose={onClose} disableAutoFocus {...rest}>
      <Box sx={style}>{children}</Box>
    </MuiModal>
  );
};

export default Modal;
