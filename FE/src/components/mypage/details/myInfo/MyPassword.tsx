import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Input from "@components/common/Input";
import Text from "@components/common/Text";
import SaveIcon from "@mui/icons-material/Save";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { userApi } from "@api/Api";
import { removeWhitespaceRegex, userRegex } from "@util/regex";

const MyPassword = () => {
  const userId: number = useAppSelector(selectUserId);
  const [password, setPassword] = useState({
    oldPassword: "",
    newPassword: "",
    newPasswordCheck: "",
  });
  const { oldPassword, newPassword, newPasswordCheck } = password;
  const [error, setError] = useState({
    oldPassword: false,
    newPassword: false,
    newPasswordCheck: false,
  });

  const handlePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    if (name == "oldPassword" && error.oldPassword) setError({ ...error, [name]: false });
    else if (name == "newPassword") {
      setError({
        ...error,
        [name]: !!value && !userRegex.password.test(value),
        newPasswordCheck: !!newPasswordCheck && newPasswordCheck != value,
      });
    } else if (name == "newPasswordCheck") {
      setError({ ...error, [name]: !!value && newPassword != value });
    }

    setPassword({
      ...password,
      [name]: value.replace(removeWhitespaceRegex, ""),
    });
  };

  const saveMyInfo = () => {
    if (!password.oldPassword.length) setError({ ...error, oldPassword: true });
    else if (!(error.newPassword || error.newPasswordCheck)) {
      userApi
        .password(userId, { oldPassword, newPassword })
        .then(() => {
          setPassword({
            oldPassword: "",
            newPassword: "",
            newPasswordCheck: "",
          });
        })
        .catch(() => setError({ ...error, oldPassword: true }));
    }
  };

  return (
    <div className={styles["info__cantainer"]}>
      <div className={styles["info__contents"]}>
        <div className={styles["info__line"]}>
          <div>
            <Text>비밀번호 변경</Text>
          </div>
          <div>
            <div className={styles["info__password"]}>
              <Input
                types="password"
                placeholder="현재 비밀번호 입력"
                name="oldPassword"
                value={oldPassword}
                onChange={handlePassword}
                error={error.oldPassword}
                helperText={error.oldPassword ? "현재 비밀번호와 일치하지 않습니다." : " "}
                maxLength={20}
              />
              <Input
                types="password"
                placeholder="변경할 비밀번호 입력"
                name="newPassword"
                value={newPassword}
                onChange={handlePassword}
                error={error.newPassword}
                helperText={"6~20자의 영문, 숫자, 특수문자(!?^&*@#)를 사용해 주세요."}
                maxLength={20}
              />
              <Input
                types="password"
                placeholder="변경할 비밀번호 확인"
                name="newPasswordCheck"
                value={newPasswordCheck}
                onChange={handlePassword}
                error={error.newPasswordCheck}
                helperText={error.newPasswordCheck ? "비밀번호가 일치하지 않습니다." : " "}
                maxLength={20}
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
