import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import { selectType, selectUserId, setLogout } from "@store/authSlice";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { authApi, userApi } from "@api/Api";
import { persistor } from "@hooks/configStore";

const CancelMembership = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const type = useAppSelector(selectType);
  const [Modalopen, setModalOpen] = useState(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  const deleteProfile = () => {
    const autoLoginKey = localStorage.getItem("AL");
    const headers = {
      "Auto-Login": autoLoginKey ?? "",
    };
    Promise.all([userApi.userOut(userId), authApi.logout({ userId, type }, headers)])
      .then(() => {
        handleClose();
        dispatch(setLogout());
      })
      .then(() => {
        persistor.purge();
        localStorage.removeItem("AL");
        localStorage.removeItem("popup_visible");
        localStorage.removeItem("theme");
      })
      .catch((err) => console.error(err));
  };

  return (
    <div className={styles["info__cantainer"]}>
      <div className={styles["info__contents"]}>
        <div className={styles["info__line"]}>
          <div>
            <Text>회원탈퇴 안내</Text>
          </div>
          <div className={styles["info__cancelMembership"]}>
            <Text bold>회원탈퇴 전 유의사항을 확인해 주세요.</Text>
            <p>
              <Text types="small">• 회원탈퇴 시, 해당 계정으로 다시 로그인하실 수 없습니다.</Text>
              <br />
              <Text types="small">• 서비스에 저장된 회원 정보는 모두 삭제되며, 다시 복구 할 수 없습니다.</Text>
              <br />
              <Text types="small">
                • 회원탈퇴를 하시면 추후 재가입시 동일 아이디(이메일 주소)로 재가입이 불가능합니다.
              </Text>
            </p>

            <div onClick={handleOpen}>
              <Text types="small">회원탈퇴</Text>
            </div>
          </div>
        </div>
      </div>

      <Modal types="twoBtn" open={Modalopen} onClose={handleClose} onClick={deleteProfile}>
        <div className={styles["info__modal"]}>
          <WarningAmberRoundedIcon />
          <Text types="small">
            <>정말 탈퇴하시겠습니까?</>
            <br />
            <></>
          </Text>
          <Text types="small">해당 유의사항을 모두 확인하였으며 회원 탈퇴에 동의합니다</Text>
        </div>
      </Modal>
    </div>
  );
};

export default CancelMembership;
