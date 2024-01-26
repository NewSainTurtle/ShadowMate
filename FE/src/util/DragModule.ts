import { DragEvent, MutableRefObject, useState } from "react";
import { plannerApi } from "@api/Api";
import { Dispatch, SetStateAction, useRef } from "react";
import { TodoConfig } from "./planner.interface";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";

interface Props {
  date: string;
  todos: TodoConfig[];
  setTodos: Dispatch<SetStateAction<TodoConfig[]>>;
  /** 드래그 했을 때 적용할 스타일의 클래스이름 */
  dragClassName: string;
  /** 드래그 가능한 컴포넌트들 */
  draggablesRef: MutableRefObject<HTMLDivElement[]>;
}

const dragModule = ({ date, todos, setTodos, dragClassName, draggablesRef }: Props) => {
  const userId = useAppSelector(selectUserId);
  const dragTargetRef = useRef<number>(-1);
  const dragEndRef = useRef<number>(-1);
  const [mouseOnChild, setMouseOnChild] = useState(false);
  const childMouseEnter = () => setMouseOnChild(true);
  const childMouseLeave = () => setMouseOnChild(false);

  const containerDragOver = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    const afterElement = getDragAfterElement(e.clientY);
    const draggable = document.querySelector(`.${dragClassName}`);
    if (draggable) e.currentTarget.insertBefore(draggable, afterElement);
  };

  const dragStart = (e: DragEvent<HTMLDivElement>, idx: number) => {
    if (!mouseOnChild) {
      e.preventDefault();
      e.stopPropagation();
      return;
    }
    e.dataTransfer.setDragImage(new Image(), 0, 0);
    e.currentTarget.classList.add(dragClassName);
    dragTargetRef.current = idx;
  };

  const dragEnter = (e: DragEvent<HTMLDivElement>, idx: number) => {
    e.preventDefault();
    if (dragTargetRef.current == idx) return;
    dragEndRef.current = idx;
  };

  const dragEnd = async (e: DragEvent<HTMLDivElement>, todoId: number) => {
    e.preventDefault();
    e.currentTarget.classList.remove(dragClassName);
    dragTodo(todoId);
  };

  const dragTodo = async (todoId: number) => {
    const copyTodos = [...todos];
    const drageTargetIdx = dragTargetRef.current;
    const endTargetIdx = dragEndRef.current;
    if (drageTargetIdx == endTargetIdx || endTargetIdx == -1) return;
    const upperTodoId = (() => {
      if (endTargetIdx == 0) return null;
      if (drageTargetIdx > endTargetIdx) return copyTodos[endTargetIdx - 1].todoId;
      return copyTodos[endTargetIdx].todoId;
    })();

    await plannerApi
      .todoSequence(userId, {
        date,
        todoId,
        upperTodoId,
      })
      .then(() => {
        const dragItemContent = copyTodos[drageTargetIdx];
        copyTodos.splice(drageTargetIdx, 1);
        copyTodos.splice(endTargetIdx, 0, dragItemContent);
        dragTargetRef.current = -1;
        dragEndRef.current = -1;
        setTodos(copyTodos);
      });
  };

  /** 드래그 가까운 element 찾기 */
  const getDragAfterElement = (y: number) => {
    const draggableElements = Array.from(
      draggablesRef.current.filter((ele) => ele && !ele.classList.contains(`${dragClassName}`)),
    );

    const initialObject = { offset: Number.NEGATIVE_INFINITY, element: null as null | Element };
    const result = draggableElements.reduce((closest, element) => {
      const box = element.getBoundingClientRect(); //해당 element top값, height값 담겨져 있는 메소드
      const offset = y - box.top - box.height / 2;
      if (offset < 0 && offset > closest.offset) return { offset: offset, element: element };
      else return closest;
    }, initialObject).element;

    return result;
  };

  return {
    childMouseEnter,
    childMouseLeave,
    containerDragOver,
    dragStart,
    dragEnter,
    dragEnd,
  };
};

export default dragModule;
