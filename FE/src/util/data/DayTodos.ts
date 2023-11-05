import { ProfileConfig } from "@components/common/FriendProfile";
import { CategoryItemConfig } from "@util/planner.interface";
import { TodoConfig } from "@util/planner.interface";
import dayjs from "dayjs";

export const todoData_friend: ProfileConfig = {
  userId: 0,
  nickname: "토롱이",
  statusMessage: "인생은 생각하는대로 흘러간다.",
  profileImage: "https://avatars.githubusercontent.com/u/85155789?v=4",
};

export const todoData_category: CategoryItemConfig[] = [
  {
    categoryId: 44,
    categoryTitle: "국어",
    categoryColorCode: "#FFCBE1",
    categoryEmoticon: "📓",
  },
  {
    categoryId: 45,
    categoryTitle: "수학",
    categoryColorCode: "#B6F7E1",
    categoryEmoticon: "📐",
  },
  {
    categoryId: 46,
    categoryTitle: "과학 뿌셔뿌셔 대항전",
    categoryColorCode: "#B6DEF7",
    categoryEmoticon: "🧪",
  },
];

export const todoData_list: TodoConfig[] = [
  {
    todoId: 1,
    todoContent: "수능완성 수학 과목별 10문제",
    todoStatus: "미완료",
    category: {
      categoryId: 2,
      categoryTitle: "수학",
      categoryColorCode: "#B6F7E1",
      categoryEmoticon: "📐",
    },
    timeTable: {
      timeTableId: 0,
      startTime: "",
      endTime: "",
    },
  },
  {
    todoId: 2,
    todoContent: "초고난도 독서 02 (사회·경제)",
    todoStatus: "완료",
    category: {
      categoryId: 1,
      categoryTitle: "국어",
      categoryColorCode: "#FFCBE1",
      categoryEmoticon: "📓",
    },
    timeTable: {
      timeTableId: 0,
      startTime: dayjs().set("h", 11).set("m", 0).format("YYYY-MM-DD HH:mm"),
      endTime: dayjs().set("h", 12).set("m", 0).format("YYYY-MM-DD HH:mm"),
    },
  },
  {
    todoId: 3,
    todoContent: "매3비 DAY6",
    todoStatus: "완료",
    category: {
      categoryId: 1,
      categoryTitle: "국어",
      categoryColorCode: "#FFCBE1",
      categoryEmoticon: "📓",
    },
    timeTable: {
      timeTableId: 0,
      startTime: "",
      endTime: "",
    },
  },
  {
    todoId: 4,
    todoContent: "명탐정 코난12기 10~12화",
    todoStatus: "완료",
    category: {
      categoryId: 0,
      categoryTitle: "",
      categoryColorCode: "#E9E9EB",
      categoryEmoticon: "",
    },
    timeTable: {
      timeTableId: 0,
      startTime: "",
      endTime: "",
    },
  },
  {
    todoId: 5,
    todoContent: "산화환원 반응 실험하기",
    todoStatus: "완료",
    category: {
      categoryId: 3,
      categoryTitle: "과학 뿌셔뿌셔 대항전",
      categoryColorCode: "#B6DEF7",
      categoryEmoticon: "🧪",
    },
    timeTable: {
      timeTableId: 0,
      startTime: "",
      endTime: "",
    },
  },
  {
    todoId: 6,
    todoContent: "유전과 진화 체험학습 - 타임머신타고 30만년 전으로 가서 호모 사피엔스 싸인 받기",
    todoStatus: "완료",
    category: {
      categoryId: 3,
      categoryTitle: "과학 뿌셔뿌셔 대항전",
      categoryColorCode: "#B6DEF7",
      categoryEmoticon: "🧪",
    },
    timeTable: {
      timeTableId: 0,
      startTime: "",
      endTime: "",
    },
  },
  {
    todoId: 7,
    todoContent: "함수의 미분 인강",
    todoStatus: "미완료",
    category: {
      categoryId: 2,
      categoryTitle: "수학",
      categoryColorCode: "#B6F7E1",
      categoryEmoticon: "📐",
    },
    timeTable: {
      timeTableId: 0,
      startTime: "",
      endTime: "",
    },
  },
];
