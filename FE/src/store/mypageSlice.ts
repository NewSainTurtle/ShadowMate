import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { CategoryConfig } from "@util/planner.interface";

interface CategoryColorConfig {
  categoryColorId: number;
  categoryColorCode: string;
}

interface mypageConfig {
  category: {
    categoryList: CategoryConfig[];
    categoryColors: CategoryColorConfig[];
    categoryClick: number;
    categoryInput: CategoryConfig;
    categoryColorClick: number;
  };
}

const initialState: mypageConfig = {
  category: {
    categoryList: [],
    categoryColors: [],
    categoryClick: 0,
    categoryInput: {
      categoryId: 0,
      categoryTitle: "",
      categoryEmoticon: "",
      categoryColorCode: "",
    },
    categoryColorClick: 0,
  },
};

const mypageSlice = createSlice({
  name: "mypage",
  initialState,
  reducers: {
    setCategoryList: (state, { payload }: PayloadAction<CategoryConfig[]>) => {
      console.log("updated: " + payload);
      state.category.categoryList = payload;
    },
    setCategoryColors: (state, { payload }: PayloadAction<CategoryColorConfig[]>) => {
      state.category.categoryColors = payload;
    },
    setCategoryClick: (state, { payload }: PayloadAction<number>) => {
      state.category.categoryClick = payload;
    },
    setCategoryInput: (state, { payload }: PayloadAction<CategoryConfig>) => {
      state.category.categoryInput = payload;
    },
    setCategoryColorClick: (state, { payload }: PayloadAction<number>) => {
      state.category.categoryColorClick = payload;
    },
  },
});

export const { setCategoryList, setCategoryColors, setCategoryClick, setCategoryInput, setCategoryColorClick } =
  mypageSlice.actions;
export const selectCategoryList = (state: rootState) => state.mypage.category.categoryList;
export const selectCategoryColors = (state: rootState) => state.mypage.category.categoryColors;
export const selectCategoryClick = (state: rootState) => state.mypage.category.categoryClick;
export const selectCategoryInput = (state: rootState) => state.mypage.category.categoryInput;
export const selectCategoryColorClick = (state: rootState) => state.mypage.category.categoryColorClick;
export default mypageSlice.reducer;
