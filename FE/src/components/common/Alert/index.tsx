import React from "react";
import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";

interface Props {
  open: boolean;
  onClose: () => void;
  message: string;
}

const Alert = ({ open, onClose, message, ...rest }: Props) => {
  return (
    <Snackbar
      anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      open={open}
      onClose={onClose}
      autoHideDuration={2000}
    >
      <MuiAlert onClose={onClose} severity="success" sx={{ width: "100%" }}>
        {message}
      </MuiAlert>
    </Snackbar>
  );
};

export default Alert;
