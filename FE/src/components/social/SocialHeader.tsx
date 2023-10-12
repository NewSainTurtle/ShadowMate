import React, { useEffect, useState } from "react";
import styles from "@styles/social/Social.module.scss";
import Button from "@components/common/Button";
import Input from "@components/common/Input";
import { userNickname } from "@util/data/SocialData";

interface Props {
  order: string;
  inputValue: string;
  setInputValue: (value: React.SetStateAction<string>) => void;
  setSearchValue: (value: React.SetStateAction<string>) => void;
  setOrder: (value: React.SetStateAction<string>) => void;
}

const SocialHeader = (props: Props) => {
  const { order, inputValue, setInputValue, setSearchValue, setOrder } = props;
  const sortArr = ["최신순", "인기순"];

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const handleOnKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      setSearchValue(inputValue);
    }
  };

  const onClickMyButton = () => {
    setInputValue(userNickname);
    setSearchValue(inputValue);
  };

  const onClickOrder = (name: string) => {
    setOrder(name);
  };

  return (
    <>
      <div className={styles["item-header__search"]}>
        <Input
          types="search"
          placeholder="사용자 닉네임으로 검색 검색"
          value={inputValue}
          onChange={handleInput}
          onKeyPress={handleOnKeyPress}
        />
        <Button types="gray" onClick={onClickMyButton}>
          My
        </Button>
      </div>
      <div className={styles["item-header__sort"]}>
        {sortArr.map((sort, idx) => (
          <span
            className={order != sort ? styles["item-header__sort--none"] : ""}
            key={idx}
            onClick={() => onClickOrder(sort)}
          >
            {sort}
          </span>
        ))}
      </div>
    </>
  );
};

export default SocialHeader;
