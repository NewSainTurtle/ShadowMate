import React, { ChangeEvent, Dispatch, MouseEvent, SetStateAction, useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import { ddayType } from "@util/planner.interface";
import { dateFormat } from "@util/getThisWeek";
import dayjs from "dayjs";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import { DateCalendar, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

interface Props {
  click: number;
  ddayList: ddayType[];
  input: ddayType;
  setInput: Dispatch<SetStateAction<ddayType>>;
  error: boolean;
}

const MyPageDday = ({ click, ddayList, input, setInput, error }: Props) => {
  const [openCalendar, setOpenCalendar] = useState<boolean>(false);
  const { ddayId, ddayTitle, ddayDate } = input;

  const calendarRef = useRef<HTMLDivElement>(null);

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    setInput({ ...input, [name]: value });
  };

  useEffect(() => {
    setInput(ddayList[click]);
  }, [click, ddayList]);

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
          error={error}
          name="ddayTitle"
          value={ddayTitle}
          placeholder="디데이 이름을 입력하세요."
          onChange={onChangeInput}
          helperText={"2 ~ 20자의 이름을 입력할 수 있습니다."}
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
              onChange={(value) => setInput({ ...input, ddayDate: dayjs(value).toDate() })}
            />
          </LocalizationProvider>
        </div>
      )}
    </div>
  );
};

export default MyPageDday;
