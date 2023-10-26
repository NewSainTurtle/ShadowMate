import React, { useEffect } from "react";
import Week from "@components/planner/week/Week";
import { useAppDispatch } from "@hooks/hook";
import { setWeekInfo } from "@store/weekSlice";
import { TODO_ITEMS_RESPONSE } from "@util/data/WeekTodos";

const WeekPage = () => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    const response = TODO_ITEMS_RESPONSE;
    dispatch(
      setWeekInfo({
        plannerAccessScope: response.data.plannerAccessScope,
        dday: response.data.dday,
        weeklyTodo: response.data.weeklyTodo,
        dayList: response.data.dayList,
      }),
    );
  }, []);

  return <Week />;
};

export default WeekPage;
