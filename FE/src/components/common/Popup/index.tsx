import React, { useLayoutEffect } from "react";
import styles from "@styles/common/Popup.module.scss";
import Modal from "@mui/material/Modal";
import Text from "@components/common/Text";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectPopupVisible, setPopupClose, setPopupOpen } from "@store/modalSlice";

const Popup = () => {
  const visible = useAppSelector(selectPopupVisible);
  if (!visible) return null;

  const dispatch = useAppDispatch();
  const VISITED_BEFORE_DATE = localStorage.getItem("popup_visible"); // 이전방문 날짜
  const VISITED_NOW_DATE = String(new Date().getDate());
  const onOpen = () => dispatch(setPopupOpen());
  const onClose = () => dispatch(setPopupClose());

  useLayoutEffect(() => {
    if (VISITED_BEFORE_DATE == null) return;
    if (VISITED_BEFORE_DATE == VISITED_NOW_DATE) {
      localStorage.removeItem("popup_visible");
      onOpen();
    } else onClose();
  }, [VISITED_BEFORE_DATE]);

  const Dayclose = () => {
    onClose();
    const expiryDate = new Date().getDate() + 1;
    localStorage.setItem("popup_visible", String(expiryDate));
  };

  return (
    <Modal open={visible}>
      <div className={styles["popup"]}>
        <div className={styles["popup__button-close"]}>
          <span onClick={Dayclose}>오늘 하루 보지 않기</span>
          <span onClick={onClose}>닫기</span>
        </div>
        <div className={styles["popup__container"]}>
          <div className={styles["popup__title-wrapper"]}>
            <Text types="medium" bold>
              ShadowMate
              <br />
              서비스 만족도 조사
            </Text>
          </div>
          <div className={styles["popup__main-wrapper"]}>
            <div className={styles["popup__commentary"]}>
              <Text types="small">
                안녕하십니까, ShadowMate 홈페이지 이용자 여러분의 귀한 의견을 듣고자 설문조사를 실시합니다.
              </Text>
              <br />
              <br />
              <Text types="small">
                여러분께 보내주신 의견은 향후 홈페이지의 운영 및 개선을 위한 자료로 활용될 예정입니다. 이용자 여러분의
                많은 참여 부탁드립니다.
              </Text>
            </div>
            <div className={styles["popup__content-wrapper"]}>
              <div className={styles["content-title"]}>
                <div>대 상</div>
              </div>
              <Text types="small">ShadowMate 홈페이지 서비스 이용자 누구나</Text>
              <div className={styles["content-title"]}>
                <div>기 간</div>
              </div>
              <Text types="small">2023.12.06(수) ~ 2023.12.31(일)</Text>
              <div className={styles["content-title"]}>
                <div>내 용</div>
              </div>
              <Text types="small">UI/UX 만족도 및 개선점, 서비스 문의</Text>
              <div className={styles["content-title"]}>
                <div>참여방법</div>
              </div>
              <Text types="small">
                방법1. 팝업창 하단 설문참여하기 클릭
                <br />
                방법2. 좌측 상단 로고 아래 "설문" 버튼을 클릭
              </Text>
            </div>

            <div
              className={styles["popup__button-questionnaire"]}
              onClick={() => window.open("https://forms.gle/UAztt4dsGeqKWkHy9")}
            >
              <span>설문 참여하기</span>
            </div>
          </div>
        </div>
      </div>
    </Modal>
  );
};

export default Popup;
