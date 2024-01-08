import React, { useState, ChangeEvent } from "react";
import styles from "@styles/auth/Signup.module.scss";
import AuthButton from "../AuthButton";
import Text from "@components/common/Text";
import Checkbox from "@components/common/Checkbox";
import { Conditions, PersonalInformation } from "@util/Consent";

const SignupContract = ({ handleClickNext }: { handleClickNext: () => void }) => {
  const [allAgreed, setAllAgreed] = useState(false);
  const [agreements, setAgreements] = useState({
    conditionsAgreed: false,
    personalInfoAgreed: false,
  });

  const handleAgreementChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, checked } = e.target;
    setAgreements({ ...agreements, [name]: checked });
    const allChecked = Object.values({ ...agreements, [name]: checked }).every((value) => value);
    setAllAgreed(allChecked);
  };

  const handleAllAgreementChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { checked } = e.target;
    setAgreements({
      ...agreements,
      conditionsAgreed: checked,
      personalInfoAgreed: checked,
    });
    setAllAgreed(checked);
  };

  const onClickNext = () => {
    if (allAgreed) handleClickNext();
  };

  const { conditionsAgreed, personalInfoAgreed } = agreements;

  return (
    <>
      <div className={styles["signup-contract__form"]}>
        <div className={`${styles["signup-contract__line"]} ${styles["all-agree"]}`}>
          <Text types="small">전체 동의하기</Text>
          <Checkbox name="allAgreed" checked={allAgreed} onChange={handleAllAgreementChange} />
        </div>

        <div>
          <div className={styles["signup-contract__line"]}>
            <div className={styles["signup-contract__title"]}>
              <Text types="small">이용약관 동의</Text>
              <Text types="small"> (필수)</Text>
            </div>
            <Checkbox name="conditionsAgreed" checked={conditionsAgreed} onChange={handleAgreementChange} />
          </div>
          <textarea className={styles["signup-contract__textarea"]} value={Conditions} readOnly>
            {Conditions}
          </textarea>
        </div>

        <div>
          <div className={styles["signup-contract__line"]}>
            <div className={styles["signup-contract__title"]}>
              <Text types="small">개인정보 수집 및 이용 동의</Text>
              <Text types="small"> (필수)</Text>
            </div>
            <Checkbox name="personalInfoAgreed" checked={personalInfoAgreed} onChange={handleAgreementChange} />
          </div>
          <textarea className={styles["signup-contract__textarea"]} value={PersonalInformation} readOnly>
            {PersonalInformation}
          </textarea>
        </div>
      </div>

      <div className={`${styles["signup-contract__button"]} ${!allAgreed ? styles["--disable"] : ""}`}>
        <AuthButton onClick={onClickNext}>Next</AuthButton>
      </div>
    </>
  );
};

export default SignupContract;
