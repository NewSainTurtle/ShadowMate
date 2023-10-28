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
  const [isEmailAuthentication, setEmailAuthentication] = useState(false);
  const [isEmailRedundancy, setEmailRedundancy] = useState(false);
  const [isNickanmeAuthentication, setNickanmeAuthentication] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const { email, code, password, passwordCheck, nickname } = userData;

  const signupApiModule = (() => {
    const onClickEmail = () => {
      if (userRegex.email.test(email))
        authApi
          .emailAuthentication({ email })
          .then(() => {
            setEmailAuthentication(true);
            setEmailRedundancy(false);
            setErrorMessage("");
          })
          .catch((err) => {
            setEmailAuthentication(false);
            setErrorMessage(err.response.data.message);
          });
      else setErrorMessage("이메일 형식이 아닙니다");
    };
    const onClickEmailCheck = () =>
      authApi
        .emailAuthenticationCheck({ email, code })
        .then(() => {
          setEmailRedundancy(true);
          setErrorMessage("");
        })
        .catch(() => {
          setEmailRedundancy(false);
          setErrorMessage("이메일 인증 코드가 일치하지 않습니다");
        });
    const onClickNickName = () => {
      if (userRegex.nickname.test(nickname)) {
        setNickanmeAuthentication(true);
        setErrorMessage("");
        //백 API 수정 필요
        // authApi
        //   .nickname({ nickname })
        //   .then(() => {
        //     setNickanmeAuthentication(true);
        //     setErrorMessage("");
        //   })
        //   .catch((err) => {
        //     setNickanmeAuthentication(false);
        //     setErrorMessage("중복된 닉네임 입니다");
        //   });
      } else {
        setNickanmeAuthentication(false);
        setErrorMessage("닉네임은 2 ~ 10글자이내로 입력해주세요");
      }
    };
    const onClickJoin = () => {
      if (userRegex.password.test(password)) setErrorMessage("비밀번호는 6 ~ 20이내로 입력해주세요");
      else if (password != passwordCheck) setErrorMessage("비밀번호가 일치하지 않습니다.");
      else if (!isEmailRedundancy) setErrorMessage("이메일을 인증 해주세요");
      else if (!isNickanmeAuthentication) setErrorMessage("닉네임을 인증 해주세요");
      else if (!!email && !!password && !!nickname) {
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

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserData({
      ...userData,
      [e.target.name]: e.target.value.replace(removeWhitespaceRegex, ""),
    });
  };

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
            />
            <Button types="gray" onClick={onClickEmail} disabled={isEmailRedundancy}>
              {!isEmailAuthentication ? "인증" : "재전송"}
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
              />
              <Button types="gray" onClick={onClickEmailCheck} disabled={isEmailRedundancy}>
                {!isEmailRedundancy ? "인증확인" : "인증완료"}
              </Button>
            </div>
          )}
          <Input placeholder="비밀번호" types="password" name="password" value={password} onChange={handleInput} />
          <Input
            placeholder="비밀번호 확인"
            types="password"
            name="passwordCheck"
            value={passwordCheck}
            onChange={handleInput}
          />
          <div className={styles["input_box__button"]}>
            <Input
              placeholder="닉네임"
              name="nickname"
              value={nickname}
              onChange={handleInput}
              disabled={isNickanmeAuthentication}
            />
            <Button types="gray" onClick={onClickNickName} disabled={isNickanmeAuthentication}>
              {!isNickanmeAuthentication ? "중복검사" : "검사완료"}
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
        </div>

        <div className={styles["signup_context"]}>
          <Text types="small">이미 회원이신가요?</Text>
          <NavLink to={"/login"}>
            <Text types="small">Login</Text>
          </NavLink>
        </div>
      </div>
    </div>
  );
};

export default Signup;
