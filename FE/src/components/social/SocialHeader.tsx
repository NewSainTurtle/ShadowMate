import React, { useEffect, useRef, useState } from "react";
import styles from "@styles/social/Social.module.scss";
import { useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";
import "react-date-range/dist/styles.css";
import "react-date-range/dist/theme/default.css";
import "@styles/common/DataRangePicker.css";
import ko from "date-fns/locale/ko";
import SocialInput from "./SocialInput";
import dayjs from "dayjs";
import { DateRangePicker, RangeKeyDict } from "react-date-range";

interface Props {
  order: string;
  searchKeyWord: string;
  setSearchKeyWord: (value: React.SetStateAction<string>) => void;
  setOrder: (value: React.SetStateAction<"latest" | "popularity">) => void;
}

const SocialHeader = (props: Props) => {
  const { order, searchKeyWord, setSearchKeyWord, setOrder } = props;
  const sortArr: ("latest" | "popularity")[] = ["latest", "popularity"];
  const userName = useAppSelector(selectUserInfo).nickname;
  const [openCalendar, setOpenCalendar] = useState(false);
  const [dateRange, setDateRange] = useState({
    startDate: new Date(),
    endDate: new Date(dayjs().add(-7, "day").toDate()),
  });
  const calendarRef = useRef<HTMLDivElement>(null);

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyWord(e.target.value);
  };

  const onClickMyButton = () => {
    setSearchKeyWord(userName);
  };

  const onClickOrder = (name: "latest" | "popularity") => {
    setOrder(name);
  };

  const onChangeDate = (e: RangeKeyDict) => {
    const startDate = e.range1.startDate ?? dateRange.startDate;
    const endDate = e.range1.endDate ?? dateRange.endDate;
    setDateRange({ startDate, endDate });
  };

  useEffect(() => {
    /**  캘린더 외부 영역 클릭 시 발생하는 이벤트*/
    const handleFocus = (e: CustomEvent<MouseEvent>) => {
      if (calendarRef.current && !calendarRef.current.contains(e.target as Node)) {
        setOpenCalendar(false);
      }
    };

    document.addEventListener("mousedown", handleFocus as EventListener);
    return () => {
      document.removeEventListener("mouseup", handleFocus as EventListener);
    };
  }, [calendarRef]);

  return (
    <>
      <div className={styles["item-header__search"]}>
        <SocialInput
          name="search"
          placeholder="사용자 닉네임으로 검색 검색"
          value={searchKeyWord}
          onChange={handleInput}
          onCalendarClick={() => {
            setOpenCalendar((value) => !value);
          }}
          onMyClick={onClickMyButton}
        />
        {openCalendar && (
          <div ref={calendarRef} className={styles["date__picker"]}>
            <DateRangePicker
              locale={ko}
              ranges={[dateRange]}
              onChange={onChangeDate}
              showDateDisplay
              dateDisplayFormat={"yyyy-mm-dd"}
              fixedHeight
            />
          </div>
        )}
      </div>
      <div className={styles["item-header__sort"]}>
        {sortArr.map((sort, idx) => (
          <span
            className={order != sort ? styles["item-header__sort--none"] : ""}
            key={idx}
            onClick={() => onClickOrder(sort)}
          >
            {sort == "latest" ? "최신순" : "인기순"}
          </span>
        ))}
      </div>
    </>
  );
};

export default SocialHeader;
