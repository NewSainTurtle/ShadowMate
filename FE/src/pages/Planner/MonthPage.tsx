import React, { useEffect } from "react";
import Month from "@components/planner/month/Month";
import { settingApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { setDdayList } from "@store/mypageSlice";

const MonthPage = () => {
  const dispatch = useAppDispatch();
  const userId: number = useAppSelector(selectUserId);

  const getDdayList = () => {
    settingApi
      .ddays(userId)
      .then((res) => {
        const response = res.data.data.ddayList;
        dispatch(setDdayList(response));
      })
      .catch((err) => console.log(err));
  };

  useEffect(() => {
    getDdayList();
  }, []);

  return <Month />;
};

export default MonthPage;
