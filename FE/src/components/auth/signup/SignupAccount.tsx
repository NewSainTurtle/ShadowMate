import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "@styles/auth/Signup.module.scss";
import AuthButton from "@components/auth/AuthButton";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import Button from "@components/common/Button";
import { ServerErrorResponse, authApi } from "@api/Api";
import { userRegex, removeWhitespaceRegex } from "@util/regex";
import axios from "axios";

const SignupAccount = () => {
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

    if (!value) {
      if (name == "passwordCheck") setError({ ...error, [name]: "비밀번호를 다시 한번 입력해주세요." });
      else setError({ ...error, [name]: "필수 정보입니다." });
      return;
    }

    setError({
      ...error,
      email: name == "email" && !userRegex.email.test(email) ? "이메일 형식이 올바르지 않습니다." : "",
      password:
        name == "password" && !userRegex.password.test(password)
          ? "6~20자의 영문, 숫자, 특수문자(!?^&*@#)를 사용해 주세요."
          : "",
      passwordCheck:
        (name == "password" || name == "passwordCheck") && !!passwordCheck && passwordCheck != password
          ? "비밀번호가 일치하지 않습니다."
          : "",
      nickname:
        name == "nickname" && !userRegex.nickname.test(nickname)
          ? "2~10자의 특수문자를 제외한 문자를 사용해 주세요."
          : "",
    });
  };

  const signupApiModule = (() => {
    const onClickEmail = () => {
      if (!email) {
        setError({ ...error, email: "필수 정보입니다." });
        return;
      }

      if (!error.email) {
        authApi
          .emailAuthentication({ email })
          .then(() => {
            setEmailAuthentication(true);
            setErrorMessage("");
          })
          .catch((err) => {
            if (axios.isAxiosError<ServerErrorResponse>(err) && err.response) {
              const { code, message } = err.response.data;
              setEmailAuthentication(false);
              if (code == "DUPLICATED_EMAIL" || code == "ALREADY_AUTHENTICATED_EMAIL")
                setError({ ...error, email: message });
            } else console.error(err);
          });
      }
    };

    const onClickEmailCheck = () => {
      authApi
        .emailAuthenticationCheck({ email, code })
        .then(() => {
          setEmailRedundancy(true);
          setError({ ...error, code: "" });
          setErrorMessage("");
        })
        .catch(() => {
          setEmailRedundancy(false);
          setError({ ...error, code: "이메일 인증 코드가 일치하지 않습니다." });
        });
    };

    const onClickNickName = () => {
      if (!nickname) {
        setError({ ...error, nickname: "필수 정보입니다." });
        return;
      }

      if (!error.nickname) {
        authApi
          .nickname({ nickname })
          .then(() => {
            setNicknameAuthentication(true);
            setError({ ...error, nickname: "" });
            setErrorMessage("");
          })
          .catch(() => {
            setNicknameAuthentication(false);
            setError({ ...error, nickname: "중복된 닉네임입니다." });
          });
      }
    };

    const onClickJoin = () => {
      if (!(email && password && passwordCheck && nickname)) {
        setError({
          email: !email ? "필수 정보입니다." : "",
          code: !code ? "이메일 인증 코드를 입력해주세요" : "",
          password: !password ? "필수 정보입니다." : "",
          passwordCheck: !passwordCheck ? "비밀번호를 다시 한번 입력해주세요." : "",
          nickname: !nickname ? "필수 정보입니다." : "",
        });
        return;
      }

      if (!isEmailAuthentication) {
        setErrorMessage("메일을 인증을 해주세요.");
        return;
      }

      if (!code) {
        setError({ ...error, code: "메일 인증 코드를 입력해주세요" });
        return;
      }

      if (!isEmailRedundancy) {
        setErrorMessage("인증코드를 확인 해주세요.");
        return;
      }

      if (!isNicknameAuthentication) {
        setErrorMessage("닉네임 중복을 확인 해주세요.");
        return;
      }

      authApi.join({ email, password, nickname });
      navigator("/login");
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
    <>
      <div className={styles["signup-account__form"]}>
        <div className={styles["signup-account__input"]}>
          <Input
            name="email"
            dataCy="emailInput"
            placeholder="이메일"
            value={email}
            onChange={handleInput}
            disabled={isEmailRedundancy}
            onBlur={checkError}
            error={!!error.email}
            helperText={error.email || " "}
            maxLength={100}
          />
          <Button data-cy="emailButton" types="gray" onClick={onClickEmail} disabled={isEmailRedundancy}>
            {!isEmailAuthentication ? "메일인증" : "재전송"}
          </Button>
        </div>
        {isEmailAuthentication && (
          <div className={styles["signup-account__input"]}>
            <Input
              name="code"
              dataCy="emailCodeInput"
              placeholder="이메일 인증 코드"
              value={code}
              onChange={handleInput}
              disabled={isEmailRedundancy}
              onBlur={checkError}
              error={!!error.code}
              helperText={error.code || " "}
              maxLength={6}
            />
            <Button data-cy="emailCodeButton" types="gray" onClick={onClickEmailCheck} disabled={isEmailRedundancy}>
              {!isEmailRedundancy ? "인증확인" : "인증완료"}
            </Button>
          </div>
        )}
        <Input
          name="password"
          dataCy="passwordInput"
          placeholder="비밀번호(6~20자)"
          types="password"
          value={password}
          onChange={handleInput}
          onBlur={checkError}
          error={!!error.password}
          helperText={error.password || " "}
          maxLength={20}
        />
        <Input
          name="passwordCheck"
          dataCy="passwordCheckInput"
          placeholder="비밀번호 확인"
          types="password"
          value={passwordCheck}
          onChange={handleInput}
          onBlur={checkError}
          error={!!error.passwordCheck}
          helperText={error.passwordCheck || " "}
          maxLength={20}
        />
        <div className={styles["signup-account__input"]}>
          <Input
            name="nickname"
            dataCy="nicknameInput"
            placeholder="닉네임(특수문자 제외, 2~10자)"
            value={nickname}
            onChange={handleInput}
            onBlur={checkError}
            error={!!error.nickname}
            helperText={error.nickname || " "}
            maxLength={10}
          />
          <Button data-cy="nicknameButton" types="gray" onClick={onClickNickName} disabled={isNicknameAuthentication}>
            {!isNicknameAuthentication ? "중복확인" : "검사완료"}
          </Button>
        </div>
      </div>

      <div className={styles["signup-account__warning"]}>
        <Text types="small">{errorMessage}</Text>
      </div>

      <div data-cy="signupButton" className={styles["signup-account__button"]} onClick={onClickJoin}>
        <AuthButton>Sign up</AuthButton>
      </div>
    </>
  );
};

export default SignupAccount;
