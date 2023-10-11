import React, { Children, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { FormControlLabel, Radio, RadioGroup, Stack } from "@mui/material";
import RadioButton from "@components/common/RadioButton";
import SaveIcon from "@mui/icons-material/Save";

const MyPageDiary = () => {
  return (
    <div className={styles["diary__container"]}>
      <div className={styles["diary__contents"]}>
        <div className={styles["diary__line"]}>
          <Text>다이어리 공개</Text>
          <div>
            <Text types="small">다이어리 공개 시 작성된 플래너가 소셜에 공유됩니다.</Text>
            <div className={styles["radio"]}>
              <Stack direction="row" spacing={1} alignItems="center">
                <RadioGroup defaultValue="close" name="radio-buttons-group">
                  <FormControlLabel value="close" control={<RadioButton />} label={<Text types="small">비공개</Text>} />
                  <FormControlLabel
                    value="friendOpen"
                    control={<RadioButton />}
                    label={<Text types="small">친구 공개</Text>}
                  />
                  <FormControlLabel
                    value="open"
                    control={<RadioButton />}
                    label={<Text types="small">전체 공개</Text>}
                  />
                </RadioGroup>
              </Stack>
            </div>
          </div>
        </div>
        <div className={styles["diary__button"]}>
          <div className={styles["diary__button--save"]}>
            <SaveIcon />
            <Text>저장</Text>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MyPageDiary;
