import React, { useState } from "react";
import styles from "@styles/social/Social.module.scss";
import { useAppSelector } from "@hooks/hook";
import { selectUserInfo } from "@store/authSlice";
import SocialInput from "./SocialInput";

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

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyWord(e.target.value);
  };

  const onClickMyButton = () => {
    setSearchKeyWord(userName);
  };

  const onClickOrder = (name: "latest" | "popularity") => {
    setOrder(name);
  };

  return (
    <>
      <div className={styles["item-header__search"]}>
        <SocialInput
          name="search"
          placeholder="사용자 닉네임으로 검색 검색"
          value={searchKeyWord}
          onChange={handleInput}
          onCalendarClick={() => {
            setOpenCalendar((open) => !open);
          }}
          onMyClick={onClickMyButton}
        />
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
