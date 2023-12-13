import React from "react";
import { CalendarToday, EventNote, Today, Groups } from "@mui/icons-material";
import styles from "@styles/common/Header.module.scss";
import Text from "@components/common/Text";
import { NavLink } from "react-router-dom";
import { useAppDispatch } from "@hooks/hook";
import { clearFriendInfo } from "@store/friendSlice";
import dayjs from "dayjs";
import { setDayDate } from "@store/planner/daySlice";

const values = [
  { icon: <CalendarToday />, message: "월별", link: "/month" },
  { icon: <EventNote />, message: "주별", link: "/week" },
  { icon: <Today />, message: "일별", link: `/day` },
  { icon: <Groups />, message: "소셜", link: "/social" },
];

const Menu = () => {
  const dispatch = useAppDispatch();

  const handleClear = (link: string) => {
    if (link == "/day") dispatch(setDayDate(dayjs().format("YYYY-MM-DD")));
    dispatch(clearFriendInfo());
  };

  return (
    <div className={styles.menu_container}>
      {values.map((item, idx) => (
        <NavLink className={styles.menu_item} to={item.link} key={idx} onClick={() => handleClear(item.link)}>
          {item.icon}
          <Text types="small">{item.message}</Text>
        </NavLink>
      ))}
    </div>
  );
};

export default Menu;
