import { categoryType } from "@util/planner.interface";

export const CATEGORY_COLORS: string[] = [
  "#D9B5D9",
  "#FFCBE1",
  "#FF8FAD",
  "#F1607D",
  "#F1FCAD",
  "#DAFFCB",
  "#B6F7E1",
  "#637F69",
  "#FFE7C8",
  "#FFAF6A",
  "#CCDA61",
  "#8668C2",
  "#B6DEF7",
  "#A6C9E9",
  "#CCCCCC",
  "#AEB8C1",
];

export const CATEGORY_LIST: categoryType[] = [
  { categoryId: 0, categoryTitle: "국어", categoryEmoticon: "📕", categoryColorCode: "#F1607D" },
  { categoryId: 1, categoryTitle: "수학", categoryEmoticon: "📗", categoryColorCode: "#637F69" },
  { categoryId: 2, categoryTitle: "영어", categoryEmoticon: "📒", categoryColorCode: "#F1FCAD" },
  { categoryId: 3, categoryTitle: "생물", categoryEmoticon: "", categoryColorCode: "#B6DEF7" },
];
