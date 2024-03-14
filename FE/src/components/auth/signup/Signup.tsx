import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import styles from "@styles/auth/Signup.module.scss";
import SignupAccount from "./SignupAccount";
import SignupContract from "./SignupContract";
import Text from "@components/common/Text";

const Signup = () => {
  const [isNext, setNext] = useState(false);
  const handleClickNext = () => setNext(true);

  return (
    <div className={styles["signup_display"]}>
      <div className={styles["signup_container"]}>
        <div className={styles["signup_logo"]}>Sign up</div>
        {!isNext ? <SignupContract handleClickNext={handleClickNext} /> : <SignupAccount />}
        <div className={styles["signup_context"]}>
          <Text types="small">이미 회원이신가요?</Text>
          <NavLink to={"/login"} data-cy="loginButton">
            <Text types="small">Login</Text>
          </NavLink>
        </div>
      </div>
    </div>
  );
};

export default Signup;
