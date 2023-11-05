import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { useAppSelector } from "@hooks/hook";
import { DdayItemConfig } from "@util/planner.interface";
import MyPageDdayItem from "@components/mypage/item/MyPageDdayItem";
import Text from "@components/common/Text";
import { selectDdayList } from "@store/mypage/ddaySlice";

const DdayList = () => {
  const ddayList = useAppSelector(selectDdayList) || [];
  return (
    <>
      {ddayList.length != 0 ? (
        <>
          {ddayList.map((item: DdayItemConfig, idx: number) => (
            <MyPageDdayItem key={idx} idx={idx} item={item} />
          ))}
        </>
      ) : (
        <div className={styles["category__none"]}>
          <Text>디데이가 없습니다.</Text>
          <Text>새 디데이를 생성해보세요.</Text>
        </div>
      )}
    </>
  );
};

export default DdayList;
