import React, { useRef } from "react";
import styles from "@styles/planner/day.module.scss";
import { useAppSelector } from "@hooks/hook";
import { BASIC_CATEGORY_ITEM, selectTodoItem } from "@store/planner/daySlice";

const throttle = <T extends (...args: any[]) => any>(fn: T, delay: number) => {
  let timeId: ReturnType<typeof setTimeout> | null;
  return (...args: Parameters<T>) => {
    let result: any;
    if (!timeId) {
      timeId = setTimeout(() => {
        result = fn(...args);
        timeId = null;
      }, delay);
      return result;
    }
  };
};

const CustomCursor = () => {
  const cursorRef = useRef<SVGSVGElement>(null);
  const todoItem = useAppSelector(selectTodoItem);
  const category = (() => todoItem.category || BASIC_CATEGORY_ITEM)();
  const categoryColorCode = category.categoryColorCode;

  const moveCursor = (e: { clientY: any; clientX: any }) => {
    const mouseY = e.clientY;
    const mouseX = e.clientX;
    if (cursorRef.current) {
      cursorRef.current.style.transform = `translate(${mouseX}px, ${mouseY}px)`;
    }
  };
  const throttleMouseMove = throttle(moveCursor, 30);

  window.addEventListener("mousemove", throttleMouseMove);

  return (
    <svg ref={cursorRef} className={styles["custom-cursor"]} viewBox="0 0 35 35" fill="none" preserveAspectRatio="none">
      <path
        d="M21.5187 10.154C21.8355 9.82246 22.3534 9.81805 22.6755 10.1442L23.6996 11.1811C24.0217 11.5072 24.026 12.3056 23.7092 12.6372L15.1668 21.5774C14.85 21.9089 14.332 21.648 14.01 21.3219L12.9858 20.285C12.6637 19.9589 12.6594 19.4257 12.9762 19.0942L21.5187 10.154Z"
        fill="#C9C8C8"
      />
      <path
        fillRule="evenodd"
        clipRule="evenodd"
        d="M14.9155 29.4345C20.2565 22.9872 28.8168 12.5781 31.8717 8.86175C32.5159 8.07795 32.4694 6.93782 31.7657 6.20695L27.1623 1.42629C26.4016 0.636265 25.1466 0.606247 24.3489 1.359L5.03039 19.5908C9.17445 23.711 10.7714 25.3143 14.9155 29.4345ZM7.11139 31.4309L3.09397 27.9257L0.979841 30.5997C0.52459 31.1755 0.824163 32.0292 1.53939 32.1943L4.57589 32.8951C4.86399 32.9616 5.16674 32.8972 5.40283 32.7192L7.11139 31.4309ZM21.5187 10.154C21.8355 9.82246 22.3534 9.81805 22.6755 10.1442L23.6996 11.1811C24.0217 11.5072 24.026 12.3056 23.7092 12.6372L15.1668 21.5774C14.85 21.9089 14.332 21.648 14.01 21.3219L12.9858 20.285C12.6637 19.9589 12.6594 19.4257 12.9762 19.0942L21.5187 10.154Z"
        fill={categoryColorCode}
      />
      <path
        d="M14.9155 29.4345C10.7714 25.3143 9.17445 23.711 5.03039 19.5908L3.09397 27.9257L7.11139 31.4309L14.9155 29.4345Z"
        fill="#C9C8C8"
      />
      <path
        d="M21.8802 10.4994C22.0024 10.3715 22.1958 10.3701 22.3197 10.4955L23.3439 11.5324C23.3769 11.5659 23.4427 11.696 23.444 11.9102C23.4453 12.1242 23.3813 12.2566 23.3477 12.2918L14.8062 21.231C14.7965 21.2312 14.7745 21.2298 14.7377 21.2185C14.63 21.1853 14.4882 21.0946 14.3657 20.9706L13.3415 19.9336C13.2097 19.8001 13.2077 19.5757 13.3377 19.4396L21.8802 10.4994ZM23.0312 9.79281C22.5109 9.26602 21.6685 9.27346 21.1572 9.80859L12.6147 18.7488C12.1112 19.2757 12.1177 20.1176 12.6301 20.6363L13.6542 21.6733C13.8538 21.8753 14.132 22.0782 14.443 22.1741C14.7677 22.2742 15.1975 22.269 15.5283 21.9228L24.0707 12.9826C24.3539 12.6862 24.4461 12.2537 24.444 11.904C24.4418 11.5546 24.3444 11.1224 24.0554 10.8297L23.0312 9.79281ZM5.74894 19.6002L24.6921 1.72263C25.2903 1.15807 26.2316 1.18058 26.8022 1.7731L31.4055 6.55376C31.9339 7.10247 31.9681 7.95707 31.4854 8.54425C28.4962 12.1807 20.2367 22.224 14.8797 28.6938C13.0134 26.8378 11.6698 25.4982 10.326 24.1585L10.3248 24.1574C8.97597 22.8127 7.62681 21.4677 5.74894 19.6002ZM3.64895 27.7464L5.31429 20.5783C7.05304 22.3077 8.33642 23.5871 9.61993 24.8667L9.62107 24.8679C10.9073 26.1501 12.1938 27.4327 13.9387 29.1683L7.24208 30.8814L3.64895 27.7464ZM3.1607 28.6475L6.31843 31.4026L5.1018 32.32C4.98376 32.409 4.83238 32.4412 4.68834 32.4079L1.65183 31.7071C1.29422 31.6246 1.14443 31.1977 1.37206 30.9098L3.1607 28.6475Z"
        stroke="black"
      />
    </svg>
  );
};

export default CustomCursor;
