import React from "react";
import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { CategoryColorConfig, CategoryItemConfig } from "@util/planner.interface";
import { PURGE } from "redux-persist";

interface CategoryConfig {
  categoryList: CategoryItemConfig[];
  categoryColors: CategoryColorConfig[];
  categoryClick: number;
  categoryInput: CategoryItemConfig;
  categoryColorClick: number;
}

const initialState: CategoryConfig = {
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
};

const categorySlice = createSlice({
  name: "category",
  initialState,
  reducers: {
    setCategoryList: (state, { payload }: PayloadAction<CategoryItemConfig[]>) => {
      state.categoryList = payload;
    },
    setCategoryColors: (state, { payload }: PayloadAction<CategoryColorConfig[]>) => {
      state.categoryColors = payload;
    },
    setCategoryClick: (state, { payload }: PayloadAction<number>) => {
      state.categoryClick = payload;
    },
    setCategoryInput: (state, { payload }: PayloadAction<CategoryItemConfig>) => {
      state.categoryInput = payload;
    },
    setCategoryColorClick: (state, { payload }: PayloadAction<number>) => {
      state.categoryColorClick = payload;
    },
  },
  extraReducers: (builder) => {
    // redux-persist 초기화
    builder.addCase(PURGE, () => initialState);
  },
});

export const { setCategoryList, setCategoryColors, setCategoryClick, setCategoryInput, setCategoryColorClick } =
  categorySlice.actions;

export const selectCategoryList = (state: rootState) => state.category.categoryList;
export const selectCategoryColors = (state: rootState) => state.category.categoryColors;
export const selectCategoryClick = (state: rootState) => state.category.categoryClick;
export const selectCategoryInput = (state: rootState) => state.category.categoryInput;
export const selectCategoryColorClick = (state: rootState) => state.category.categoryColorClick;

export default categorySlice.reducer;
