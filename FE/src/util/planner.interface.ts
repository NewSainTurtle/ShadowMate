export interface categoryType {
  categoryId: number;
  categoryColorCode: string;
  categoryTitle: string;
  categoryEmoticon: string;
}

export interface todoType {
  todoId: number;
  categoryColorCode: string;
  categoryTitle: string;
  todoContent: string;
  todoStatus: 0 | 1 | 2; // none, O, X
}

// 공통 Todo Item
export interface TodoConfig {
  todoId: number;
  category?: categoryType;
  todoContent: string;
  todoStatus: string;
  todoUpdate?: boolean;
  timeTable?: TimeTableConfig;
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

export interface ddayType {
  ddayId: number;
  ddayDate: Date | string;
  ddayTitle: string;
}
