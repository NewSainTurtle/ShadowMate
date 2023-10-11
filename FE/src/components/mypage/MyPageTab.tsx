import React, { Dispatch, SetStateAction, useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";

interface MyPageTabProps {
  setTabName: Dispatch<SetStateAction<string>>;
}

const MENU_LIST = [
  { id: "info", title: "내 정보", list: ["내 정보 확인"] },
  { id: "diary", title: "다이어리", list: ["다이어리 설정", "카테고리 설정", "디데이 설정"] },
  { id: "friend", title: "친구", list: ["팔로워 목록", "팔로잉 목록", "친구 검색"] },
];

const MyPageTab = ({ setTabName }: MyPageTabProps) => {
  const [subIndex, setSubIndex] = useState<string>("내 정보 확인"); // 하위 카테고리

  const handleLink = (list: string) => {
    setSubIndex(list);
    setTabName(list);
  };

  return (
    <div className={styles["tab"]}>
      {MENU_LIST.map((item, idx) => {
        return (
          <div key={idx} className={styles[`tab__title`]}>
            <input type="checkbox" id={item.id} checked />
            <label htmlFor={item.id}>{item.title}</label>
            {item.list && (
              <div className={styles["tab__contents"]}>
                {item.list?.map((list, idx) => (
                  <div onClick={() => handleLink(list)} key={idx}>
                    <Text types="small" bold={list === subIndex}>
                      {list}
                    </Text>
                  </div>
                ))}
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
};

export default MyPageTab;
