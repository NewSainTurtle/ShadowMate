import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import styles from "@styles/auth/SignupPage.module.scss";
import AuthButton from "@components/auth/AuthButton";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import Button from "@components/common/Button";
import AuthHeader from "@components/auth/AuthHeader";

const SignupPage = () => {
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

  const { email, password, passwordCheck, name } = userData;
  const isSame = password === passwordCheck;

  return (
    <>
      <AuthHeader />
      <div className={styles.container}>
        <Text types="large">Sign up</Text>
        <div className={styles.box_1}>
          <div className={styles.input__button}>
            <Input placeholder="이메일" name="email" value={email} onChange={handleInput} />
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
          <div className={styles.input__button}>
            <Input placeholder="닉네임" name="name" value={name} onChange={handleInput} />
            <Button types="gray">중복검사</Button>
          </div>
          <div className={styles.box_2}>
            <Text types="small">{!isSame && "비밀번호가 다릅니다."}</Text>
            <AuthButton>Sign up</AuthButton>
          </div>
        </div>
        <div className={styles.context}>
          <Text>이미 회원이신가요?</Text>
          <NavLink to={"/login"}>
            <Text>Login</Text>
          </NavLink>
        </div>
      </div>
    </>
  );
};

export default SignupPage;
