import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";
import { useAppSelector } from "@hooks/hook";
import { CategoryItemConfig } from "@util/planner.interface";
import { selectCategoryList } from "@store/mypage/categorySlice";

interface Props {
  type: "week" | "day";
  handleClick: (props: CategoryItemConfig) => void;
}

const CategorySelector = ({ type, handleClick }: Props) => {
  const categoryList: CategoryItemConfig[] = useAppSelector(selectCategoryList);
  return (
    <div className={styles["category__selector"]}>
      <div>
        <Text>카테고리 선택</Text>
      </div>
      <div>
        {categoryList.map((item, idx) => (
          <div key={idx} className={styles["category__item--hover"]} onClick={() => handleClick(item)}>
            {
              {
                week: <div>{item.categoryEmoticon}</div>,
                day: <div style={{ backgroundColor: item.categoryColorCode }}></div>,
              }[type]
            }
            <div className={styles["category__emoticon"]}>{item.categoryTitle}</div>
          </div>
        ))}
      </div>
      <div className={styles["category__item--hover"]} onClick={() => handleClick(BASIC_CATEGORY_ITEM)}>
        {
          {
            week: <div className={styles["category__emoticon"]}>{BASIC_CATEGORY_ITEM.categoryEmoticon}</div>,
            day: <div style={{ backgroundColor: BASIC_CATEGORY_ITEM.categoryColorCode }}></div>,
          }[type]
        }
        <div>카테고리 없음</div>
      </div>
    </div>
  );
};

export default CategorySelector;
