import React from "react";
import MyPageCategoryItem from "../item/MyPageCategoryItem";
import { useAppSelector } from "@hooks/hook";
import { selectCategoryList } from "@store/mypageSlice";
import { CategoryConfig } from "@util/planner.interface";

const CategoryList = () => {
  const categoryList: CategoryConfig[] = useAppSelector(selectCategoryList);
  return <>{categoryList?.map((item, idx) => <MyPageCategoryItem key={item.categoryId} idx={idx} item={item} />)}</>;
};

export default CategoryList;
