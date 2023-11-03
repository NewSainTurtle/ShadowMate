import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Input from "@components/common/Input";
import Text from "@components/common/Text";
import SaveIcon from "@mui/icons-material/Save";

const MyPassword = () => {
  const [password, setPassword] = useState({
    oldPassword: "",
    newPassword: "",
    newPasswordCheck: "",
  });

  const { oldPassword, newPassword, newPasswordCheck } = password;
  const errorCheck = !(newPassword == "" || newPasswordCheck == "") && newPassword != newPasswordCheck;

  const handlePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword({
      ...password,
      [e.target.name]: e.target.value,
    });
  };

  const saveMyInfo = async () => {};

  return (
    <div className={styles["info__cantainer"]}>
      <div className={styles["info__contents"]}>
        <div className={styles["info__line"]}>
          <div>
            <Text>비밀번호 변경</Text>
          </div>
          <div>
            <div className={styles["info__profile-password"]}>
              <Input
                types="password"
                placeholder="현재 비밀번호 입력"
                name="nowPassword"
                value={oldPassword}
                onChange={handlePassword}
              />
              <Input
                types="password"
                placeholder="변경할 비밀번호 입력"
                name="newPassword"
                value={newPassword}
                onChange={handlePassword}
              />
              <Input
                types="password"
                placeholder="변경할 비밀번호 확인"
                name="newPasswordCheck"
                value={newPasswordCheck}
                onChange={handlePassword}
                error={errorCheck}
              />
            </div>
          </div>
        </div>
      </div>
      <div className={styles["info__button"]}>
        <div className={styles["info__button--save"]} onClick={saveMyInfo}>
          <SaveIcon />
          <Text>저장</Text>
        </div>
      </div>
    </div>
  );
};

export default MyPassword;
