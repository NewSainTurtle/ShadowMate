import React, { useEffect, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import MonthCalendar from "@components/planner/month/MonthCalendar";
import MonthDetail from "@components/planner/month/MonthDetail";
import Text from "@components/common/Text";
import Loading from "@components/common/Loading";
import dayjs from "dayjs";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { MonthConfig, MonthDayConfig, setFollowCount, setMonthInfo, setStatistics } from "@store/planner/monthSlice";
import { followApi, plannerApi, userApi } from "@api/Api";
import { selectFriendId } from "@store/friendSlice";

const Month = () => {
  const dispatch = useAppDispatch();
  const today = new Date();
  const [selectedDay, setSelectedDay] = useState<string>(dayjs(today).format("YYYY-MM-DD"));
  const year: number = dayjs(selectedDay).year();
  const month: number = dayjs(selectedDay).month() + 1;
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const [loading, setLoading] = useState<boolean>(true);
  const [isOpen, setIsOpen] = useState<boolean>(true);

  const handlePrevMonth = () => {
    const newDate = dayjs(selectedDay).subtract(1, "month").endOf("month").format("MM/DD/YY");
    setSelectedDay(newDate);
  };

  const handleNextMonth = () => {
    const newDate = dayjs(selectedDay).add(1, "month").startOf("month").format("MM/DD/YY");
    setSelectedDay(newDate);
  };

  const getIsOpen = async () => {
    const response = await userApi.getProfiles(friendId);
    const status = response.data.data.plannerAccessScope;
    if (status === "비공개") {
      setIsOpen(false);
    } else {
      setIsOpen(true);
      setLoading(true);
      getMonthInfo();
      getFollowCountInfo();
    }
  };

  const getMonthInfo = () => {
    plannerApi
      .calendars(friendId, { date: dayjs(new Date(year, month - 1, 1)).format("YYYY-MM-DD") })
      .then((res) => {
        const response = res.data.data;
        const dayList: MonthDayConfig[] = response.dayList;
        const plannerAccessScope: MonthConfig["plannerAccessScope"] = response.plannerAccessScope || "전체공개";
        const statistics: MonthConfig["statistics"] = {
          plannerLikeCount: response.plannerLikeCount,
          todoComplete: response.todoComplete,
          todoIncomplete: response.todoIncomplete,
          todoTotal: response.todoTotal,
        };
        dispatch(setMonthInfo({ plannerAccessScope, dayList }));
        dispatch(setStatistics(statistics));
        setLoading(false);
      })
      .catch((err) => console.log(err));
  };

  const getFollowCountInfo = () => {
    followApi
      .getFollowCount(friendId)
      .then((res) => {
        const response = res.data.data;
        dispatch(setFollowCount(response));
      })
      .catch((err) => console.log(err));
  };

  useEffect(() => {
    setSelectedDay(dayjs(new Date()).format("YYYY-MM-DD"));
    getIsOpen();
  }, [friendId]);

  useEffect(() => {
    getMonthInfo();
  }, [selectedDay]);

  return (
    <div className={styles["month"]}>
      <div className={styles["month__calendar"]}>
        <div className={styles["month__title"]}>
          <div>
            <div>
              <Text types="semi-large" title>
                {year}년
              </Text>
              <Text types="semi-large" title>
                {month.toString().length > 1 ? month : "0" + month.toString()}월
              </Text>
            </div>
            <div className={styles["month__button"]} onClick={handlePrevMonth}>
              <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" fillRule="evenodd" clipRule="evenodd">
                <path d="M20 .755l-14.374 11.245 14.374 11.219-.619.781-15.381-12 15.391-12 .609.755z" />
              </svg>
            </div>
            <div className={styles["month__button"]} onClick={handleNextMonth}>
              <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" fillRule="evenodd" clipRule="evenodd">
                <path d="M4 .755l14.374 11.245-14.374 11.219.619.781 15.381-12-15.391-12-.609.755z" />
              </svg>
            </div>
          </div>
        </div>
        {loading ? <Loading /> : <MonthCalendar selectedDay={selectedDay} isOpen={isOpen} />}
      </div>
      <MonthDetail isOpen={isOpen} />
    </div>
  );
};

export default Month;
