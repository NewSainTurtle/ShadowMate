import React, { ChangeEvent, useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import Modal from "@components/common/Modal";
import CategorySelector from "@components/common/CategorySelector";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import { CategoryItemConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectRoutineClick, selectRoutineInput, selectRoutineList, setRoutineInput } from "@store/mypage/routineSlice";

const Routine = () => {
  const dispatch = useAppDispatch();
  const routineList = useAppSelector(selectRoutineList);
  const routineInput = useAppSelector(selectRoutineInput);
  const routineClick = useAppSelector(selectRoutineClick);
  const { routineContent, category, startDay, endDay, days } = routineInput;
  const [Modalopen, setModalOpen] = useState<boolean>(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  const dayList = ["월", "화", "수", "목", "금", "토", "일"];

  const [checkedList, setCheckedList] = useState<string[]>([]);
  const [isChecked, setIsChecked] = useState<boolean>(false);
  const [openCalendar, setOpenCalendar] = useState<boolean>(false);

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    dispatch(setRoutineInput({ ...routineInput, [name]: value }));
  };

  const handleClickCategory = (props: CategoryItemConfig) => {
    dispatch(setRoutineInput({ ...routineInput, category: props }));
    handleClose();
  };

  const handleCheckedDay = (isChecked: boolean, day: string) => {
    if (isChecked) {
      setCheckedList((prev) => [...prev, day]);
      return;
    }
    if (!isChecked && checkedList.includes(day)) {
      setCheckedList(checkedList.filter((item) => item !== day));
      return;
    }
    return;
  };

  const handleRoutineDay = (e: ChangeEvent<HTMLInputElement>, day: string) => {
    setIsChecked(!isChecked);
    handleCheckedDay(e.target.checked, day);
  };

  useEffect(() => {
    dispatch(setRoutineInput(routineList[routineClick]));
    setCheckedList(routineList[routineClick].days);
  }, [routineClick]);

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
          />
        </div>

        <div className={styles["frame__line"]}>
          <Text>카테고리</Text>
          <Input
            name="routineCategory"
            value={category?.categoryId === 0 ? "카테고리 없음" : category.categoryTitle}
            onClick={handleOpen}
            style={{ caretColor: "transparent" }}
            placeholder="루틴의 카테고리를 선택하세요."
          />
        </div>

        <div className={styles["frame__line"]}>
          <Text>반복 요일</Text>
          <div className={styles["routine__day-list"]}>
            {dayList.map((day: string, i: number) => {
              const clicked = checkedList.includes(day) ? "--click" : "";
              return (
                <div key={i}>
                  <input
                    type="checkbox"
                    id={day}
                    defaultChecked={checkedList.includes(day)}
                    onChange={(e) => handleRoutineDay(e, day)}
                  />
                  <label className={styles[`routine__day-item${clicked}`]} htmlFor={day}>
                    {day}
                  </label>
                </div>
              );
            })}
          </div>
        </div>
        <div>
          <div className={styles["frame__line"]}>
            <Text>루틴 기간</Text>
            <div className={styles["routine__period"]}>
              <div>
                <Input name="routinePeriodStart" value={startDay} placeholder="시작 일자" />
                <CalendarMonthIcon onClick={() => setOpenCalendar(!openCalendar)} />
              </div>
              <div>
                <Input name="routinePeriodEnd" value={endDay} placeholder="종료 일자" />
                <CalendarMonthIcon onClick={() => setOpenCalendar(!openCalendar)} />
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
