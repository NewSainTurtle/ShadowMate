import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import styles from "@styles/auth/Signup.module.scss";
import AuthButton from "@components/auth/AuthButton";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import Button from "@components/common/Button";
import { authApi } from "@api/Api";
import { userRegex, removeWhitespaceRegex } from "@util/regex";

const Signup = () => {
  const [userData, setUserData] = useState({
    email: "",
    code: "",
    password: "",
    passwordCheck: "",
    nickname: "",
  });
  const [isCheck, setCheck] = useState({
    email: false, // 이메일 형식, 이메일 인증 요청 여부
    emailCheck: false, // 이메일 인증 확인 여부
    passwordSame: false, // 패스워드 형식, 일치 여부
    nicknameCheck: false, // 닉네임 형식, 중복 여부
  });
  const [errorMessage, setErrorMessage] = useState("");
  const { email, code, password, passwordCheck, nickname } = userData;

  const EmailRe = () => {
    console.log(userRegex.email.test(email));
  };

  const signupApiModule = (() => {
    const onClickEmail = () =>
      userRegex.email.test(email) ? authApi.emailAuthentication({ email }) : setErrorMessage("이메일 형식이 아닙니다");
    const onClickEmailCheck = () => authApi.emailAuthenticationCheck({ email, code });
    const onClickNickName = () => authApi.nickname({ nickname });
    const onClickJoin = () => authApi.join({ email, password, nickname });

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

  // const isEmail = userRegex.email.test(email) ? setErrorMessage("이메일 형식이 아닙니다") : "";
  const isEmailAuthentication = false && setErrorMessage("이메일 인증을 해주세요");
  const isEmailRedundancy = false && setErrorMessage("중복된 이메일 입니다");
  const isNickname = userRegex.nickname.test(nickname) && setErrorMessage("닉네임은 2 ~ 10글자");
  const isNickanmeAuthentication = false && setErrorMessage("닉네임을 인증해주세요");
  const isNickNameRedundancy = false && setErrorMessage("중복된 닉네임 입니다");
  const passwordRex = userRegex.password.test(password) && setErrorMessage("비밀번호는 6 ~ 20글자 가능합니다.");
  // const isPasswordSame = password === passwordCheck && setErrorMessage("비밀번호가 일치하지 않습니다.");
  const isError =
    isEmailAuthentication &&
    isEmailRedundancy &&
    isNickname &&
    isNickanmeAuthentication &&
    isNickNameRedundancy &&
    passwordRex;

  return (
    <div className={styles["signup_display"]}>
      <div className={styles["signup_container"]}>
        <div className={styles["signup_logo"]}>Sign up</div>

        <div className={styles["signup_box_1"]}>
          <div className={styles["input_box__button"]}>
            <Input placeholder="이메일" name="email" value={email} onChange={handleInput} />
            <Button types="gray" onClick={onClickEmail}>
              인증
            </Button>
          </div>
          <div className={styles["input_box__button"]}>
            <Input placeholder="이메일 인증 코드" name="emailCode" value={code} onChange={handleInput} />
            <Button types="gray" onClick={onClickEmailCheck}>
              확인
            </Button>
          </div>
          <Input placeholder="비밀번호" types="password" name="password" value={password} onChange={handleInput} />
          <Input
            placeholder="비밀번호 확인"
            types="password"
            name="passwordCheck"
            value={passwordCheck}
            onChange={handleInput}
          />
          <div className={styles["input_box__button"]}>
            <Input placeholder="닉네임" name="nickname" value={nickname} onChange={handleInput} />
            <Button types="gray" onClick={onClickNickName}>
              중복검사
            </Button>
          </div>
        </div>

        <div className={styles["signup_box_2"]}>
          <div className={styles["signup_warning"]} style={{ visibility: "ㅇ" ? "visible" : "hidden" }}>
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
