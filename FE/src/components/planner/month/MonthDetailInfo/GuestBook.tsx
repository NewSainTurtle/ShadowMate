import React from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";
import { Avatar } from "@mui/material";
import { useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo } from "@store/authSlice";
import { selectFriendId } from "@store/friendSlice";
import { DeleteOutline } from "@mui/icons-material";
import ArrowCircleUpIcon from "@mui/icons-material/ArrowCircleUp";
import { TMP_COMMENT } from "@util/data/GuestBookData";

export interface GuestBookConfig {
  visitorBookId: number;
  visitorNickname: string;
  visitorProfileImage: string;
  visitorBookContent: string;
  writeDateTime: string;
}

const GuestBook = () => {
  const userInfo = useAppSelector(selectUserInfo);
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const none = friendId != userId ? "" : "--none";

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
      {friendId != userId && (
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
