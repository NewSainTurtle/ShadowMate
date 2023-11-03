import React, { Dispatch, SetStateAction, useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";

interface MyPageTabProps {
  setTabName: Dispatch<SetStateAction<string>>;
}

interface TabListType {
  id: string;
  title: string;
  contents: {
    index: number;
    list: string[];
  };
}

const TAB_LIST: TabListType[] = [
  { id: "info", title: "내 정보", contents: { index: 0, list: ["내 정보 확인", "비밀번호 변경", "회원 탈퇴"] } },
  {
    id: "diary",
    title: "다이어리",
    contents: { index: 1, list: ["다이어리 설정", "카테고리 설정", "디데이 설정"] },
  },
  { id: "friend", title: "친구", contents: { index: 2, list: ["팔로워 목록", "팔로잉 목록", "친구 검색"] } },
];

const MyPageTab = ({ setTabName }: MyPageTabProps) => {
  const [headerIndex, setHeaderIndex] = useState<number>(0); // 상위 카테고리
  const [subIndex, setSubIndex] = useState<string>("내 정보 확인"); // 하위 카테고리

  const handleLink = (list: string, idx: number) => {
    setHeaderIndex(idx);
    setSubIndex(list);
    setTabName(list);
  };

  return (
    <div className={styles["tab"]}>
      {TAB_LIST.map((item, idx) => {
        const clicked = idx === headerIndex ? "--clicked" : "";
        return (
          <div key={idx} className={styles[`tab__title${clicked}`]}>
            <input type="checkbox" id={item.id} checked readOnly />
            <label htmlFor={item.id}>{item.title}</label>
            {item.contents.list && (
              <div className={styles["tab__contents"]}>
                {item.contents.list.map((list, idx) => {
                  const cur = item.contents.index;
                  return (
                    <div onClick={() => handleLink(list, cur)} key={idx}>
                      <Text types="small" bold={list === subIndex}>
                        {list}
                      </Text>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
};

export default MyPageTab;
