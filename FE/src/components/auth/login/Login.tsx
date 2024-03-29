import React, { ChangeEvent, KeyboardEvent, useEffect, useState } from "react";
import styles from "@styles/auth/Login.module.scss";
import Input from "@components/common/Input";
import AuthButton from "@components/auth/AuthButton";
import Text from "@components/common/Text";
import Google from "@assets/Icons/google_icon.svg";
import { NavLink, useNavigate } from "react-router-dom";
import { authApi, userApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { setLogin, selectAutoLogin, setAutoLogin, setIsGoogle, setUserInfo } from "@store/authSlice";

const getCookie = (name: string) => {
  const regExp = new RegExp("(^|;) ?" + name + "=([^;]*)(;|$)");
  const value = regExp.exec(document.cookie);
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
  const [loginInfo, setLoginInfo] = useState({
    email: "",
    password: "",
  });
  const { email, password } = loginInfo;
  const autoLogin = useAppSelector(selectAutoLogin);

  const handleGoogleLogin = () => {
    window.location.href = process.env.REACT_APP_API_URL + "/api/oauth/google";
  };

  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    setLoginInfo({
      ...loginInfo,
      [name]: value,
    });
  };

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleLogin();
    }
  };

  const handleAutoLogin = () => {
    dispatch(setAutoLogin(!autoLogin));
  };

  const handleLogin = () => {
    setShowAlert(false);
    setError({ email: !!email, password: !!password }); // null이면 false
    if (!email || !password) return;

    authApi
      .login({ email: email, password: password })
      .then((res) => {
        setShowAlert(false);
        const accessToken = res.headers["authorization"] as string;
        const userId = res.headers["id"] as number;
        const type = res.headers["type"] as string;
        dispatch(setLogin({ accessToken, userId, type }));

        // 자동로그인 auto-login 코드 저장
        const auto = res.headers["auto-login"] as string;
        if (auto) localStorage.setItem("AL", auto);

        userApi.getProfiles(userId).then((res) => {
          dispatch(setUserInfo(res.data.data));
          navigator("/month");
        });

        // dispatch(setPopupOpen()); 로그인 팝업
      })
      .catch((err) => {
        setShowAlert(true);
        console.error(err);
      });
  };

  useEffect(() => {
    // 소셜 로그인
    const token = getCookie("token");
    const id = getCookie("userId");
    const type = getCookie("type");
    if (token && id && type) {
      dispatch(setLogin({ accessToken: token, userId: parseInt(id), type }));
      dispatch(setIsGoogle(true));
      deleteCookie("token");
      deleteCookie("userId");
      deleteCookie("type");
      userApi
        .getProfiles(parseInt(id))
        .then((res) => {
          dispatch(setUserInfo(res.data.data));
          navigator("/month");
        })
        .catch((err) => console.error(err));
    }
  }, [document.cookie]);

  return (
    <div className={styles.login_display}>
      <div className={styles.login_container}>
        <div className={styles.login_logo}>Login</div>
        <div className={styles.login_input}>
          <Input
            name="email"
            dataCy="emailInput"
            value={email}
            types="default"
            placeholder="이메일"
            onChange={onChange}
            error={!error.email} // false일 시 error
            helperText={!error.email && <p id="helper-text__email">이메일을 입력해주세요.</p>}
            onKeyDown={handleOnKeyPress}
            maxLength={100}
          />
          <Input
            name="password"
            dataCy="passwordInput"
            value={password}
            types="password"
            placeholder="비밀번호"
            onChange={onChange}
            error={!error.password}
            helperText={!error.password && <p id="helper-text__password">비밀번호를 입력해주세요.</p>}
            onKeyDown={handleOnKeyPress}
            maxLength={20}
          />
        </div>
        <div className={styles.login_toolbox}>
          <div className={styles.login_checkbox}>
            <input id="auto" type="checkbox" defaultChecked={autoLogin} onChange={handleAutoLogin} />
            <label htmlFor="auto">
              <Text types="small">자동로그인</Text>
            </label>
          </div>
          {/* <Text types="small">비밀번호 찾기</Text> */}
        </div>
        <div
          className={styles.login_warning}
          style={{ visibility: showAlert ? "visible" : "hidden" }}
          data-testid="error-message"
        >
          <Text types="small">이메일 또는 비밀번호를 잘못 입력했습니다.</Text>
          <Text types="small">입력하신 내용을 다시 확인해주세요</Text>
        </div>
        <AuthButton onClick={handleLogin} data-cy="loginButton">
          Login
        </AuthButton>
        <div className={styles.login_signup}>
          <Text types="small">아직 회원이 아니신가요? </Text>
          <NavLink to="/signup" data-cy="signupButton">
            <Text types="small">Sign Up</Text>
          </NavLink>
        </div>
        <div className={styles.login_divider}>
          <div />
          <Text types="small">or</Text>
        </div>
        <div className={styles.login_social}>
          <button onClick={handleGoogleLogin} data-cy="socialButton">
            <img src={Google} alt="Goolgle Icon" />
            <span>Continue with Google</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default Login;
