import React from "react";
import { useNavigate } from "react-router-dom";
import { ArrowBackIosNew } from "@mui/icons-material";
import { Stack } from "@mui/material";

const AuthHeader = () => {
  const navigate = useNavigate();
  const onClickBtn = () => {
    if (location.pathname == "/signup") navigate("/login");
    else navigate("/");
  };

  return (
    <Stack sx={{ position: "fixed", width: 1, mt: 2, ml: 2 }} direction="row" justifyContent="space-between">
      <ArrowBackIosNew sx={{ cursor: "pointer" }} fontSize="small" onClick={onClickBtn} />
    </Stack>
  );
};

export default AuthHeader;
