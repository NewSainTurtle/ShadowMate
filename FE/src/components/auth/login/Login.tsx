import React, { useState } from "react";
import styles from "@styles/auth/Login.module.scss";
import InputStyles from "@components/common/Input";
import AuthButton from "../AuthButton";
import Text from "@components/common/Text";

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
              <Text>자동로그인</Text>
            </label>
          </div>
          <Text>비밀번호 찾기</Text>
        </div>
        <div className={styles.login_warning} style={{ visibility: showAlert ? "visible" : "hidden" }}>
          <Text types="small">아이디 또는 비밀번호를 잘못 입력했습니다.</Text>
          <Text types="small">입력하신 내용을 다시 확인해주세요</Text>
        </div>
        <AuthButton>Login</AuthButton>
        <div className={styles.login_signup}>
          <Text types="small">아직 회원이 아니신가요? </Text>
          <Text types="small">Sign Up</Text>
        </div>
        <div className={styles.login_divider}>
          <div />
          <Text types="small">or</Text>
        </div>

        <div className={styles.login_social}>
          <button>Continue with Google</button>
        </div>
      </div>
    </div>
  );
};

export default Login;
