import React, { ChangeEvent, Dispatch, MouseEvent, SetStateAction, useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import { DdayItemConfig } from "@util/planner.interface";
import { dateFormat } from "@util/getThisWeek";
import dayjs from "dayjs";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import { DateCalendar, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectDdayClick, selectDdayInput, selectDdayList, setDdayInput } from "@store/mypage/ddaySlice";

const MyPageDday = () => {
  const dispatch = useAppDispatch();
  const click = useAppSelector(selectDdayClick);
  const ddayList = useAppSelector(selectDdayList);
  const ddayInput: DdayItemConfig = useAppSelector(selectDdayInput);
  const [openCalendar, setOpenCalendar] = useState<boolean>(false);
  const [error, setError] = useState<boolean>(false);
  const calendarRef = useRef<HTMLDivElement>(null);

  const { ddayTitle, ddayDate } = ddayInput || "";
  const [length, setLength] = useState<number>(ddayTitle ? ddayTitle.length : 0);

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    if (name === "ddayTitle") {
      setLength(value.length);
      if (value.length < 2 || value.length >= 20) {
        setError(true);
      } else setError(false);
    }
    dispatch(setDdayInput({ ...ddayInput, [name]: value }));
  };

  useEffect(() => {
    if (ddayList.length > 0 && ddayInput) {
      dispatch(setDdayInput(ddayList[click]));
      setLength(ddayList[click].ddayTitle.length);
    }
  }, [click]);

  /* 캘린더 외부 영역 클릭 시 캘린더 닫힘. */
  useEffect(() => {
    // 특정 영역 외 클릭 시 발생하는 이벤트
    const handleFocus = (e: CustomEvent<MouseEvent>) => {
      if (calendarRef.current && !calendarRef.current.contains(e.target as Node)) {
        setOpenCalendar(false);
      }
    };

    // 이벤트 리스너에 handleFocus 함수 등록
    document.addEventListener("mousedown", handleFocus as EventListener);
    return () => {
      document.removeEventListener("mouseup", handleFocus as EventListener);
    };
  }, [calendarRef]);

  return (
    <div className={styles["frame__contents"]}>
      <div className={styles["frame__line"]}>
        <Text>디데이 이름</Text>
        <Input
          name="ddayTitle"
          value={ddayTitle}
          placeholder="디데이 이름을 입력하세요."
          onChange={onChangeInput}
          error={error}
          helperText={error ? "2 ~ 20자의 이름을 입력할 수 있습니다." : `글자 수: ${length}/20`}
        />
      </div>
      <div id={styles["date"]} className={styles["frame__line"]}>
        <Text>디데이 날짜</Text>
        <Input name="ddayDate" value={dateFormat(ddayDate)} disabled />
        <CalendarMonthIcon onClick={() => setOpenCalendar(!openCalendar)} />
      </div>
      {openCalendar && (
        <div ref={calendarRef} className={styles["date__picker"]}>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DateCalendar
              value={dayjs(ddayDate)}
              onChange={(value) => dispatch(setDdayInput({ ...ddayInput, ddayDate: dayjs(value).toDate() }))}
            />
          </LocalizationProvider>
        </div>
      )}
    </div>
  );
};

export default MyPageDday;
