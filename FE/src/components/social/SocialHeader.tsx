import React, { useEffect, useRef, useState } from "react";
import styles from "@styles/social/Social.module.scss";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";
import "react-date-range/dist/styles.css";
import "react-date-range/dist/theme/default.css";
import "@styles/common/DataRangePicker.css";
import ko from "date-fns/locale/ko";
import SocialInput from "./SocialInput";
import { DateRangePicker, RangeKeyDict } from "react-date-range";
import {
  SocialConfig,
  selectSocialSort,
  selectSocialKeyword,
  selectSocialDateRange,
  setSocialSort,
  setSocialClear,
  setSocialDateRange,
  setSocialKeyWord,
} from "@store/socialSlice";
import dayjs from "dayjs";
import Text from "@components/common/Text";
import colors from "@util/colors";

const SocialHeader = () => {
  const dispatch = useAppDispatch();
  const userName = useAppSelector(selectUserInfo).nickname;
  const sort: string = useAppSelector(selectSocialSort);
  const keyword: string = useAppSelector(selectSocialKeyword);
  const dateRange: SocialConfig["dateRange"] = useAppSelector(selectSocialDateRange);
  const [openCalendar, setOpenCalendar] = useState(false);
  const calendarRef = useRef<HTMLDivElement>(null);

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => dispatch(setSocialKeyWord(e.target.value));
  const onClickMyButton = () => dispatch(setSocialKeyWord(userName));
  const onClickSort = (name: SocialConfig["sort"]) => dispatch(setSocialSort(name));
  const onChangeDate = (e: RangeKeyDict) => {
    const startDate = dayjs(e.range1.startDate).format("YYYY-MM-DD");
    const endDate = dayjs(e.range1.endDate).format("YYYY-MM-DD");
    dispatch(setSocialDateRange({ startDate, endDate }));
  };
  const onClickClearDate = () => dispatch(setSocialDateRange({ startDate: "", endDate: "" }));

  useEffect(() => {
    dispatch(setSocialClear());
  }, []);

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

  const sortArr: SocialConfig["sort"][] = ["latest", "popularity"];
  const searchDateInfo = (() => {
    if (dateRange.startDate) {
      return `검색 기간 : ${dayjs(dateRange.startDate).format("YYYY-MM-DD")} ~ ${dayjs(dateRange.endDate).format(
        "YYYY-MM-DD",
      )}`;
    }
    return "";
  })();

  const showDateRange = (() => {
    let startDate, endDate;
    if (dateRange.startDate) {
      startDate = dayjs(dateRange.startDate).toDate();
      endDate = dayjs(dateRange.endDate).toDate();
    }
    return { startDate, endDate };
  })();

  const dateRangeStyle = dateRange.startDate ? "" : "--none";

  return (
    <>
      <div className={styles["item-header__search"]}>
        <SocialInput
          name="search"
          placeholder="사용자 닉네임으로 검색"
          value={keyword}
          onChange={handleInput}
          onCalendarClick={() => setOpenCalendar((value) => !value)}
          onMyClick={onClickMyButton}
        />
        {openCalendar && (
          <div ref={calendarRef} className={styles["date__picker"]}>
            <div>
              <DateRangePicker
                className={dateRangeStyle}
                locale={ko}
                ranges={[showDateRange]}
                onChange={onChangeDate}
                dateDisplayFormat={"yyyy-MM-dd"}
                fixedHeight
              />
              <div className={styles["date__picker__button"]} onClick={onClickClearDate}>
                <Text types="small">clear</Text>
              </div>
            </div>
          </div>
        )}
      </div>
      <div className={styles["item-header__box"]}>
        <Text types="small">{searchDateInfo}</Text>
        <div className={styles["item-header__sort"]}>
          {sortArr.map((order, idx) => (
            <span
              className={sort != order ? styles["item-header__sort--none"] : ""}
              key={idx}
              onClick={() => onClickSort(order)}
            >
              {order == "latest" ? "최신순" : "인기순"}
            </span>
          ))}
        </div>
      </div>
    </>
  );
};

export default SocialHeader;
