import React, { useLayoutEffect } from "react";
import Month from "@components/planner/month/Month";
import { followApi, settingApi, userApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId, setUserInfo } from "@store/authSlice";
import { setCategoryColors, setCategoryInput, setCategoryList } from "@store/mypage/categorySlice";
import { setDdayList } from "@store/mypage/ddaySlice";
import { CategoryItemConfig } from "@util/planner.interface";
import { setFollowingList } from "@store/friendSlice";
import { setRoutineInput, setRoutineList } from "@store/mypage/routineSlice";

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

  const getCategoryColors = () => {
    settingApi
      .categoriesColors(userId)
      .then((res) => {
        const response = res.data.data.categoryColorList;
        dispatch(setCategoryColors(response));
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const getDdayList = () => {
    settingApi
      .ddays(userId)
      .then((res) => {
        const response = res.data.data.ddayList;
        if (response.length != 0) {
          dispatch(setDdayList(response));
        }
      })
      .catch((err) => console.log(err));
  };

  const getProfileInfo = () => {
    userApi
      .getProfiles(userId)
      .then((res) => {
        dispatch(setUserInfo(res.data.data));
      })
      .catch((err) => console.log(err));
  };

  const getFollowing = () => {
    followApi
      .getFollowing(userId)
      .then((res) => {
        const response = res.data.data;
        dispatch(setFollowingList(response));
      })
      .catch((err) => console.error(err));
  };

  useLayoutEffect(() => {
    getProfileInfo();
    getCategoryList();
    getCategoryColors();
    getDdayList();
    getFollowing();
  }, []);

  return <Month />;
};

export default MonthPage;
