import React, { useState } from "react";
import styles from "@styles/auth/Login.module.scss";
import InputStyles from "@components/common/Input";
import AuthButton from "../AuthButton";
import Text from "@components/common/Text";
import Google from "@assets/Icons/google_icon.svg";
import { NavLink } from "react-router-dom";

const Login = () => {
  const [showAlert, setShowAlert] = useState(false);
  return (
    <div className={styles.login_display}>
      <div className={styles.login_container}>
        <div className={styles.login_logo}>Login</div>
        <div className={styles.login_input}>
          <InputStyles name="id" types="default" placeholder="이메일" />
          <InputStyles name="password" types="password" placeholder="비밀번호" />
        </div>
        <div className={styles.login_toolbox}>
          <div className={styles.login_checkbox}>
            <input id="auto" type="checkbox" />
            <label htmlFor="auto">
              <Text types="small">자동로그인</Text>
            </label>
          </div>
          <Text types="small">비밀번호 찾기</Text>
        </div>
        <div className={styles.login_warning} style={{ visibility: showAlert ? "visible" : "hidden" }}>
          <Text types="small">아이디 또는 비밀번호를 잘못 입력했습니다.</Text>
          <Text types="small">입력하신 내용을 다시 확인해주세요</Text>
        </div>
        <AuthButton>Login</AuthButton>
        <div className={styles.login_signup}>
          <Text types="small">아직 회원이 아니신가요? </Text>
          <NavLink to="/signup">
            <Text types="small">Sign Up</Text>
          </NavLink>
        </div>
        <div className={styles.login_divider}>
          <div />
          <Text types="small">or</Text>
        </div>
        <div className={styles.login_social}>
          <button>
            <img src={Google} alt="Goolgle Icon" />
            <span>Continue with Google</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default Login;
