import React, { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import styles from "@styles/auth/Signup.module.scss";
import AuthButton from "@components/auth/AuthButton";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import Button from "@components/common/Button";
import { authApi } from "@api/Api";
import { userRegex, removeWhitespaceRegex } from "@util/regex";

const Signup = () => {
  const navigator = useNavigate();
  const [userData, setUserData] = useState({
    email: "",
    code: "",
    password: "",
    passwordCheck: "",
    nickname: "",
  });
  const [error, setError] = useState({
    email: "",
    code: "",
    password: "",
    passwordCheck: "",
    nickname: "",
  });
  const [isEmailAuthentication, setEmailAuthentication] = useState(false);
  const [isEmailRedundancy, setEmailRedundancy] = useState(false);
  const [isNicknameAuthentication, setNicknameAuthentication] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const { email, code, password, passwordCheck, nickname } = userData;

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    if (name == "nickname" && isNicknameAuthentication) {
      authApi.deleteNickname({ nickname }).catch((err) => console.error(err));
      setNicknameAuthentication(false);
    }
    setUserData({
      ...userData,
      [name]: value.replace(removeWhitespaceRegex, ""),
    });
  };

  const checkError = (e: React.FocusEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    if (name == "passwordCheck" && !value) setError({ ...error, [name]: "비밀번호를 다시 한번 입력해주세요." });
    else if (!value) setError({ ...error, [name]: "필수 정보입니다." });
    else if (name == "email" && !userRegex.email.test(email))
      setError({ ...error, [name]: "이메일 형식이 올바르지 않습니다." });
    else if (name == "password" && !userRegex.password.test(password))
      setError({ ...error, [name]: "비밀번호는 6~20자로 설정해 주세요." });
    else if (name == "passwordCheck" && passwordCheck != password)
      setError({ ...error, [name]: "비밀번호가 일치하지 않습니다." });
    else if (name == "nickname" && !userRegex.nickname.test(nickname))
      setError({ ...error, [name]: "닉네임은 특수문자 제외한 2~10자로 설정해 주세요." });
    else setError({ ...error, [name]: "" });
  };

  const signupApiModule = (() => {
    const onClickEmail = () => {
      if (!email) setError({ ...error, email: "필수 정보입니다." });
      else if (!error.email) {
        authApi
          .emailAuthentication({ email })
          .then(() => setEmailAuthentication(true))
          .catch((err) => {
            const { code, message } = err.response.data;
            setEmailAuthentication(false);
            if (code == "DUPLICATED_EMAIL" || code == "ALREADY_AUTHENTICATED_EMAIL")
              setError({ ...error, email: message });
            else console.error(err);
          });
      }
    };
    const onClickEmailCheck = () => {
      authApi
        .emailAuthenticationCheck({ email, code })
        .then(() => {
          setEmailRedundancy(true);
          setError({ ...error, code: "" });
        })
        .catch(() => {
          setEmailRedundancy(false);
          setError({ ...error, code: "이메일 인증 코드가 일치하지 않습니다." });
        });
    };
    const onClickNickName = () => {
      if (!nickname) setError({ ...error, nickname: "필수 정보입니다." });
      else if (!error.nickname) {
        authApi
          .nickname({ nickname })
          .then(() => {
            setNicknameAuthentication(true);
            setError({ ...error, nickname: "" });
          })
          .catch(() => {
            setNicknameAuthentication(false);
            setError({ ...error, nickname: "중복된 닉네임입니다." });
          });
      }
    };

    const onClickJoin = () => {
      if (!email || !code || !password || !passwordCheck || !nickname) {
        setError({
          email: !email ? "필수 정보입니다." : "",
          code: !code ? "이메일 인증 코드를 입력해주세요" : "",
          password: !password ? "필수 정보입니다." : "",
          passwordCheck: !passwordCheck ? "비밀번호를 다시 한번 입력해주세요." : "",
          nickname: !nickname ? "필수 정보입니다." : "",
        });
      } else if (!isEmailAuthentication) setErrorMessage("메일을 인증을 해주세요.");
      else if (!isEmailRedundancy) setErrorMessage("인증코드를 확인 해주세요.");
      else if (!isNicknameAuthentication) setErrorMessage("닉네임을 인증 해주세요.");
      else {
        authApi.join({ email, password, nickname });
        navigator("/login");
      }
    };

    return {
      onClickEmail,
      onClickEmailCheck,
      onClickNickName,
      onClickJoin,
    };
  })();
  const { onClickEmail, onClickEmailCheck, onClickNickName, onClickJoin } = signupApiModule;

  return (
    <div className={styles["signup_display"]}>
      <div className={styles["signup_container"]}>
        <div className={styles["signup_logo"]}>Sign up</div>

        <div className={styles["signup_box_1"]}>
          <div className={styles["input_box__button"]}>
            <Input
              placeholder="이메일"
              name="email"
              value={email}
              onChange={handleInput}
              disabled={isEmailRedundancy}
              onBlur={checkError}
              error={!!error.email}
              helperText={error.email || " "}
            />
            <Button types="gray" onClick={onClickEmail} disabled={isEmailRedundancy}>
              {!isEmailAuthentication ? "메일인증" : "재전송"}
            </Button>
          </div>
          {isEmailAuthentication && (
            <div className={styles["input_box__button"]}>
              <Input
                placeholder="이메일 인증 코드"
                name="code"
                value={code}
                onChange={handleInput}
                disabled={isEmailRedundancy}
                error={!!error.code}
                helperText={error.code || " "}
              />
              <Button types="gray" onClick={onClickEmailCheck} disabled={isEmailRedundancy}>
                {!isEmailRedundancy ? "인증확인" : "인증완료"}
              </Button>
            </div>
          )}
          <Input
            placeholder="비밀번호(6~20자)"
            types="password"
            name="password"
            value={password}
            onChange={handleInput}
            onBlur={checkError}
            error={!!error.password}
            helperText={error.password || " "}
          />
          <Input
            placeholder="비밀번호 확인"
            types="password"
            name="passwordCheck"
            value={passwordCheck}
            onChange={handleInput}
            onBlur={checkError}
            error={!!error.passwordCheck}
            helperText={error.passwordCheck || " "}
          />
          <div className={styles["input_box__button"]}>
            <Input
              placeholder="닉네임(특수문자 제외, 2~10자)"
              name="nickname"
              value={nickname}
              onChange={handleInput}
              onBlur={checkError}
              error={!!error.nickname}
              helperText={error.nickname || " "}
            />
            <Button types="gray" onClick={onClickNickName} disabled={isNicknameAuthentication}>
              {!isNicknameAuthentication ? "중복확인" : "검사완료"}
            </Button>
          </div>
        </div>

        <div className={styles["signup_box_2"]}>
          <div className={styles["signup_warning"]}>
            <Text types="small">{errorMessage}</Text>
          </div>
          <div onClick={onClickJoin}>
            <AuthButton>Sign up</AuthButton>
          </div>
          <div className={styles["signup_context"]}>
            <Text types="small">이미 회원이신가요?</Text>
            <NavLink to={"/login"}>
              <Text types="small">Login</Text>
            </NavLink>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Signup;
