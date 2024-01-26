import React, { ChangeEvent, MouseEvent, useEffect, useRef, useState } from "react";
import "react-date-range/dist/styles.css"; // main style file
import "react-date-range/dist/theme/default.css"; // theme css file
import "@styles/common/DataRangePicker.css";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import { DdayItemConfig } from "@util/planner.interface";
import { dateFormat } from "@util/getThisWeek";
import { Calendar } from "react-date-range";
import ko from "date-fns/locale/ko";
import dayjs from "dayjs";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectDdayClick, selectDdayInput, selectDdayList, setDdayInput } from "@store/mypage/ddaySlice";

interface Props {
  newItem: boolean;
}

const Dday = ({ newItem }: Props) => {
  const dispatch = useAppDispatch();
  const click = useAppSelector(selectDdayClick);
  const ddayList = useAppSelector(selectDdayList);
  const ddayInput: DdayItemConfig = useAppSelector(selectDdayInput);
  const [openCalendar, setOpenCalendar] = useState<boolean>(false);
  const [error, setError] = useState<boolean>(false);
  const calendarRef = useRef<HTMLDivElement>(null);
  const minLength = 1;
  const maxLength = 20;
  const { ddayTitle, ddayDate } = ddayInput;
  const [length, setLength] = useState<number>(ddayTitle ? ddayTitle.length : 0);
  const titleFocus = useRef<HTMLInputElement>(null);

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    if (name === "ddayTitle") {
      setLength(value.length);
      if (value.length < minLength || value.length > maxLength) {
        setError(true);
      } else setError(false);
    }
    dispatch(setDdayInput({ ...ddayInput, [name]: value }));
  };

  useEffect(() => {
    if (ddayList.length > 0 && !newItem) {
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

  useEffect(() => {
    if (titleFocus.current) titleFocus.current.focus();
  }, []);

  return (
    <div className={styles["frame__contents"]}>
      <div className={styles["frame__line"]}>
        <Text>디데이 이름</Text>
        <Input
          inputRef={titleFocus}
          name="ddayTitle"
          value={ddayTitle}
          placeholder="디데이 이름을 입력하세요."
          onChange={onChangeInput}
          error={error}
          helperText={
            error ? `${minLength} ~ ${maxLength}자의 이름을 입력할 수 있습니다.` : `글자 수: ${length}/${maxLength}`
          }
          maxLength={maxLength}
        />
      </div>
      <div id={styles["date"]} className={styles["frame__line"]}>
        <Text>디데이 날짜</Text>
        <Input name="ddayDate" value={dateFormat(ddayDate)} disabled maxLength={maxLength} />
        <CalendarMonthIcon onClick={() => setOpenCalendar(!openCalendar)} />
        {openCalendar && (
          <div ref={calendarRef} className={styles["date__picker"]}>
            <Calendar
              locale={ko}
              date={new Date(ddayDate)}
              onChange={(value) => dispatch(setDdayInput({ ...ddayInput, ddayDate: dayjs(value).toDate() }))}
              dateDisplayFormat={"yyyy-mm-dd"}
              minDate={new Date(dayjs().startOf("year").add(-25, "year").toDate())}
              maxDate={new Date(dayjs().endOf("year").add(5, "year").toDate())}
              fixedHeight
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default Dday;
