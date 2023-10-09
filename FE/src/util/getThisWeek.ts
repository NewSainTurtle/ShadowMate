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

export const getThisWeek = ({ date }: Props) => {
  const inputDate = dayjs(date).toDate();
  let weekInfo: DayInfoConfig[] = [];

  const theYear = inputDate.getFullYear();
  const theMonth = inputDate.getMonth();
  const theDate = inputDate.getDate();
  const dayOfWeek = inputDate.getDay();

  for (var i = 0; i < 7; i++) {
    const resultDay = new Date(theYear, theMonth, theDate + (i - dayOfWeek));
    let year = resultDay.getFullYear();
    let month = Number(resultDay.getMonth());
    let day = resultDay.getDate();

    weekInfo[i] = {
      year,
      month,
      day,
      dayOfWeek: dayOfWeekList[i],
    };
  }

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
