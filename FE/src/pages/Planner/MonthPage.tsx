import React, { useEffect } from "react";
import Month from "@components/planner/month/Month";
import { settingApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { setCategoryInput, setCategoryList, setDdayList } from "@store/mypageSlice";
import { CategoryConfig } from "@util/planner.interface";

const MonthPage = () => {
  const dispatch = useAppDispatch();
  const userId: number = useAppSelector(selectUserId);

  const getCategoryList = () => {
    settingApi
      .categories(userId)
      .then((res) => {
        let response: CategoryConfig[] = res.data.data.categoryList;
        if (response.length != 0) {
          dispatch(setCategoryList(response));
          dispatch(setCategoryInput(response[0]));
        }
      })
      .catch((err) => console.log(err));
  };

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
    getCategoryList();
    getDdayList();
  }, []);

  return <Month />;
};

export default MonthPage;
