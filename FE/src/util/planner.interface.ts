export interface MonthType {
  date: string;
  todoCount: number;
  dayStatus: number;
}

export interface GuestBookConfig {
  visitorId: number;
  visitorBookId: number;
  visitorNickname: string;
  visitorProfileImage: string;
  visitorBookContent: string;
  writeDateTime: string;
}

export interface CategoryColorConfig {
  categoryColorId: number;
  categoryColorCode: string;
}

export interface CategoryItemConfig {
  categoryId: number;
  categoryTitle: string;
  categoryColorCode: string;
  categoryEmoticon?: string | null;
}

export interface DdayItemConfig {
  ddayId: number;
  ddayDate: Date | string;
  ddayTitle: string;
}

export interface TimeTableConfig {
  timeTableId: number;
  startTime: string;
  endTime: string;
}

export interface TodoConfig {
  todoId: number;
  category?: CategoryItemConfig | null;
  todoContent: string;
  todoStatus: "공백" | "완료" | "진행중" | "미완료";
  todoUpdate?: boolean;
  timeTables?: TimeTableConfig[] | null;
}

/* Day Interfaces */
export interface DayInfoConfig {
  plannerAccessScope: "전체공개" | "친구공개" | "비공개";
  dday: string | null;
  ddayTitle: string;
  like: boolean;
  likeCount: number;
  shareSocial: number; // 소셜공유 여부 [없으면 0]
  dailyTodos: TodoConfig[]; //할일 리스트
}

export interface DayInfoResponse extends DayInfoConfig {
  date: string;
  todayGoal: string | null;
  retrospection: string | null;
  retrospectionImage: string | null;
  tomorrowGoal: string | null;
  studyTimeHour: number;
  studyTimeMinute: number;
}

/* --- Week Interfaces --- */

export interface WeekTodoItemConfig {
  weeklyTodoId: number;
  weeklyTodoContent: string;
  weeklyTodoStatus: boolean;
  weeklyTodoUpdate?: boolean;
}
