import React, { ChangeEvent, useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import Modal from "@components/common/Modal";
import CategorySelector from "@components/common/CategorySelector";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import { CategoryItemConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectRoutineClick, selectRoutineInput, selectRoutineList, setRoutineInput } from "@store/mypage/routineSlice";
import { Calendar } from "react-date-range";
import { dateFormat } from "@util/getThisWeek";
import dayjs from "dayjs";
import ko from "date-fns/locale/ko";

interface Props {
  dayError: boolean;
}

const Routine = ({ dayError }: Props) => {
  const dispatch = useAppDispatch();
  const routineList = useAppSelector(selectRoutineList);
  const routineInput = useAppSelector(selectRoutineInput);
  const routineClick = useAppSelector(selectRoutineClick);
  const { routineContent, category, startDay, endDay, days } = routineInput;
  const [Modalopen, setModalOpen] = useState<boolean>(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  const minLength = 1;
  const maxLength = 50;
  const [error, setError] = useState<boolean>(false);
  const [length, setLength] = useState<number>(routineContent ? routineContent.length : 0);

  const dayList = ["월", "화", "수", "목", "금", "토", "일"];

  const [openStartCalendar, setOpenStartCalendar] = useState<boolean>(false);
  const [openEndCalendar, setOpenEndCalendar] = useState<boolean>(false);
  const startCalendarRef = useRef<HTMLDivElement>(null);
  const endCalendarRef = useRef<HTMLDivElement>(null);

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    if (name === "routineContent") {
      setLength(value.length);
      if (value.length < minLength || value.length > maxLength) {
        setError(true);
      } else setError(false);
    }
    dispatch(setRoutineInput({ ...routineInput, [name]: value }));
  };

  const handleClickCategory = (props: CategoryItemConfig) => {
    dispatch(setRoutineInput({ ...routineInput, category: props }));
    handleClose();
  };

  const handleRoutineDay = (e: ChangeEvent<HTMLInputElement>, day: string) => {
    const isChecked = e.target.checked;
    if (isChecked) {
      dispatch(setRoutineInput({ ...routineInput, days: [...days, day] }));
      return;
    }
    if (!isChecked && days.includes(day)) {
      const filteredDays = days.filter((item: string) => item !== day);
      dispatch(setRoutineInput({ ...routineInput, days: filteredDays }));
      return;
    }
  };

  const handleStartCalendar = (date: Date) => {
    if (date > endDay) dispatch(setRoutineInput({ ...routineInput, startDay: date, endDay: date }));
    else dispatch(setRoutineInput({ ...routineInput, startDay: date }));
  };

  const handleEndCalendar = (date: Date) => {
    dispatch(setRoutineInput({ ...routineInput, endDay: date }));
  };

  // 특정 영역 외 클릭 시 발생하는 이벤트
  const handleFocus = (event: CustomEvent<MouseEvent>) => {
    if (startCalendarRef || endCalendarRef) {
      if (startCalendarRef.current && !startCalendarRef.current.contains(event.target as Node)) {
        setOpenStartCalendar(false);
      }
      if (endCalendarRef.current && !endCalendarRef.current.contains(event.target as Node)) {
        setOpenEndCalendar(false);
      }
    }
  };

  useEffect(() => {
    if (routineList.length > 0 && routineInput) {
      dispatch(setRoutineInput(routineList[routineClick]));
      setLength(routineList[routineClick].routineContent.length);
    }
  }, [routineClick]);

  /* 캘린더 외부 영역 클릭 시 캘린더 닫힘. */
  useEffect(() => {
    // 이벤트 리스너에 handleFocus 함수 등록
    document.addEventListener("mousedown", handleFocus as EventListener);
    return () => {
      document.removeEventListener("mouseup", handleFocus as EventListener);
    };
  }, [startCalendarRef, endCalendarRef]);

  const handleCategory = () => {
    if (!category) return "";
    else if (category.categoryId === 0) return "카테고리 없음";
    else return category.categoryTitle;
  };

  return (
    <>
      <div className={styles["frame__contents"]}>
        <div className={styles["frame__line"]}>
          <Text>루틴 이름</Text>
          <Input
            name="routineContent"
            value={routineContent}
            placeholder="루틴 이름을 입력하세요."
            onChange={onChangeInput}
            error={error}
            helperText={
              error ? `${minLength} ~ ${maxLength}자의 이름을 입력할 수 있습니다.` : `글자 수: ${length}/${maxLength}`
            }
          />
        </div>
        <div className={styles["frame__line"]}>
          <Text>카테고리</Text>
          <Input
            name="routineCategory"
            value={handleCategory()}
            onClick={handleOpen}
            style={{ caretColor: "transparent" }}
            placeholder="루틴의 카테고리를 선택하세요."
            autoComplete="off"
          />
        </div>
        <div id={dayError ? styles["routine__days"] : ""} className={styles["frame__line"]}>
          <Text>반복 요일</Text>
          <div className={styles["routine__day-list"]}>
            {dayList.map((day: string, i: number) => {
              return (
                <div className={styles["routine__day-item"]} key={i}>
                  <input
                    type="checkbox"
                    id={day}
                    checked={days.includes(day)}
                    onChange={(e) => handleRoutineDay(e, day)}
                  />
                  <label htmlFor={day}>{day}</label>
                </div>
              );
            })}
          </div>
          <div />
          {dayError && (
            <div id={styles["routine__error"]}>
              <Text types="small">반복할 요일을 선택하세요.</Text>
            </div>
          )}
        </div>
        <div>
          <div className={styles["frame__line"]}>
            <Text>루틴 기간</Text>
            <div className={styles["routine__period"]}>
              <div className={styles["start"]}>
                <Input name="startDay" value={dateFormat(startDay)} placeholder="시작 일자" disabled />
                <CalendarMonthIcon onClick={() => setOpenStartCalendar(!openStartCalendar)} />
                {openStartCalendar && (
                  <div ref={startCalendarRef} className={styles["date__picker"]}>
                    <Calendar
                      locale={ko}
                      date={new Date(startDay)}
                      onChange={(date) => handleStartCalendar(date)}
                      dateDisplayFormat={"yyyy-mm-dd"}
                      minDate={new Date(dayjs().startOf("year").add(-25, "year").toDate())}
                      maxDate={new Date(dayjs().endOf("year").add(5, "year").toDate())}
                      fixedHeight
                    />
                  </div>
                )}
              </div>
              <div className={styles["end"]}>
                <Input name="endDay" value={dateFormat(endDay)} placeholder="종료 일자" disabled />
                <CalendarMonthIcon onClick={() => setOpenEndCalendar(!openEndCalendar)} />
                {openEndCalendar && (
                  <div ref={endCalendarRef} className={styles["date__picker"]}>
                    <Calendar
                      locale={ko}
                      date={new Date(endDay)}
                      onChange={(date) => handleEndCalendar(date)}
                      dateDisplayFormat={"yyyy-mm-dd"}
                      minDate={new Date(dayjs(startDay).toDate())}
                      maxDate={new Date(dayjs().endOf("year").add(5, "year").toDate())}
                      fixedHeight
                    />
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
      <Modal types="noBtn" open={Modalopen} onClose={handleClose}>
        <CategorySelector type="day" handleClick={handleClickCategory} />
      </Modal>
    </>
  );
};

export default Routine;
