import React from "react";
import styles from "@styles/common/Header.module.scss";
import Toggle from "./Toggle";
import Menu from "./Menu";
import { Avatar } from "@mui/material";
import { NavLink } from "react-router-dom";

const Header = () => {
  return (
    <div className={styles.header_container}>
      <NavLink to="/month">
        <div className={styles.header_logo}>
          <span>Shadow</span>
          <br />
          <span>Mate</span> 
        </div>
      </NavLink>
      <Menu />
      <div className={styles.header_profile}>
        <Toggle />
        <NavLink to="/mypage">
          <Avatar src="https://avatars.githubusercontent.com/u/85155789?v=4" />
        </NavLink>
      </div>
    </div>
  );
};

export default Header;
