import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import RoutineItem from "@components/mypage/item/RoutineItem";
import { RoutineItemConfig } from "@util/planner.interface";
import { selectRoutineList } from "@store/mypage/routineSlice";
import { useAppSelector } from "@hooks/hook";

const RoutineList = () => {
  const routineList = useAppSelector(selectRoutineList);
  return (
    <>
      {routineList.length != 0 ? (
        <>
          {routineList.map((item: RoutineItemConfig, idx: number) => (
            <RoutineItem key={item.routineId} idx={idx} item={item} />
          ))}
        </>
      ) : (
        <div className={styles["category__none"]}>
          <Text>루틴이 없습니다.</Text>
          <Text>새 루틴을 생성해보세요.</Text>
        </div>
      )}
    </>
  );
};

export default RoutineList;
