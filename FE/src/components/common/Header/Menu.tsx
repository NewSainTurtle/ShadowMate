import React, { Children } from "react";
import { CalendarToday, EventNote, Today, Groups } from "@mui/icons-material";
import styles from "@styles/common/Header.module.scss";
import Text from "../Text";
import { NavLink } from "react-router-dom";

const values = [
  { icon: <CalendarToday />, message: "월별", link: "/month" },
  { icon: <EventNote />, message: "주별", link: "/week" },
  { icon: <Today />, message: "일별", link: "/day" },
  { icon: <Groups />, message: "소셜", link: "/social" },
];

const Menu = () => {
  return (
    <div className={styles.menu_container}>
      {values.map((item) => (
        <NavLink className={styles.menu_item} to={item.link}>
          {item.icon}
          <Text types="small">{item.message}</Text>
        </NavLink>
      ))}
    </div>
  );
};

export default Menu;
