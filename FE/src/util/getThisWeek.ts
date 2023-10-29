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

export const getThisWeekCnt = ({ date }: Props) => {
  const inputDate = dayjs(date).toDate();
  const currentDate = inputDate.getDate();
  const firstDay = new Date(inputDate.setDate(1)).getDay();
  const weekCnt = Math.ceil((currentDate + firstDay) / 7);

  const week: WeekConfig = {
    weekCnt,
    weekYear: inputDate.getFullYear(),
    weekMonth: inputDate.getMonth(),
  };

  return week;
};

export const dateFormat = (dateInfo: Date | string) => {
  const date = new Date(dateInfo);
  let day = date.getDay();
  day = day === 0 ? 6 : day - 1;
  return dayjs(date).format("YYYY.MM.DD") + "(" + dayOfWeekList[day] + ")";
};
