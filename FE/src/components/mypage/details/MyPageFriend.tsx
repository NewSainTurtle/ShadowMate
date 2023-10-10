import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";

const MyPageFriend = () => {
  const [currentTab, setCurrentTab] = useState(0);

  const menuArr = [
    { name: "팔로워", node: "팔로워 목록" },
    { name: "팔로잉", node: "팔로잉 목록" },
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
      <div>
        <p>{menuArr[currentTab].node}</p>
      </div>
    </div>
  );
};

export default MyPageFriend;
