import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import MyPageCategoryItem from "@components/mypage/item/MyPageCategoryItem";
import { useAppSelector } from "@hooks/hook";
import { selectCategoryList } from "@store/mypageSlice";
import { CategoryConfig } from "@util/planner.interface";

const CategoryList = () => {
  const categoryList: CategoryConfig[] = useAppSelector(selectCategoryList);

  return (
    <>
      {categoryList.length != 0 ? (
        <> {categoryList?.map((item, idx) => <MyPageCategoryItem key={item.categoryId} idx={idx} item={item} />)} </>
      ) : (
        <div className={styles["category__none"]}>
          <Text>카테고리가 없습니다.</Text>
          <Text>새 카테고리를 생성해보세요.</Text>
        </div>
      )}
    </>
  );
};

export default CategoryList;
