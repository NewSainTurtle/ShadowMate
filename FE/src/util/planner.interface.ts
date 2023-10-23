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

/* --- Week Interfaces --- */

export interface WeekTodoItemConfig {
  weeklyTodoContent: string;
  weeklyTodoStatus: boolean;
  weeklyTodoUpdate: boolean;
}

export interface TodoItemConfig {
  todoContents: string;
  todoStatus: boolean;
  categoryEmoticon?: string;
  todoUpdate: boolean;
}

export interface ddayType {
  ddayId: number;
  ddayDate: Date | string;
  ddayTitle: string;
}
