import React from "react";
import { useNavigate } from "react-router-dom";
import { ArrowBackIosNew } from "@mui/icons-material";
import Toggle from "@components/common/Header/Toggle";
import { Box, Stack } from "@mui/material";

const AuthHeader = () => {
  const navigate = useNavigate();
  const onClickBtn = () => {
    if (location.pathname == "/signup") navigate("/login");
    else navigate("/");
  };

  return (
    <Stack sx={{ position: "fixed", width: 1, mt: 1 }} direction="row" justifyContent="space-between">
      <ArrowBackIosNew sx={{ pl: 1 }} fontSize="small" onClick={onClickBtn} />
      <Box sx={{ pr: 3 }}>
        <Toggle />
      </Box>
    </Stack>
  );
};

export default AuthHeader;
