import { todoType, categoryType } from "@util/planner.interface";

export const todoData_friend = {
  nickname: "토롱이",
  message: "인생은 생각하는대로 흘러간다.",
  src: "https://avatars.githubusercontent.com/u/85155789?v=4",
};

export const todoData_category: categoryType[] = [
  {
    categoryId: 1,
    categoryTitle: "국어",
    categoryColorCode: "#FFCBE1",
    categoryEmoticon: "🌈",
  },
  {
    categoryId: 2,
    categoryTitle: "수학",
    categoryColorCode: "#B6F7E1",
    categoryEmoticon: "🎠",
  },
  {
    categoryId: 3,
    categoryTitle: "과학 뿌셔뿌셔 대항전",
    categoryColorCode: "#B6DEF7",
    categoryEmoticon: "🧤",
  },
];

export const todoData_list: todoType[] = [
  {
    todoId: 1,
    categoryTitle: "수학",
    categoryColorCode: "#B6F7E1",
    todoContent: "수능완성 수학 과목별 10문제",
    todoStatus: 2,
  },
  {
    todoId: 2,
    categoryTitle: "국어",
    categoryColorCode: "#FFCBE1",
    todoContent: "초고난도 독서 02 (사회·경제)",
    todoStatus: 1,
  },
  {
    todoId: 3,
    categoryTitle: "국어",
    categoryColorCode: "#FFCBE1",
    todoContent: "매3비 DAY6",
    todoStatus: 1,
  },
  {
    todoId: 4,
    categoryTitle: "",
    categoryColorCode: "#E9E9EB",
    todoContent: "명탐정 코난12기 10~12화",
    todoStatus: 1,
  },
  {
    todoId: 5,
    categoryTitle: "과학 뿌셔뿌셔 대항전",
    categoryColorCode: "#B6DEF7",
    todoContent: "산화환원 반응 실험하기",
    todoStatus: 1,
  },
  {
    todoId: 6,
    categoryTitle: "과학 뿌셔뿌셔 대항전",
    categoryColorCode: "#B6DEF7",
    todoContent: "유전과 진화 체험학습 - 타임머신타고 30만년 전으로 가서 호모 사피엔스 싸인 받기",
    todoStatus: 1,
  },
  {
    todoId: 7,
    categoryTitle: "수학",
    categoryColorCode: "#B6F7E1",
    todoContent: "함수의 미분 인강",
    todoStatus: 2,
  },
];
