import React, { Dispatch, SetStateAction, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { useAppSelector } from "@hooks/hook";
import { selectIsGoogle } from "@store/authSlice";

interface MyPageTabProps {
  tabName: string;
  setTabName: Dispatch<SetStateAction<string>>;
}

interface TabListType {
  id: number;
  title: string;
  list: string[];
}

const TAB_LIST: TabListType[] = [
  {
    id: 0,
    title: "내 정보",
    list: ["내 정보 확인", "비밀번호 변경", "회원탈퇴"],
  },
  {
    id: 1,
    title: "다이어리",
    list: ["다이어리 설정", "카테고리 설정", "디데이 설정", "루틴 설정"],
  },
  { id: 2, title: "친구", list: ["팔로워 목록", "팔로잉 목록", "친구 검색"] },
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
          <div key={item.id} className={styles[`tab__title${clicked}`]}>
            <input type="checkbox" id={item.title} checked readOnly />
            <label htmlFor={item.title}>{item.title}</label>
            {item.list && (
              <div className={styles["tab__contents"]}>
                {item.list.map((list, idx) => {
                  const cur = item.id;
                  return (
                    <div
                      key={idx.toString()}
                      onClick={() => handleLink(list, cur)}
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
