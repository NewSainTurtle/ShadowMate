import { todoType, categoryType } from "@util/planner.interface";

interface todoListType extends todoType {
  isPossible?: boolean;
}

export const todoData_category: categoryType[] = [
  {
    categoryId: 0,
    categoryName: "",
    categoryColorCode: "E9E9EB",
    categoryEmoticon: "",
  },
  {
    categoryId: 1,
    categoryName: "국어",
    categoryColorCode: "FFCBE1",
    categoryEmoticon: "🌈",
  },
  {
    categoryId: 2,
    categoryName: "수학",
    categoryColorCode: "B6F7E1",
    categoryEmoticon: "🎠",
  },
  {
    categoryId: 3,
    categoryName: "과학 뿌셔뿌셔 대항전",
    categoryColorCode: "B6DEF7",
    categoryEmoticon: "🧤",
  },
];

export const todoData_list: todoListType[] = [
  {
    todoId: 0,
    categoryName: "수학",
    categoryColorCode: "B6F7E1",
    todoContent: "수능완성 수학 과목별 10문제",
    todoStatus: 2,
    isPossible: true,
  },
  {
    todoId: 2,
    categoryName: "국어",
    categoryColorCode: "FFCBE1",
    todoContent: "초고난도 독서 02 (사회·경제)",
    todoStatus: 1,
    isPossible: true,
  },
];
