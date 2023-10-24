import React, { Dispatch, Ref, RefObject, SetStateAction, useEffect, useRef } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { CategoryConfig } from "@util/planner.interface";

interface Props {
  item: CategoryConfig;
  index: number;
  click: number;
  setClick: Dispatch<SetStateAction<number>>;
}

const MyPageCategoryItem = ({ item, index, click, setClick }: Props) => {
  const clicked = click === index ? "--clicked" : "";
  const endRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }, [click]);

  return (
    <div
      ref={index === click ? endRef : null}
      className={styles[`category__item${clicked}`]}
      onClick={() => setClick(index)}
    >
      <div>{item.categoryEmoticon}</div>
      <div>{item.categoryTitle}</div>
      <div style={{ backgroundColor: item.categoryColorCode }}></div>
    </div>
  );
};

export default MyPageCategoryItem;
