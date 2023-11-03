import React from "react";
import styles from "@styles/common/Header.module.scss";
import Toggle from "./Toggle";
import Menu from "./Menu";
import { Avatar } from "@mui/material";
import { NavLink } from "react-router-dom";
import { useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";

const Header = () => {
  const profileImage = useAppSelector(selectUserInfo).profileImage;

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
          <Avatar src={profileImage} />
        </NavLink>
      </div>
    </div>
  );
};

export default Header;
