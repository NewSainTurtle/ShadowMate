import React, { useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";

const MonthCalendar = () => {
  const [isNotFriend, setIsNotFriend] = useState<boolean>(false);
  return (
    <>
      <div className={styles.monthCalendar_container}>
        {isNotFriend && (
          <div className={styles.monthCalendar_overlay}>
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24">
              <path d="M18 10v-4c0-3.313-2.687-6-6-6s-6 2.687-6 6v4h-3v14h18v-14h-3zm-5 7.723v2.277h-2v-2.277c-.595-.347-1-.984-1-1.723 0-1.104.896-2 2-2s2 .896 2 2c0 .738-.404 1.376-1 1.723zm-5-7.723v-4c0-2.206 1.794-4 4-4 2.205 0 4 1.794 4 4v4h-8z" />
            </svg>
            <Text>비공개 계정은 확인할 수 없습니다.</Text>
          </div>
        )}
      </div>
    </>
  );
};

export default MonthCalendar;
