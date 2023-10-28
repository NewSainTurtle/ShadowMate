import { TodoConfig } from "@util/planner.interface";

export const WEEK_TODO_ITEMS = [
  { weeklyTodoId: 0, weeklyTodoContent: "국어 졸라게 열심히 풀기", weeklyTodoStatus: true, weeklyTodoUpdate: false },
  { weeklyTodoId: 1, weeklyTodoContent: "수학 갈아버리기", weeklyTodoStatus: false, weeklyTodoUpdate: false },
  { weeklyTodoId: 2, weeklyTodoContent: "영어 처참하게 눌러버리기", weeklyTodoStatus: false, weeklyTodoUpdate: false },
];

export const TODO_ITEMS = [
  { todoContents: "비문학 지문 복습", todoStatus: true, todoUpdate: false },
  {
    todoContents:
      "가나다라마바사아자차카타파하가나다라마바사아자차가나다라마바사아자차카타파하가나다라마바사아자차가나다라마바사아자차카타파하가나다라마바사아자차가나다라마바사아자차카타파하가나다라마바사아자차",
    todoStatus: true,
    todoUpdate: false,
  },
];

const Daily_TODS: TodoConfig[] = [
  {
    todoId: 1,
    category: {
      categoryId: 1,
      categoryTitle: "국어",
      categoryColorCode: "",
      categoryEmoticon: "🍅",
    },
    todoContent: "비문학 3문제 풀기",
    todoStatus: "완료",
  },
  {
    todoId: 2,
    category: null,
    todoContent: "수학 3문제 풀기",
    todoStatus: "공백",
  },
];

export const TODO_ITEMS_RESPONSE = {
  message: "주간 플래너 조회가 완료되었습니다.",
  data: {
    plannerAccessScope: "전체공개",
    dday: "2023-11-11",
    weeklyTodo: [
      {
        weeklyTodoId: 1,
        weeklyTodoContent: "마트 장보기",
        weeklyTodoStatus: true,
      },
      {
        weeklyTodoId: 2,
        weeklyTodoContent: "서버 환경구축하기",
        weeklyTodoStatus: false,
      },
    ],
    dayList: [
      {
        date: "2023-10-09",
        retrospection: "이제는 더이상 물러나 곳이 없다.",
        dailyTodo: Daily_TODS,
      },
      {
        date: "2023-10-10",
        retrospection: null,
        dailyTodo: null,
      },
      {
        date: "2023-10-11",
        retrospection: null,
        dailyTodo: null,
      },
      {
        date: "2023-10-12",
        retrospection: null,
        dailyTodo: null,
      },
      {
        date: "2023-10-13",
        retrospection: null,
        dailyTodo: null,
      },
      {
        date: "2023-10-14",
        retrospection: null,
        dailyTodo: null,
      },
      {
        date: "2023-10-15",
        retrospection: null,
        dailyTodo: null,
      },
    ],
  },
};
