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

export interface todoListType extends todoType {
  isPossible?: boolean;
}
