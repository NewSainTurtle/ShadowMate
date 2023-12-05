import React from "react";
import { NavLink } from "react-router-dom";
import styles from "@styles/auth/Signup.module.scss";
import SignupAccount from "./SignupAccount";
import Text from "@components/common/Text";

const Signup = () => {
  return (
    <div className={styles["signup_display"]}>
      <div className={styles["signup_container"]}>
        <div className={styles["signup_logo"]}>Sign up</div>

        <SignupAccount />

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
