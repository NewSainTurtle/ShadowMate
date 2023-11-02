import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { useAppSelector } from "@hooks/hook";
import { selectDdayList } from "@store/mypageSlice";
import { DdayConfig } from "@util/planner.interface";
import MyPageDdayItem from "@components/mypage/item/MyPageDdayItem";
import Text from "@components/common/Text";

const DdayList = () => {
  const ddayList = useAppSelector(selectDdayList);
  return (
    <>
      {ddayList.length != 0 ? (
        <>
          {ddayList.map((item: DdayConfig, idx: number) => (
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
