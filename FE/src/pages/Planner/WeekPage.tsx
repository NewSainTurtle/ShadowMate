import React, { useEffect } from "react";
import Week from "@components/planner/week/Week";
import { useAppDispatch } from "@hooks/hook";
import { setWeekInfo } from "@store/planner/weekSlice";
import { TODO_ITEMS_RESPONSE } from "@util/data/WeekTodos";

const WeekPage = () => {
  return <Week />;
};

export default WeekPage;
