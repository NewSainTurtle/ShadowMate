import React, { ChangeEvent, Dispatch, SetStateAction, useEffect } from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";
import { useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo } from "@store/authSlice";
import { selectFriendId } from "@store/friendSlice";
import { userApi } from "@api/Api";

interface Props {
  isEdit: boolean;
  introduction: string;
  setIntroduction: Dispatch<SetStateAction<string>>;
}

const Introduction = ({ isEdit, introduction, setIntroduction }: Props) => {
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const maxLength = 100;

  const handleIntroduction = (e: ChangeEvent<HTMLTextAreaElement>) => {
    let input = e.target.value;
    if (input.length > maxLength) input = input.slice(0, maxLength);
    setIntroduction(input);
  };

  const getIntroduction = async () => {
    const response = await userApi.getIntroduction(friendId);
    if (response.status === 200) {
      setIntroduction(response.data.data.introduction || "");
    }
  };

  useEffect(() => {
    getIntroduction();
  }, [friendId]);

  return (
    <div className={styles["introduction"]}>
      {isEdit ? (
        <>
          <div className={styles["introduction__edit"]}>
            <textarea maxLength={maxLength} value={introduction} onChange={handleIntroduction} />
            <Text types="small">({introduction.length}/100자)</Text>
          </div>
        </>
      ) : (
        <>
          {introduction ? (
            <Text>{introduction}</Text>
          ) : (
            <div className={styles["introduction__none"]}>
              <Text>소개글이 없습니다.</Text>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default Introduction;
