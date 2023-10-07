import { todoType, categoryType } from "@util/planner.interface";

export const todoData_friend = {
  nickname: "토롱이",
  message: "인생은 생각하는대로 흘러간다.",
  src: "https://avatars.githubusercontent.com/u/85155789?v=4",
};

export const todoData_category: categoryType[] = [
  {
    categoryId: 0,
    categoryTitle: "",
    categoryColorCode: "E9E9EB",
    categoryEmoticon: "",
  },
  {
    categoryId: 1,
    categoryTitle: "국어",
    categoryColorCode: "FFCBE1",
    categoryEmoticon: "🌈",
  },
  {
    categoryId: 2,
    categoryTitle: "수학",
    categoryColorCode: "B6F7E1",
    categoryEmoticon: "🎠",
  },
  {
    categoryId: 3,
    categoryTitle: "과학 뿌셔뿌셔 대항전",
    categoryColorCode: "B6DEF7",
    categoryEmoticon: "🧤",
  },
];

export const todoData_list: todoType[] = [
  {
    todoId: 0,
    categoryTitle: "수학",
    categoryColorCode: "B6F7E1",
    todoContent: "수능완성 수학 과목별 10문제",
    todoStatus: 2,
  },
  {
    todoId: 1,
    categoryTitle: "국어",
    categoryColorCode: "FFCBE1",
    todoContent: "초고난도 독서 02 (사회·경제)",
    todoStatus: 1,
  },
];
