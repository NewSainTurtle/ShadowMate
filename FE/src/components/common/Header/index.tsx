import React from "react";
import styles from "@styles/common/Header.module.scss";
import Toggle from "./Toggle";
import Menu from "./Menu";
import Avatar from "@components/common/Avatar";
import Text from "@components/common/Text";
import { NavLink } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";
import { clearFriendInfo } from "@store/friendSlice";
import { Assignment } from "@mui/icons-material";
const Header = () => {
  const dispatch = useAppDispatch();
  const handleClear = () => dispatch(clearFriendInfo());
  const profileImage = useAppSelector(selectUserInfo).profileImage;

  return (
    <div className={styles["header_container"]}>
      <NavLink to="/month">
        <div className={styles["header_logo"]} onClick={handleClear}>
          <span>Shadow</span>
          <br />
          <span>Mate</span>
        </div>
      </NavLink>
      <div
        className={styles["header_questionnaire"]}
        onClick={() => window.open("https://forms.gle/UAztt4dsGeqKWkHy9")}
      >
        <Assignment />
        <Text types="small">설문</Text>
      </div>

      <Menu />
      <div className={styles["header_profile"]}>
        <Toggle />
        <NavLink to="/mypage">
          <Avatar src={profileImage} />
        </NavLink>
      </div>
    </div>
  );
};

export default Header;
