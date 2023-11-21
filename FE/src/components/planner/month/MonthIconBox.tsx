import React, { useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";
import Introduction from "@components/planner/month/MonthDetailInfo/Introduction";
import GuestBook from "@components/planner/month/MonthDetailInfo/GuestBook";
import Statistics from "@components/planner/month/MonthDetailInfo/Statistics";
import MoodIcon from "@mui/icons-material/Mood";
import CalendarTodayIcon from "@mui/icons-material/CalendarToday";
import PeopleIcon from "@mui/icons-material/People";
import FavoriteIcon from "@mui/icons-material/Favorite";
import MessageIcon from "@mui/icons-material/Message";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { selectFriendId } from "@store/friendSlice";

interface Props {
  types: "소개글" | "월별통계" | "팔로우" | "좋아요" | "방명록";
}

const IconType = {
  소개글: {
    icon: <MoodIcon />,
    color: "#FFAF6A",
  },
  월별통계: {
    icon: <CalendarTodayIcon />,
    color: "#FFE352",
  },
  팔로우: {
    icon: <PeopleIcon />,
    color: "#95C1F5",
  },
  좋아요: {
    icon: <FavoriteIcon />,
    color: "#E88080",
  },
  방명록: {
    icon: <MessageIcon />,
    color: "#637F69",
  },
};

const MonthIconBox = ({ types }: Props) => {
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const [isEdit, setIsEdit] = useState<boolean>(false);
  return (
    <div className={styles["icon-box"]}>
      <div className={styles["icon-box__title"]}>
        <div>
          <div style={{ backgroundColor: `${IconType[types].color}` }}>{IconType[types].icon}</div>
          <Text>{types}</Text>
        </div>
        {types === "소개글" &&
          friendId === userId &&
          (isEdit ? <SaveIcon onClick={() => setIsEdit(false)} /> : <EditIcon onClick={() => setIsEdit(true)} />)}
      </div>
      {types === "소개글" ? (
        <Introduction isEdit={isEdit} setIsEdit={setIsEdit} />
      ) : (
        <>{types === "방명록" ? <GuestBook /> : <Statistics types={types} />}</>
      )}
    </div>
  );
};

export default MonthIconBox;
