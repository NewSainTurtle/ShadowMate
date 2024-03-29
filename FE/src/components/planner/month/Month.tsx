import React, { useEffect, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import w_styles from "@styles/planner/Week.module.scss";
import MonthCalendar from "@components/planner/month/MonthCalendar";
import MonthDetail from "@components/planner/month/MonthDetail";
import MonthGraph from "@components/planner/month/MonthGraph";
import Text from "@components/common/Text";
import Loading from "@components/common/Loading";
import dayjs from "dayjs";
import { NavigateBefore, NavigateNext } from "@mui/icons-material";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { setFollowCount, setMonthInfo, setStatistics } from "@store/planner/monthSlice";
import { followApi, plannerApi } from "@api/Api";
import { selectFriendId } from "@store/friendSlice";
import Popup from "@components/common/Popup";
import { selectPopupVisible } from "@store/modalSlice";

const Month = () => {
  const dispatch = useAppDispatch();
  const today: Date = new Date();
  const [selectedDay, setSelectedDay] = useState<string>(dayjs(today).format("YYYY-MM-DD"));
  const year: number = dayjs(selectedDay).year();
  const month: number = dayjs(selectedDay).month() + 1;
  const userId: number = useAppSelector(selectUserId);
  let friendId: number = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const [loading, setLoading] = useState<boolean>(true);
  const [isOpen, setIsOpen] = useState<boolean>(true);
  const visible: boolean = useAppSelector(selectPopupVisible);

  const handlePrevMonth = () => {
    const newDate = dayjs(selectedDay).subtract(1, "month").endOf("month").format("MM/DD/YY");
    setSelectedDay(newDate);
  };

  const handleToday = () => {
    setSelectedDay(dayjs(today).format("YYYY-MM-DD"));
  };

  const handleNextMonth = () => {
    const newDate = dayjs(selectedDay).add(1, "month").startOf("month").format("MM/DD/YY");
    setSelectedDay(newDate);
  };

  const getMonthInfo = () => {
    plannerApi
      .calendars(friendId, { date: dayjs(new Date(year, month - 1, 1)).format("YYYY-MM-DD") })
      .then((res) => {
        const response = res.data.data;
        const dayList = response.dayList;
        const plannerAccessScope = response.plannerAccessScope;
        const statistics = {
          plannerLikeCount: response.plannerLikeCount,
          todoComplete: response.todoComplete,
          todoIncomplete: response.todoIncomplete,
          todoTotal: response.todoTotal,
        };
        if (dayList.length < 1) setIsOpen(false);
        else setIsOpen(true);
        dispatch(setMonthInfo({ plannerAccessScope, dayList }));
        dispatch(setStatistics(statistics));
        setLoading(false);
      })
      .catch((err) => console.error(err));
  };

  const getFollowCountInfo = () => {
    followApi
      .getFollowCount(friendId)
      .then((res) => {
        const response = res.data.data;
        dispatch(setFollowCount(response));
      })
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    setLoading(true);
    setSelectedDay(dayjs(new Date()).format("YYYY-MM-DD"));
    getMonthInfo();
    getFollowCountInfo();
  }, [friendId]);

  useEffect(() => {
    setLoading(true);
    getMonthInfo();
  }, [selectedDay]);

  return (
    <div className={styles["month"]}>
      <div className={styles["month__calendar"]}>
        <div className={styles["month__title"]}>
          <div>
            <div>
              <div>
                <Text types="semi-large" title>
                  {year}년
                </Text>
                <Text types="semi-large" title>
                  {month.toString().length > 1 ? month : "0" + month.toString()}월
                </Text>
              </div>
              <div>
                <div className={w_styles["week__button"]} onClick={handlePrevMonth}>
                  <NavigateBefore />
                </div>
                <div className={w_styles["week__today"]} onClick={handleToday}>
                  <Text bold>today</Text>
                </div>
                <div className={w_styles["week__button"]} onClick={handleNextMonth}>
                  <NavigateNext />
                </div>
              </div>
            </div>
            {isOpen && <MonthGraph />}
          </div>
        </div>
        {loading ? <Loading /> : <MonthCalendar selectedDay={selectedDay} isOpen={isOpen} />}
      </div>
      <MonthDetail isOpen={isOpen} />
      {visible && <Popup />}
    </div>
  );
};

export default Month;
