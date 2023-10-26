export interface MonthType {
  date: string;
  todoCount: number;
  dayStatus: number;
}

export interface CategoryConfig {
  categoryId: number;
  categoryTitle: string;
  categoryColorCode: string;
  categoryEmoticon: string;
}

export interface DayTodoConfig {
  todoId: number;
  todoContent: string;
  todoStatus: "공백" | "완료" | "미완료"; // none, O, X
}

export interface TimeTableConfig {
  timeTableId: number;
  startTime: string;
  endTime: string;
}

// 공통 Todo Item
export interface TodoConfig {
  todoId: number;
  category?: CategoryConfig;
  todoContent: string;
  todoStatus: string;
  todoUpdate?: boolean;
  timeTable?: TimeTableConfig;
}

/* --- Week Interfaces --- */

export interface WeekTodoItemConfig {
  weeklyTodoContent: string;
  weeklyTodoStatus: boolean;
  weeklyTodoUpdate: boolean;
}

export interface ddayType {
  ddayId: number;
  ddayDate: Date | string;
  ddayTitle: string;
}
