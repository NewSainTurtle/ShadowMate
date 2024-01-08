import React, { Dispatch, SetStateAction, useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { useAppSelector } from "@hooks/hook";
import { selectIsGoogle } from "@store/authSlice";

interface MyPageTabProps {
  tabName: string;
  setTabName: Dispatch<SetStateAction<string>>;
}

interface TabListType {
  title: string;
  contents: {
    index: number;
    list: string[];
  };
}

const TAB_LIST: TabListType[] = [
  {
    title: "내 정보",
    contents: {
      index: 0,
      list: ["내 정보 확인", "비밀번호 변경", "회원탈퇴"],
    },
  },
  {
    title: "다이어리",
    contents: { index: 1, list: ["다이어리 설정", "카테고리 설정", "디데이 설정"] },
  },
  { title: "친구", contents: { index: 2, list: ["팔로워 목록", "팔로잉 목록", "친구 검색"] } },
];

const setHeaderName = (name: string) => {
  if (name === "친구 검색") return 2;
  return name === "카테고리 설정" ? 1 : 0;
};

const MyPageTab = ({ tabName, setTabName }: MyPageTabProps) => {
  const [headerIndex, setHeaderIndex] = useState<number>(setHeaderName(tabName)); // 상위 카테고리
  const [subIndex, setSubIndex] = useState<string>(tabName); // 하위 카테고리
  const isGoogle = useAppSelector(selectIsGoogle);

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
            <input type="checkbox" id={item.title} checked readOnly />
            <label htmlFor={item.title}>{item.title}</label>
            {item.contents.list && (
              <div className={styles["tab__contents"]}>
                {item.contents.list.map((list, idx) => {
                  const cur = item.contents.index;
                  return (
                    <div
                      onClick={() => handleLink(list, cur)}
                      key={idx}
                      style={{ display: isGoogle && list === "비밀번호 변경" ? "none" : "visible" }}
                    >
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
