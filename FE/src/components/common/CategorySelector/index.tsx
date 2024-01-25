import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import AddIcon from "@mui/icons-material/Add";
import { useNavigate } from "react-router-dom";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";
import { useAppSelector } from "@hooks/hook";
import { CategoryItemConfig } from "@util/planner.interface";
import { selectCategoryList } from "@store/mypage/categorySlice";

interface Props {
  type: "week" | "day";
  handleClick: (props: CategoryItemConfig) => void;
  addBtn: boolean;
}

const CategorySelector = ({ type, handleClick, addBtn }: Props) => {
  const navigator = useNavigate();
  const categoryList: CategoryItemConfig[] = useAppSelector(selectCategoryList);
  return (
    <div className={styles["category__selector"]}>
      <div>
        <Text>카테고리 선택</Text>
      </div>
      <div>
        {categoryList.map((item, idx) => (
          <div key={item.categoryId} className={styles["category__item--hover"]} onClick={() => handleClick(item)}>
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
      {addBtn && (
        <div className={styles["category__selector--add"]} onClick={() => navigator("/category")}>
          <AddIcon />
          <Text>새 카테고리 추가</Text>
        </div>
      )}
    </div>
  );
};

export default CategorySelector;
