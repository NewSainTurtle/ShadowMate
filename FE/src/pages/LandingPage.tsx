import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import styles from "@styles/auth/Landing.module.scss";
import Text from "@components/common/Text";
import AuthButton from "@components/auth/AuthButton";
import backgroundImg from "@assets/Imgs/landing_background.svg";

const LandingPage = () => {
  return (
    <div className={styles.landing_container}>
      <img src={backgroundImg} className={styles.landing_backgroundImg} />
      <div className={styles.landing_content}>
        <p className={styles.landing_title}>
          SHADOW
          <br />
          MATE
        </p>
        <div className={styles.landing_description}>
          <Text types="semi-medium" bold>
            당신의 스케줄을 도와줄게요
          </Text>
        </div>
        <NavLink to="/login">
          <AuthButton>Login</AuthButton>
        </NavLink>
      </div>
    </div>
  );
};

export default LandingPage;
