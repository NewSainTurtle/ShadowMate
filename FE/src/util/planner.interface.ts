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
