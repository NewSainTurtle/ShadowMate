import React, { useEffect } from "react";
import Month from "@components/planner/month/Month";
import { settingApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { setCategoryColors, setCategoryInput, setCategoryList } from "@store/mypage/categorySlice";
import { setDdayList } from "@store/mypage/ddaySlice";
import { CategoryItemConfig } from "@util/planner.interface";

const MonthPage = () => {
  const dispatch = useAppDispatch();
  const userId: number = useAppSelector(selectUserId);

  const getCategoryList = () => {
    settingApi
      .categories(userId)
      .then((res) => {
        let response: CategoryItemConfig[] = res.data.data.categoryList;
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
