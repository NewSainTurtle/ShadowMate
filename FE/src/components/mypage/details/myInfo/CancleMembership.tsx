import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import { selectUserId, setLogout } from "@store/authSlice";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { userApi } from "@api/Api";

const CancleMembership = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const [Modalopen, setModalOpen] = useState(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  const deleteProfile = () => {
    userApi
      .userOut(userId)
      .then(() => {
        handleClose();
        dispatch(setLogout());
      })
      .catch((err) => console.error(err));
  };

  return (
    <div className={styles["info__cantainer"]}>
      <div className={styles["info__contents"]}>
        <div className={styles["info__cancleMembership"]} onClick={handleOpen}>
          <Text>회원 탈퇴</Text>
        </div>
      </div>

      <Modal open={Modalopen} onClose={handleClose}>
        <div className={styles["info__modal"]}>
          <WarningAmberRoundedIcon />
          <Text types="small">
            <>정말 탈퇴하시겠습니까?</>
            <br />
            <></>
          </Text>
          <Text types="small">탈퇴된 계정은 다시 로그인 할 수 없습니다.</Text>
          <div>
            <div onClick={handleClose}>
              <Text types="small">취소</Text>
            </div>
            <div onClick={deleteProfile}>
              <Text types="small">탈퇴</Text>
            </div>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default CancleMembership;
