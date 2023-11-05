import React, { useEffect, useRef } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { CategoryItemConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectCategoryClick, setCategoryClick } from "@store/mypage/categorySlice";

interface Props {
  idx: number;
  item: CategoryItemConfig;
}

const MyPageCategoryItem = ({ item, idx }: Props) => {
  const dispatch = useAppDispatch();
  const click: number = useAppSelector(selectCategoryClick);
  const clicked = click === idx ? "--clicked" : "";
  const endRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }, [click]);

  return (
    <div
      ref={idx === click ? endRef : null}
      className={styles[`category__item${clicked}`]}
      onClick={() => dispatch(setCategoryClick(idx))}
    >
      <div>{item.categoryEmoticon}</div>
      <div>{item.categoryTitle}</div>
      <div style={{ backgroundColor: item.categoryColorCode }}></div>
    </div>
  );
};

export default MyPageCategoryItem;
