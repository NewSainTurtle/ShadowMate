import React, { ChangeEvent, useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import CategoryColorList from "@components/mypage/item/CategoryColorList";
import AppleIcon from "@mui/icons-material/Apple";
import MicrosoftIcon from "@mui/icons-material/Microsoft";
import { CategoryColorConfig, CategoryItemConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import {
  selectCategoryClick,
  selectCategoryColors,
  selectCategoryInput,
  selectCategoryList,
  setCategoryColorClick,
  setCategoryInput,
} from "@store/mypage/categorySlice";

interface Props {
  newItem: boolean;
}

const Category = ({ newItem }: Props) => {
  const dispatch = useAppDispatch();
  const click = useAppSelector(selectCategoryClick);
  const categoryList = useAppSelector(selectCategoryList);
  const categoryColors = useAppSelector(selectCategoryColors);
  const categoryInput = useAppSelector(selectCategoryInput);
  const [error, setError] = useState<boolean>(false);
  const minLength = 1;
  const maxLength = 10;
  const { categoryTitle, categoryEmoticon } = categoryInput ?? "";
  const [length, setLength] = useState<number>(categoryTitle ? categoryTitle.length : 0);
  const titleFocus = useRef<HTMLInputElement>(null);

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    if (name === "categoryEmoticon") {
      if (value.length > 2) return;
      if (value !== "" && !value.match("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+")) {
        return;
      }
    }
    if (name === "categoryTitle") {
      setLength(value.length);
      if (value.length < minLength || value.length > maxLength) {
        setError(true);
      } else setError(false);
    }
    dispatch(setCategoryInput({ ...categoryInput, [name]: value }));
  };

  useEffect(() => {
    if (categoryList.length > 0 && categoryInput) {
      let currentColor: number = 0;
      categoryColors.map((item: CategoryColorConfig, idx: number) => {
        if (item.categoryColorCode === categoryList[click].categoryColorCode) currentColor = idx;
      });
      dispatch(setCategoryColorClick(currentColor));
      if (!newItem) {
        dispatch(setCategoryInput(categoryList[click]));
        setLength(categoryList[click].categoryTitle.length);
      }
    }
  }, [click]);

  useEffect(() => {
    if (newItem) dispatch(setCategoryColorClick(0));
    if (titleFocus.current) titleFocus.current.focus();
  }, []);

  return (
    <div className={styles["frame__contents"]}>
      <div className={styles["frame__line"]}>
        <Text>카테고리</Text>
        <Input
          inputRef={titleFocus}
          name="categoryTitle"
          value={categoryTitle ?? ""}
          placeholder="카테고리 이름을 입력하세요."
          onChange={onChangeInput}
          error={error}
          helperText={
            error ? `${minLength} ~ ${maxLength}자의 이름을 입력할 수 있습니다.` : `글자 수: ${length}/${maxLength}`
          }
        />
      </div>
      <div className={styles["frame__line"]}>
        <Text>카테고리 이모지</Text>
        <Input
          name="categoryEmoticon"
          value={categoryEmoticon ?? ""}
          placeholder="카테고리 이모지"
          onChange={onChangeInput}
          helperText={"이모지 외 사용이 불가능합니다."}
        />
      </div>
      <div className={styles["frame__line"]}>
        <div /> {/* 타이틀 빈공간 대체 */}
        <div className={styles["emoji__info"]}>
          <span>
            <MicrosoftIcon />
            <span>
              <Text types="small" bold>
                Window&nbsp;&nbsp;&nbsp;
              </Text>
              <Text types="small">Window + .</Text>
            </span>
          </span>
          <span>
            <AppleIcon />
            <span>
              <Text types="small" bold>
                MacOS&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              </Text>
              <Text types="small">Control + Command + SpaceBar</Text>
            </span>
          </span>
        </div>
      </div>
      <div className={styles["frame__line"]}>
        <Text>카테고리 색상</Text>
        <CategoryColorList />
      </div>
    </div>
  );
};

export default Category;
