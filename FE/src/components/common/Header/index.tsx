import React from "react";
import styles from "@styles/common/Header.module.scss";
import Toggle from "./Toggle";
import Menu from "./Menu";
import Avatar from "@components/common/Avatar";
import { NavLink } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";
import { clearFriendInfo } from "@store/friendSlice";

const Header = () => {
  const dispatch = useAppDispatch();
  const handleClear = () => dispatch(clearFriendInfo());
  const profileImage = useAppSelector(selectUserInfo).profileImage;

  return (
    <div className={styles.header_container}>
      <NavLink to="/month">
        <div className={styles.header_logo} onClick={handleClear}>
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
