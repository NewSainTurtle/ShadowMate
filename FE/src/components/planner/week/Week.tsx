import React, { useLayoutEffect, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import WeekList from "@components/planner/week/list/WeekList";
import WeekTodo from "@components/planner/week/todo/WeekTodo";
import Text from "@components/common/Text";
import FriendProfile from "@components/common/FriendProfile";
import { NavigateBefore, NavigateNext } from "@mui/icons-material";
import { getThisWeek, getThisWeekCnt } from "@util/getThisWeek";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { DayListConfig, selectDayList, selectThisWeek, setThisWeek, setWeekInfo } from "@store/planner/weekSlice";
import { selectUserId } from "@store/authSlice";
import { plannerApi } from "@api/Api";
import { selectFriendId, selectFriendInfo } from "@store/friendSlice";
import dayjs from "dayjs";

const Week = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const friendInfo = useAppSelector(selectFriendInfo);
  const dayList = useAppSelector(selectDayList);
  const week = useAppSelector(selectThisWeek);
  const thisWeekCnt = getThisWeekCnt(week);
  const [isMine, setIsMine] = useState<boolean>(userId === friendId);
  const [retroClick, setRetroClick] = useState<number>(-1);

  const handleButton = (to: string) => {
    const date = week.getDate();
    if (to === "forward") {
      dispatch(setThisWeek(new Date(week.setDate(date - 7))));
    } else if (to === "backward") {
      dispatch(setThisWeek(new Date(week.setDate(date + 7))));
    }
  };

  const getDayList = () => {
    const dates = getThisWeek(week);
    plannerApi
      .weekly(friendId, { "start-date": dates[0], "end-date": dates[1] })
      .then((res) => {
        const response = res.data.data;
        dispatch(
          setWeekInfo({
            plannerAccessScope: response.plannerAccessScope,
            dday: response.dday,
            weeklyTodos: response.weeklyTodos,
            dayList: response.dayList,
            thisWeek: week,
          }),
        );
      })
      .catch((err) => console.log(err));
  };

  useLayoutEffect(() => {
    getDayList();
    if (friendId === userId) setIsMine(true);
  }, [week, friendId]);

  return (
    <div className={styles["week"]}>
      <div className={styles["week__title"]}>
        <div>
          <Text types="semi-large" bold>
            {thisWeekCnt.weekYear}년 {thisWeekCnt.weekMonth + 1}월 {thisWeekCnt.weekCnt}주차
          </Text>
          <div>
            <div className={styles["week__button"]} onClick={() => handleButton("forward")}>
              <NavigateBefore />
            </div>
            <div
              className={styles["week__today"]}
              onClick={() => dispatch(setThisWeek(dayjs(getThisWeek(new Date())[0]).toDate()))}
            >
              <Text bold>today</Text>
            </div>
            <div className={styles["week__button"]} onClick={() => handleButton("backward")}>
              <NavigateNext />
            </div>
          </div>
        </div>
        {!isMine && (
          <div>
            <FriendProfile types="기본" profile={friendInfo} />
          </div>
        )}
      </div>
      <div className={styles["week__list"]}>
        <WeekTodo isMine={isMine} />
        {dayList?.map((today: DayListConfig, key: number) => (
          <WeekList
            key={key}
            idx={key}
            isMine={isMine}
            today={today}
            retroClick={retroClick}
            setRetroClick={setRetroClick}
          />
        ))}
      </div>
    </div>
  );
};

export default Week;
