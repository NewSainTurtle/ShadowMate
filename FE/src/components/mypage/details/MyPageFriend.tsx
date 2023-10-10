import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";

const MyPageFriend = () => {
  const [currentTab, setCurrentTab] = useState(0);

  const menuArr = [
    { name: "팔로워(15)", node: "팔로워 목록" },
    { name: "팔로잉(8)", node: "팔로잉 목록" },
  ];

  return (
    <div className={styles["friend__cantainer"]}>
      <div className={styles["friend__tab"]}>
        {menuArr.map((item, idx) => (
          <li
            className={`${styles["friend__subtab"]} ${idx === currentTab ? styles["friend__subtab--focused"] : ""}`}
            key={idx}
            onClick={() => setCurrentTab(idx)}
          >
            {item.name}
          </li>
        ))}
      </div>
      <div className={styles["friend__content"]}>
        <div className={styles["friend__list--request"]}>팔로우 요청 목록</div>
        <div className={styles["friend__list--follow"]}></div>
      </div>
    </div>
  );
};

export default MyPageFriend;
