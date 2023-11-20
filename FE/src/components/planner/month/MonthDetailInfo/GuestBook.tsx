import React from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";
import { Avatar } from "@mui/material";
import { useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo } from "@store/authSlice";
import { selectFriendId } from "@store/friendSlice";
import { DeleteOutline } from "@mui/icons-material";
import ArrowCircleUpIcon from "@mui/icons-material/ArrowCircleUp";

interface GuestBookConfig {
  visitorBookId: number;
  visitorNickname: string;
  visitorProfileImage: string;
  visitorBookContent: string;
  writeDateTime: string;
}

const TMP_COMMENT: GuestBookConfig[] = [
  {
    visitorBookId: 1,
    visitorNickname: "nickname",
    visitorProfileImage: "",
    visitorBookContent: "방명록 내용",
    writeDateTime: "2023.11.03 11:23",
  },
  {
    visitorBookId: 2,
    visitorNickname: "nickname2",
    visitorProfileImage: "",
    visitorBookContent: "방명록 내용 2",
    writeDateTime: "2023.11.03 11:23",
  },
  {
    visitorBookId: 3,
    visitorNickname: "nickname3",
    visitorProfileImage: "",
    visitorBookContent: "방명록 내용 3",
    writeDateTime: "2023.11.03 11:23",
  },
  {
    visitorBookId: 4,
    visitorNickname: "nickname4",
    visitorProfileImage: "",
    visitorBookContent: "방명록 내용 4",
    writeDateTime: "2023.11.03 11:23",
  },
  {
    visitorBookId: 5,
    visitorNickname: "nickname5",
    visitorProfileImage: "",
    visitorBookContent: "방명록 내용 5",
    writeDateTime: "2023.11.03 11:23",
  },
  {
    visitorBookId: 6,
    visitorNickname: "nickname6",
    visitorProfileImage: "",
    visitorBookContent: "방명록 내용 6",
    writeDateTime: "2023.11.03 11:23",
  },
];

const GuestBook = () => {
  const userInfo = useAppSelector(selectUserInfo);
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const none = friendId != userId ? "--none" : "";

  return (
    <div className={styles["guest"]}>
      <div className={styles[`guest__contents${none}`]}>
        {TMP_COMMENT && TMP_COMMENT.length > 1 ? (
          <>
            {TMP_COMMENT.map((item, idx) => (
              <div className={styles["guest__comment"]} key={idx}>
                <div>
                  <Avatar src={item.visitorProfileImage} sx={{ width: 20, height: 20 }} />
                  <Text types="small">{item.visitorNickname}</Text>
                </div>
                <Text types="small">{item.visitorBookContent}</Text>
                <Text types="small">{item.writeDateTime}</Text>
                {friendId === userId && <DeleteOutline />}
              </div>
            ))}
          </>
        ) : (
          <div className={styles["guest__none"]}>
            <Text>방명록이 없습니다.</Text>
          </div>
        )}
      </div>
      {friendId === userId && (
        <div className={styles["guest__input"]}>
          <div>
            <Avatar src={userInfo.profileImage} sx={{ width: 20, height: 20 }} />
            <Text types="small">{userInfo.nickname}</Text>
          </div>
          <input />
          <div>
            <ArrowCircleUpIcon />
          </div>
        </div>
      )}
    </div>
  );
};

export default GuestBook;
