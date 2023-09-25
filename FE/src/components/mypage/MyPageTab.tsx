import React, { Dispatch, SetStateAction, useEffect, useRef, useState } from "react";
import styles from "./MyPage.module.scss";
import Text from "@components/common/Text";

interface MyPageTabProps {
  setTabName: Dispatch<SetStateAction<string>>;
}

const MENU_LIST = [
  { title: "내 정보" },
  { title: "다이어리", list: ["다이어리 설정", "카테고리 설정", "디데이 설정"] },
  { title: "친구" },
];

const MyPageTab = ({ setTabName }: MyPageTabProps) => {
  const listRef = useRef<HTMLDivElement>(null);
  const [open, setOpen] = useState<boolean>(false);
  const [activeIndex, setActiveIndex] = useState<number>(0); // 상위 카테고리
  const [clickIndex, setClickIndex] = useState<number>(0); // 하위 카테고리

  const handleClickHeader = (idx: number) => {
    setActiveIndex(idx);
    setClickIndex(0);
    idx === 1 ? setOpen(!open) : setOpen(false);
    setTabName(idx === 0 ? "info" : idx === 1 ? "diary" : "friend");
  };

  const handleLink = (event: any, idx: number) => {
    setClickIndex(idx);
    setTabName(idx === 0 ? "diary" : idx === 1 ? "category" : "dday");
  };

  useEffect(() => {
    if (!listRef.current) return;
    if (!open) {
      listRef.current.style.height = "0";
      listRef.current.style.paddingTop = "0";
    } else {
      listRef.current.style.height = "6.2em";
      listRef.current.style.paddingTop = "0.5em";
    }
  }, [open]);

  return (
    <div className={styles["tab"]}>
      {MENU_LIST.map((item, idx) => {
        const clicked = idx === activeIndex ? "--clicked" : "";
        return (
          <div key={idx} className={styles[`tab__title${clicked}`]}>
            <div onClick={() => handleClickHeader(idx)}>
              <Text>{item.title}</Text>
            </div>
            {item.list && (
              <div ref={listRef} className={styles[`tab__title--open`]}>
                {item.list?.map((list, idx) => (
                  <div onClick={(event) => handleLink(event, idx)} key={idx}>
                    <Text types="small" bold={idx === clickIndex}>
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
