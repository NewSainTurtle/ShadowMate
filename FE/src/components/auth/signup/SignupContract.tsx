import React, { useState } from "react";
import styles from "@styles/auth/Signup.module.scss";
import AuthButton from "../AuthButton";
import Text from "@components/common/Text";
import Checkbox from "@components/common/Checkbox";
import { Conditions, PersonalInformation } from "@util/Consent";

const SignupContract = () => {
  const onClickNext = () => {};

  return (
    <>
      <div className={styles["signup-contract__form"]}>
        <div className={`${styles["signup-contract__line"]} ${styles["all-agree"]}`}>
          <Text types="small">전체 동의 합니다.</Text>
          <Checkbox />
        </div>

        <div>
          <div className={styles["signup-contract__line"]}>
            <div className={styles["signup-contract__title"]}>
              <Text types="small">이용약관 동의</Text>
              <Text types="small"> (필수)</Text>
            </div>
            <Checkbox />
          </div>
          <textarea className={styles["signup-contract__textarea"]} readOnly>
            {Conditions}
          </textarea>
        </div>

        <div>
          <div className={styles["signup-contract__line"]}>
            <div className={styles["signup-contract__title"]}>
              <Text types="small">개인정보 수집 및 이용 동의</Text>
              <Text types="small"> (필수)</Text>
            </div>
            <Checkbox />
          </div>
          <textarea className={styles["signup-contract__textarea"]} readOnly>
            {PersonalInformation}
          </textarea>
        </div>
      </div>

      <div className={styles["signup-contract__button"]} onClick={onClickNext}>
        <AuthButton>Next</AuthButton>
      </div>
    </>
  );
};

export default SignupContract;
