import React, { ChangeEvent, useState } from "react";
import styles from "@styles/auth/Login.module.scss";
import Input from "@components/common/Input";
import AuthButton from "../AuthButton";
import Text from "@components/common/Text";
import Google from "@assets/Icons/google_icon.svg";
import { NavLink, useNavigate } from "react-router-dom";
import { authApi } from "@api/Api";
import { useAppDispatch } from "@hooks/hook";
import { setLogin } from "@store/authSlice";

const Login = () => {
  const [showAlert, setShowAlert] = useState(false);
  const [loginInfo, setLoginInfo] = useState({
    email: "",
    password: "",
  });
  const { email, password } = loginInfo;
  const emailCondition = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
  const pwCondition = /^[a-zA-Z\\d`~!@#$%^&*()-_=+]{6,20}$/;
  const dispatch = useAppDispatch();
  const navigator = useNavigate();

  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    setLoginInfo({
      ...loginInfo,
      [name]: value,
    });
  };

  const handleLogin = async () => {
    if (!emailCondition.test(email)) {
      alert("이메일 형식에 맞지 않습니다.");
      return;
    }
    if (!pwCondition.test(password)) {
      alert("비밀번호 형식에 맞지 않습니다.");
      return;
    }
    authApi
      .login({ email: email, password: password })
      .then((res) => {
        const accessToken = res.headers["authorization"];
        dispatch(setLogin({ accessToken: accessToken, userId: 0 }));
        navigator("/month");
      })
      .catch((err) => console.log(err));
  };

  return (
    <div className={styles.login_display}>
      <div className={styles.login_container}>
        <div className={styles.login_logo}>Login</div>
        <div className={styles.login_input}>
          <Input name="email" value={email} types="default" placeholder="이메일" onChange={onChange} />
          <Input name="password" value={password} types="password" placeholder="비밀번호" onChange={onChange} />
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
        <AuthButton onClick={handleLogin}>Login</AuthButton>
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
