import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import { CategoryConfig } from "../MyPageFrame";
import CategoryColorList from "../item/CategoryColorList";

interface Props {
  click: number;
  categoryList: CategoryConfig[];
}

const MyPageCategory = ({ click, categoryList }: Props) => {
  return (
    <div className={styles["frame__contents"]}>
      <div className={styles["frame__line"]}>
        <Text>카테고리 이름</Text>
        <Input name="title" value={categoryList[click].title} placeholder="카테고리 이름을 입력하세요." />
      </div>
      <div className={styles["frame__line"]}>
        <Text>카테고리 이모지</Text>
        <Input
          name="emoticon"
          value={categoryList[click].emoticon}
          placeholder="카테고리 이모지(맥, 윈도우 단축키  설명 넣기)"
        />
      </div>
      <div className={styles["frame__line"]}>
        <Text>카테고리 색상</Text>
        <CategoryColorList />
      </div>
    </div>
  );
};

export default MyPageCategory;
