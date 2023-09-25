const getThisWeek = () => {
  const currentDay = new Date();
  const theYear = currentDay.getFullYear();
  const theMonth = currentDay.getMonth();
  const theDate = currentDay.getDate();
  const theDayOfWeek = currentDay.getDay();

  let thisWeek = [];
  let theDayOfWeekList = ["월", "화", "수", "목", "금", "토", "일"];

  for (var i = 1; i <= 7; i++) {
    const resultDay = new Date(theYear, theMonth, theDate + (i - theDayOfWeek));
    let mm = Number(resultDay.getMonth()) + 1;
    let dd = resultDay.getDate();

    thisWeek[i] = mm + "월 " + dd + "일 (" + theDayOfWeekList[i - 1] + ")";
  }
  return thisWeek;
};

export default getThisWeek;
