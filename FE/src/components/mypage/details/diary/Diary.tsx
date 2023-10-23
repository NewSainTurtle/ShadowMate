import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { FormControlLabel, RadioGroup, Stack } from "@mui/material";
import RadioButton from "@components/common/RadioButton";
import SaveIcon from "@mui/icons-material/Save";
import Modal from "@components/common/Modal";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";

const MyPageDiary = () => {
  const [Modalopen, setModalOpen] = useState(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  return (
    <>
      <div className={styles["diary__container"]}>
        <div className={styles["diary__contents"]}>
          <div className={styles["diary__line"]}>
            <Text>다이어리 공개</Text>
            <div>
              <Text types="small">다이어리 공개 시 작성된 플래너가 소셜에 공유됩니다.</Text>
              <div className={styles["radio"]}>
                <Stack direction="row" spacing={1} alignItems="center">
                  <RadioGroup defaultValue="close" name="radio-buttons-group">
                    <FormControlLabel
                      value="close"
                      control={<RadioButton />}
                      label={<Text types="small">비공개</Text>}
                    />
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
          <div className={styles["diary__button"]} onClick={handleOpen}>
            <div className={styles["diary__button--save"]}>
              <SaveIcon />
              <Text>저장</Text>
            </div>
          </div>
        </div>
      </div>
      <Modal open={Modalopen} onClose={handleClose}>
        <div className={styles["diary__modal"]}>
          <WarningAmberRoundedIcon />
          <Text types="small">
            <>전체 공개 및 친구 공개 상태에서 비공개 전환 시,</>
            <br />
            <>소셜에 공유된 게시글이 삭제됩니다.</>
          </Text>
          <Text types="small">계속 진행하시겠습니까?</Text>
          <div>
            <div onClick={handleClose}>
              <Text types="small">취소</Text>
            </div>
            <div>
              <Text types="small">저장</Text>
            </div>
          </div>
        </div>
      </Modal>
    </>
  );
};

export default MyPageDiary;
