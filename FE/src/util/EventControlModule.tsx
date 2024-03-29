import { useEffect, useState } from "react";

export function useDebounce(value: string, delay = 1000) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => clearTimeout(handler);
  }, [value]);

  return debouncedValue;
}

export const debouncing = <T extends (...args: never[]) => void>(fn: T, delay: number) => {
  let timeId: ReturnType<typeof setTimeout>;
  return (...args: Parameters<T>): ReturnType<T> | unknown => {
    let result: unknown;
    if (timeId) clearTimeout(timeId);
    timeId = setTimeout(() => {
      result = fn(...args);
    }, delay);
    return result;
  };
};

export const throttle = <T extends (...args: never[]) => void>(fn: T, delay: number) => {
  let timeId: ReturnType<typeof setTimeout> | null;
  return (...args: Parameters<T>) => {
    let result: unknown;
    if (!timeId) {
      timeId = setTimeout(() => {
        result = fn(...args);
        timeId = null;
      }, delay);
      return result;
    }
  };
};
