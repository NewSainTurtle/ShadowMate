export const debouncing = <T extends (...args: any[]) => any>(fn: T, delay: number) => {
  let timeId: ReturnType<typeof setTimeout>;
  return (...args: Parameters<T>): ReturnType<T> => {
    let result: any;
    if (timeId) clearTimeout(timeId);
    timeId = setTimeout(() => {
      result = fn(...args);
    }, delay);
    return result;
  };
};

export const throttle = <T extends (...args: any[]) => any>(fn: T, delay: number) => {
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
