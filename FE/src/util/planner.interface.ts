export interface categoryType {
  categoryId: number;
  categoryColorCode: string;
  categoryName: string;
  categoryEmoticon: string;
}

export interface todoType {
  todoId: number;
  categoryColorCode: string;
  categoryName: string;
  todoContent: string;
  todoStatus: 0 | 1 | 2; // none, O, X
}
