import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { Stack } from "@mui/material";
import { SwitchButton } from "@components/common/Header/Toggle";
import LockIcon from "@mui/icons-material/Lock";
import LockOpenIcon from "@mui/icons-material/LockOpen";

const MyPageDiary = () => {
  const [open, setOpen] = useState(false);
  const onChange = () => {
    setOpen(!open);
  };

  return (
    <div className={styles["diary__contents"]}>
      <div className={styles["diary__line"]}>
        <Text>다이어리 공개</Text>
        <span>
          <Text types="small">다이어리 공개 시 소셜에 작성된 플래너가 공유됩니다.</Text>
          <div className={styles["toggle"]}>
            <Stack direction="row" spacing={1} alignItems="center">
              <span className={styles["toggle__icon"]}>
                {open ? <LockOpenIcon sx={{ fontSize: "medium" }} /> : <LockIcon sx={{ fontSize: "medium" }} />}
              </span>
              <SwitchButton color="info" checked={open} onChange={onChange} />
            </Stack>
          </div>
        </span>
      </div>
    </div>
  );
};

export default MyPageDiary;
