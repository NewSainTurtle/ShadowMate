import React, { Dispatch, SetStateAction, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";

interface Props {
  isEdit: boolean;
  setIsEdit: Dispatch<SetStateAction<boolean>>;
}

const Introduction = ({ isEdit, setIsEdit }: Props) => {
  const [introduction, setIntroduction] = useState<string>("안녕하세요~");
  return (
    <div className={styles["introduction"]}>
      {isEdit ? (
        <div className={styles["introduction__edit"]}>
          <textarea value={introduction} onChange={(e) => setIntroduction(e.target.value)} />
        </div>
      ) : (
        <Text>{introduction}</Text>
      )}
    </div>
  );
};

export default Introduction;
