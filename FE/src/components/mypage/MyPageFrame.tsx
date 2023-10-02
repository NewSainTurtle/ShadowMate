import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import MyPageList from "./MyPageList";
import MyPageCategoryItem from "./item/MyPageCategoryItem";
import MyPageDetail from "./MyPageDetail";
import MyPageCategory from "./details/MyPageCategory";
import MyPageDday from "./details/MyPageDday";
import MyPageDdayItem from "./item/MyPageDdayItem";
import { categoryType } from "@util/planner.interface";

interface Props {
  title: string;
}

export interface DdayConfig {
  title: string;
  date?: string;
}

export interface EditInfoConfig {
  type: string;
  info: categoryType | DdayConfig | null;
  clicked: number;
}

const MyPageFrame = ({ title }: Props) => {
  const [categoryList, setCategoryList] = useState<categoryType[]>([
    { categoryId: 0, categoryTitle: "국어", categoryEmoticon: "📕", categoryColorCode: 3 },
    { categoryId: 1, categoryTitle: "수학", categoryEmoticon: "📗", categoryColorCode: 7 },
    { categoryId: 2, categoryTitle: "영어", categoryEmoticon: "📒", categoryColorCode: 4 },
    { categoryId: 3, categoryTitle: "생물", categoryEmoticon: "", categoryColorCode: 12 },
  ]);
  const [ddayList, setDdayList] = useState<DdayConfig[]>([
    { title: "목표일이 남았을 때", date: "2023.07.22(토)" },
    { title: "목표일이 당일일 때", date: "2023.07.20(목)" },
    { title: "목표일이 지났을 때", date: "2023.07.17(월)" },
  ]);
  const [categoryClick, setCategoryClick] = useState<number>(0);
  const [ddayClick, setDdayClick] = useState<number>(0);

  const [categoryInput, setCategoryInput] = useState<categoryType>({
    categoryId: 0,
    categoryTitle: categoryList[0].categoryTitle,
    categoryEmoticon: categoryList[0].categoryEmoticon,
    categoryColorCode: categoryList[0].categoryColorCode,
  });
  const [colorClick, setColorClick] = useState<number>(categoryInput.categoryColorCode);

  const saveEditInfo = () => {
    setCategoryList(
      categoryList.map((item, idx) => {
        if (categoryInput.categoryId == idx) {
          return {
            ...item,
            categoryId: categoryInput.categoryId,
            categoryTitle: categoryInput.categoryTitle,
            categoryEmoticon: categoryInput.categoryEmoticon,
            categoryColorCode: colorClick,
          };
        }
        return item;
      }),
    );
  };

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
      <MyPageDetail saveEditInfo={saveEditInfo}>
        {
          {
            카테고리: (
              <MyPageCategory
                click={categoryClick}
                categoryList={categoryList}
                input={categoryInput}
                setInput={setCategoryInput}
                colorClick={colorClick}
                setColorClick={setColorClick}
              />
            ),
            디데이: <MyPageDday click={ddayClick} ddayList={ddayList} />,
          }[title]
        }
      </MyPageDetail>
    </div>
  );
};

export default MyPageFrame;
