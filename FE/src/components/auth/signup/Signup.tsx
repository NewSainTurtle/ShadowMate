import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import styles from "@styles/auth/Signup.module.scss";
import AuthButton from "@components/auth/AuthButton";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import Button from "@components/common/Button";
import { authApi } from "@api/Api";

const Signup = () => {
  const [userData, setUserData] = useState({
    email: "",
    password: "",
    passwordCheck: "",
    name: "",
  });

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserData({
      ...userData,
      [e.target.name]: e.target.value,
    });
  };

  const onClickJoin = () => {
    let data = {
      email: "test1234@naver.com",
      password: "test1234",
      nickname: "회원이다",
    };
    authApi.join(data);
  };

  const { email, password, passwordCheck, name } = userData;
  const isSame = password === passwordCheck;

  return (
    <div className={styles["signup_display"]}>
      <div className={styles["signup_container"]}>
        <div className={styles["signup_logo"]}>Sign up</div>

        <div className={styles["signup_box_1"]}>
          <div className={styles["input_box__button"]}>
            <Input placeholder="이메일" name="email" value={email} onChange={handleInput} helperText={"안됨"} />
            <Button types="gray">인증</Button>
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
            <Input placeholder="닉네임" name="name" value={name} onChange={handleInput} />
            <Button types="gray">중복검사</Button>
          </div>
        </div>

        <div className={styles["signup_box_2"]}>
          <div className={styles["signup_warning"]} style={{ visibility: !isSame ? "visible" : "hidden" }}>
            <Text types="small">{"비밀번호가 다릅니다."}</Text>
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
