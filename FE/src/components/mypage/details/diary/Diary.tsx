import React, { ChangeEvent, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { FormControlLabel, RadioGroup, Stack } from "@mui/material";
import RadioButton from "@components/common/RadioButton";
import SaveIcon from "@mui/icons-material/Save";
import Modal from "@components/common/Modal";
import DiaryConfirmModal from "@components/common/Modal/DiaryConfirmModal";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectPlannerAccessScope, selectUserId, setUserInfo } from "@store/authSlice";
import { MonthConfig, setPlannerAccessScope } from "@store/planner/monthSlice";
import { settingApi, userApi } from "@api/Api";

const MyPageDiary = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const accessScope = useAppSelector(selectPlannerAccessScope);
  const [scope, setScope] = useState<MonthConfig["plannerAccessScope"]>(accessScope);
  const [Modalopen, setModalOpen] = useState(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  const handleCheckScope = () => {
    if (accessScope === "전체공개") {
      if (scope === "비공개" || scope === "친구공개") handleOpen();
      else handleSave();
    } else handleSave();
  };

  const handleSave = () => {
    settingApi
      .accessScopes(userId, { plannerAccessScope: scope })
      .then(() => {
        dispatch(setPlannerAccessScope(scope));
        handleClose();
        userApi
          .getProfiles(userId)
          .then((res) => dispatch(setUserInfo(res.data.data)))
          .catch((err) => console.error(err));
      })
      .catch((err) => console.error(err));
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
          <div
            className={styles["diary__button"]}
            // onClick={scope === "비공개" || scope === "친구공개" ? handleOpen : handleSave}
            onClick={handleCheckScope}
          >
            <div className={styles["diary__button--save"]}>
              <SaveIcon />
              <Text>저장</Text>
            </div>
          </div>
        </div>
      </div>
      <Modal types="twoBtn" open={Modalopen} onClose={handleClose} onClick={handleSave}>
        <DiaryConfirmModal />
      </Modal>
    </>
  );
};

export default MyPageDiary;
