import React, { useState } from "react";
import styles from "./MyPage.module.scss";
import MyPageList from "./MyPageList";
import MyPageCategoryItem from "./item/MyPageCategoryItem";
import MyPageDetail from "./MyPageDetail";
import MyPageCategory from "./details/MyPageCategory";
import MyPageDday from "./details/MyPageDday";
import MyPageDdayItem from "./item/MyPageDdayItem";

interface Props {
  title: string;
}

export interface CategoryConfig {
  title: string;
  emoticon?: string;
  colorCode?: string;
}

export interface DdayConfig {
  title: string;
  date?: string;
}

export interface EditInfoConfig {
  type: string;
  info: CategoryConfig | DdayConfig | null;
  clicked: number;
}

const MyPageFrame = ({ title }: Props) => {
  const [categoryList, setCategoryList] = useState<CategoryConfig[]>([
    { title: "국어", emoticon: "📕", colorCode: "#F1607D" },
    { title: "수학", emoticon: "📗", colorCode: "#637F69" },
    { title: "영어", emoticon: "📒", colorCode: "#F1FCAD" },
    { title: "생물", emoticon: "", colorCode: "#B6DEF7" },
  ]);
  const [ddayList, setDdayList] = useState<DdayConfig[]>([
    { title: "목표일이 남았을 때", date: "2023.07.22(토)" },
    { title: "목표일이 당일일 때", date: "2023.07.20(목)" },
    { title: "목표일이 지났을 때", date: "2023.07.17(월)" },
  ]);
  const [categoryClick, setCategoryClick] = useState<number>(0);
  const [ddayClick, setDdayClick] = useState<number>(0);

  return (
    <div className={styles["frame"]}>
      <MyPageList title={title}>
        {
          {
            카테고리: (
              <>
                {categoryList.map((item, key) => (
                  <MyPageCategoryItem
                    key={key}
                    item={item}
                    index={key}
                    click={categoryClick}
                    setClick={setCategoryClick}
                  />
                ))}
              </>
            ),
            디데이: (
              <>
                {ddayList.map((item, key) => (
                  <MyPageDdayItem key={key} item={item} index={key} click={ddayClick} setClick={setDdayClick} />
                ))}
              </>
            ),
          }[title]
        }
      </MyPageList>
      <MyPageDetail>
        {
          {
            카테고리: <MyPageCategory click={categoryClick} categoryList={categoryList} />,
            디데이: <MyPageDday click={ddayClick} ddayList={ddayList} />,
          }[title]
        }
      </MyPageDetail>
    </div>
  );
};

export default MyPageFrame;
