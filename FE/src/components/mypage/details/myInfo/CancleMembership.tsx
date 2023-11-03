import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import { selectUserId } from "@store/authSlice";
import { useAppSelector } from "@hooks/hook";
import { userApi } from "@api/Api";

const CancleMembership = () => {
  const userId = useAppSelector(selectUserId);
  const [Modalopen, setModalOpen] = useState(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  const deleteProfile = () => {
    userApi
      .userOut(userId)
      .then((res) => console.log("회원탈퇴 성공", res))
      .catch((err) => console.error("회원탈퇴 실패", err));
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
          <Text types="small">계정 탈퇴는 취소할 수 없습니다</Text>
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
