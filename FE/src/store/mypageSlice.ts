import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { CategoryColorConfig, CategoryConfig, DdayConfig } from "@util/planner.interface";

interface mypageConfig {
  category: {
    categoryList: CategoryConfig[];
    categoryColors: CategoryColorConfig[];
    categoryClick: number;
    categoryInput: CategoryConfig;
    categoryColorClick: number;
  };
  dday: {
    ddayList: DdayConfig[];
    ddayClick: number;
    ddayInput: DdayConfig;
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
  dday: {
    ddayList: [],
    ddayClick: 0,
    ddayInput: {
      ddayId: 0,
      ddayTitle: "",
      ddayDate: new Date(),
    },
  },
};

const mypageSlice = createSlice({
  name: "mypage",
  initialState,
  reducers: {
    setCategoryList: (state, { payload }: PayloadAction<CategoryConfig[]>) => {
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
    setDdayList: (state, { payload }: PayloadAction<DdayConfig[]>) => {
      state.dday.ddayList = payload;
    },
    setDdayClick: (state, { payload }: PayloadAction<number>) => {
      state.dday.ddayClick = payload;
    },
    setDdayInput: (state, { payload }: PayloadAction<DdayConfig>) => {
      state.dday.ddayInput = payload;
    },
  },
});

export const {
  setCategoryList,
  setCategoryColors,
  setCategoryClick,
  setCategoryInput,
  setCategoryColorClick,
  setDdayList,
  setDdayClick,
  setDdayInput,
} = mypageSlice.actions;

export const selectCategoryList = (state: rootState) => state.mypage.category.categoryList;
export const selectCategoryColors = (state: rootState) => state.mypage.category.categoryColors;
export const selectCategoryClick = (state: rootState) => state.mypage.category.categoryClick;
export const selectCategoryInput = (state: rootState) => state.mypage.category.categoryInput;
export const selectCategoryColorClick = (state: rootState) => state.mypage.category.categoryColorClick;

export const selectDdayList = (state: rootState) => state.mypage.dday.ddayList;
export const selectDdayClick = (state: rootState) => state.mypage.dday.ddayClick;
export const selectDdayInput = (state: rootState) => state.mypage.dday.ddayInput;

export default mypageSlice.reducer;
