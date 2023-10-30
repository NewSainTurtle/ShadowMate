import React, { ChangeEvent, useEffect, useState } from "react";
import styles from "@styles/auth/Login.module.scss";
import Input from "@components/common/Input";
import AuthButton from "../AuthButton";
import Text from "@components/common/Text";
import Google from "@assets/Icons/google_icon.svg";
import { NavLink, useNavigate } from "react-router-dom";
import { authApi } from "@api/Api";
import { useAppDispatch } from "@hooks/hook";
import { setLogin } from "@store/authSlice";

const getCookie = (name: string) => {
  var value = document.cookie.match("(^|;) ?" + name + "=([^;]*)(;|$)");
  return value ? value[2] : null;
};

const deleteCookie = (name: string) => {
  document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
};

const Login = () => {
  const dispatch = useAppDispatch();
  const navigator = useNavigate();
  const [showAlert, setShowAlert] = useState(false);
  const [error, setError] = useState({
    email: true,
    password: true,
  });
  const [autoLogin, setAutoLogin] = useState<boolean>(false);
  const [loginInfo, setLoginInfo] = useState({
    email: "",
    password: "",
  });
  const { email, password } = loginInfo;

  const handleGoogleLogin = () => {
    window.location.href = "https://shadowmate.kro.kr/api/oauth/google";
  };

  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    setLoginInfo({
      ...loginInfo,
      [name]: value,
    });
  };

  const handleLogin = () => {
    setShowAlert(false);
    setError({ email: !!email, password: !!password }); // null이면 false
    if (!email || !password) return;

    authApi
      .login({ email: email, password: password })
      .then((res) => {
        setShowAlert(false);
        const accessToken = res.headers["authorization"];
        const userId = res.headers["id"];
        dispatch(setLogin({ accessToken: accessToken, userId: userId }));
        if (autoLogin) {
          localStorage.clear();
          localStorage.setItem("accessToken", accessToken);
          localStorage.setItem("id", userId);
        }
        navigator("/month");
      })
      .catch((err) => {
        setShowAlert(true);
        console.log(err);
      });
  };

  useEffect(() => {
    const token = getCookie("token");
    const id = getCookie("userId");
    if (token && id) {
      dispatch(setLogin({ accessToken: token, userId: parseInt(id) }));
      deleteCookie("token");
      deleteCookie("userId");
      navigator("/month");
    }
  }, [document.cookie]);

  return (
    <div className={styles.login_display}>
      <div className={styles.login_container}>
        <div className={styles.login_logo}>Login</div>
        <div className={styles.login_input}>
          <Input
            name="email"
            value={email}
            types="default"
            placeholder="이메일"
            onChange={onChange}
            error={!error.email} // false일 시 error
            helperText={!error.email && "이메일을 입력해주세요."}
          />
          <Input
            name="password"
            value={password}
            types="password"
            placeholder="비밀번호"
            onChange={onChange}
            error={!error.password}
            helperText={!error.password && "비밀번호를 입력해주세요."}
          />
        </div>
        <div className={styles.login_toolbox}>
          <div className={styles.login_checkbox}>
            <input id="auto" type="checkbox" defaultChecked={autoLogin} onChange={() => setAutoLogin(!autoLogin)} />
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
          <button onClick={handleGoogleLogin}>
            <img src={Google} alt="Goolgle Icon" />
            <span>Continue with Google</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default Login;
