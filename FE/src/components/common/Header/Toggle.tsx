import React, { ChangeEvent, useState } from "react";
import styles from "@styles/common/Header.module.scss";
import { WbSunny, NightlightRound } from "@mui/icons-material";
import { styled } from "@mui/material/styles";
import { Stack, Switch } from "@mui/material";

export const SwitchButton = styled(Switch)(({ theme }) => ({
  width: 28,
  height: 14,
  padding: 0,
  display: "flex",
  "&:active": {
    "& .MuiSwitch-thumb": {
      width: 15,
    },
    "& .MuiSwitch-switchBase.Mui-checked": {
      transform: "translateX(9px)",
    },
  },
  "& .MuiSwitch-switchBase": {
    padding: 2,
    "&.Mui-checked": {
      transform: "translateX(13px)",
      color: "#f3ebfb",
      "& + .MuiSwitch-track": {
        opacity: 1,
        backgroundColor: "black", //
      },
    },
  },
  "& .MuiSwitch-thumb": {
    boxShadow: "0 2px 4px 0 rgb(0 35 11 / 20%)",
    width: 10,
    height: 10,
    borderRadius: 6,
    transition: theme.transitions.create(["width"], {
      duration: 200,
    }),
  },
  "& .MuiSwitch-track": {
    borderRadius: 16 / 2,
    opacity: 1,
    backgroundColor: theme.palette.mode === "dark" ? "rgba(255,255,255,.35)" : "rgba(0,0,0,.25)",
    boxSizing: "border-box",
  },
}));

const Toggle = () => {
  const [checked, setChecked] = useState(false);
  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    setChecked(!checked);
    e.target.checked
      ? document.documentElement.setAttribute("data-theme", "dark")
      : document.documentElement.setAttribute("data-theme", "light");
  };

  return (
    <div className={styles.toggle}>
      <Stack direction="row" spacing={1} alignItems="center">
        <span className={styles.toggle_icon}>{checked ? <NightlightRound /> : <WbSunny />}</span>
        <SwitchButton color="info" checked={checked} onChange={onChange} />
      </Stack>
    </div>
  );
};

export default Toggle;
