import React, { ChangeEvent, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { FormControlLabel, RadioGroup, Stack } from "@mui/material";
import RadioButton from "@components/common/RadioButton";
import SaveIcon from "@mui/icons-material/Save";
import Modal from "@components/common/Modal";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { MonthConfig, selectPlannerAccessScope, setPlannerAccessScope } from "@store/planner/monthSlice";
import { settingApi } from "@api/Api";

const MyPageDiary = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const accessScope = useAppSelector(selectPlannerAccessScope);
  const [scope, setScope] = useState<MonthConfig["plannerAccessScope"]>(accessScope);
  const [Modalopen, setModalOpen] = useState(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  const handleSave = () => {
    settingApi
      .accessScopes(userId, { plannerAccessScope: scope })
      .then(() => {
        dispatch(setPlannerAccessScope(scope));
        handleClose();
      })
      .catch((err) => console.log(err));
  };

  const onChangeRadio = (e: ChangeEvent<HTMLInputElement>) => {
    const radio = e.target.value;
    if (radio === "전체공개" || radio === "친구공개" || radio === "비공개") {
      setScope(radio);
    }
  };

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
                  <RadioGroup value={scope} onChange={onChangeRadio}>
                    <FormControlLabel
                      value="비공개"
                      control={<RadioButton />}
                      label={<Text types="small">비공개</Text>}
                    />
                    <FormControlLabel
                      value="친구공개"
                      control={<RadioButton />}
                      label={<Text types="small">친구 공개</Text>}
                    />
                    <FormControlLabel
                      value="전체공개"
                      control={<RadioButton />}
                      label={<Text types="small">전체 공개</Text>}
                    />
                  </RadioGroup>
                </Stack>
              </div>
            </div>
          </div>
          <div className={styles["diary__button"]} onClick={scope === "비공개" ? handleOpen : handleSave}>
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
        </div>
      </Modal>
    </>
  );
};

export default MyPageDiary;
