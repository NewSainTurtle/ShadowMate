import Week from "@components/planner/week/Week";
import WeekItem from "@components/planner/week/WeekItem";
import { useAppDispatch } from "@hooks/hook";
import { setWeekInfo } from "@store/weekSlice";
import { TODO_ITEMS_RESPONSE } from "@util/data/WeekTodos";
import React, { useEffect } from "react";

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
