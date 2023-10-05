import React, { ChangeEvent, ChangeEventHandler, Dispatch, SetStateAction, useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import CategoryColorList from "../item/CategoryColorList";
import { categoryType } from "@util/planner.interface";
import EmojiPicker, { EmojiClickData } from "emoji-picker-react";
import { ClickAwayListener } from "@mui/material";
import { COLORS } from "@util/data/CategoryData";

interface Props {
  click: number;
  categoryList: categoryType[];
  input: categoryType;
  setInput: Dispatch<SetStateAction<categoryType>>;
  colorClick: number;
  setColorClick: Dispatch<SetStateAction<number>>;
}

const MyPageCategory = ({ click, categoryList, input, setInput, colorClick, setColorClick }: Props) => {
  const [openEmoji, setOpenEmoji] = useState<boolean>(false);
  const { categoryId, categoryTitle, categoryEmoticon, categoryColorCode } = input;

  const onEmojiClick = (emojiData: EmojiClickData) => {
    setInput({ ...input, categoryEmoticon: emojiData.emoji });
  };

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    setInput({ ...input, [name]: value });
  };

  useEffect(() => {
    let currentColor: number = 0;
    COLORS.map((item, idx) => {
      if (item === categoryList[click].categoryColorCode) currentColor = idx;
    });
    setColorClick(currentColor);
    setInput(categoryList[click]);
  }, [click, categoryList]);

  return (
    <div className={styles["frame__contents"]}>
      <div className={styles["frame__line"]}>
        <Text>카테고리 이름</Text>
        <Input
          name="categoryTitle"
          value={categoryTitle}
          placeholder="카테고리 이름을 입력하세요."
          onChange={onChangeInput}
        />
      </div>
      <div className={styles["frame__line"]}>
        <Text>카테고리 이모지</Text>
        <div onClick={() => setOpenEmoji(true)}>
          <Input
            name="categoryEmoticon"
            value={categoryEmoticon}
            placeholder="카테고리 이모지"
            onChange={onChangeInput}
          />
          {openEmoji && (
            <ClickAwayListener
              onClickAway={() => {
                setOpenEmoji(false);
              }}
            >
              <div className={styles["emoji"]}>
                <EmojiPicker onEmojiClick={onEmojiClick} searchDisabled />
              </div>
            </ClickAwayListener>
          )}
        </div>
      </div>
      <div className={styles["frame__line"]}>
        <Text>카테고리 색상</Text>
        <CategoryColorList click={colorClick} setClick={setColorClick} />
      </div>
    </div>
  );
};

export default MyPageCategory;
