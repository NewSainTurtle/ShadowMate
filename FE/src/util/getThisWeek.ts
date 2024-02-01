import dayjs from "dayjs";

export interface WeekConfig {
  weekCnt: number;
  weekYear: number;
  weekMonth: number;
}

export interface DayInfoConfig {
  year: number;
  month: number;
  day: number;
  dayOfWeek: string;
}

interface Props {
  date: string | number | Date | dayjs.Dayjs;
}

const dayOfWeekList = ["월", "화", "수", "목", "금", "토", "일"];

export const getThisWeek = (date: string | Date | dayjs.Dayjs) => {
  const inputDate = dayjs(date).toDate();
  let weekInfo: string[] = [];

  const theYear = inputDate.getFullYear();
  const theMonth = inputDate.getMonth();
  const theDate = inputDate.getDate();
  const dayOfWeek = inputDate.getDay();
  const mondayFirst = dayOfWeek === 0 ? 6 : dayOfWeek - 1;

  const startDate = dayjs(new Date(theYear, theMonth, theDate - mondayFirst)).format("YYYY-MM-DD");
  const endDate = dayjs(new Date(theYear, theMonth, theDate - mondayFirst + 6)).format("YYYY-MM-DD");
  weekInfo = [startDate, endDate];

  return weekInfo;
};

export const getThisWeekCnt = (date: Date) => {
  const getWeekMonth = () => {
    if (date.getMonth() + 1 === 12) return 0;
    return date.getMonth() + 1;
  };

  let lastWeek = false;
  let currentDate = date.getDate();
  let firstDate = new Date(date.getFullYear(), date.getMonth(), 1);
  const lastDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
  if (currentDate + 6 > lastDate.getDate()) lastWeek = true;
  const firstDay = firstDate.getDay() === 0 ? 6 : firstDate.getDay() - 1;
  const weekCnt = lastWeek ? 1 : Math.ceil((currentDate + firstDay) / 7);
  const weekMonth = lastWeek ? getWeekMonth() : date.getMonth();

  const week: WeekConfig = {
    weekCnt,
    weekYear: date.getFullYear(),
    weekMonth,
  };

  return week;
};

export const dateFormat = (dateInfo: Date | string) => {
  const date = new Date(dateInfo);
  let day = date.getDay();
  day = day === 0 ? 6 : day - 1;
  return dayjs(date).format("YYYY.MM.DD") + "(" + dayOfWeekList[day] + ")";
};
