import React from "react";
import styles from "@styles/NotFound.module.scss";
import Text from "@components/common/Text";
import AuthButton from "@components/auth/AuthButton";
import logoLight from "@assets/Imgs/logo_light.svg";
import logoDark from "@assets/Imgs/logo_dark.svg";
import { useNavigate } from "react-router-dom";

const NotFoundPage = () => {
  const navigator = useNavigate();
  const theme = localStorage.getItem("theme");
  const handleMove = () => {
    navigator("/");
  };

  return (
    <div className={styles["container"]}>
      <span className={styles["title"]}>
        SHADOW
        <br />
        MATE
      </span>
      <div className={styles["contents"]}>
        <div className={styles["logo"]}>
          <img src={theme === "dark" ? logoDark : logoLight} alt="logo" />
        </div>
        <span>page not found !</span>
        <div>
          <Text>
            존재하지 않는 주소이거나 <br /> 요청하신 페이지가 변경, 삭제되어 찾을 수 없습니다.
          </Text>
        </div>
      </div>
      <AuthButton onClick={handleMove}>홈으로 돌아가기</AuthButton>
    </div>
  );
};

export default NotFoundPage;
