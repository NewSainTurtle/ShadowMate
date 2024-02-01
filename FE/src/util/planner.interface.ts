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

/* --- Week Interfaces --- */

export interface WeekTodoItemConfig {
  weeklyTodoId: number;
  weeklyTodoContent: string;
  weeklyTodoStatus: boolean;
  weeklyTodoUpdate?: boolean;
}
