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
    newPassword: false,
    newPasswordCheck: !(newPassword == "" || newPasswordCheck == "") && newPassword != newPasswordCheck,
  });
  const [length, setLength] = useState(newPassword.length);

  const handlePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    if (name == "newPassword") {
      setLength(value.length);
      if (value.length > 0 && !userRegex.password.test(value)) setError({ ...error, [name]: true });
      else setError({ ...error, [name]: false });
    }
    if (name == "newPasswordCheck") {
      if (value.length > 0 && newPassword != value) setError({ ...error, [name]: true });
      else setError({ ...error, [name]: false });
    }
    setPassword({
      ...password,
      [name]: value.replace(removeWhitespaceRegex, ""),
    });
  };

  const saveMyInfo = async () => {
    if (!(error.newPassword || error.newPasswordCheck)) return;

    userApi
      .password(userId, { oldPassword, newPassword })
      .then(() => {
        setPassword({
          oldPassword: "",
          newPassword: "",
          newPasswordCheck: "",
        });
      })
      .catch((err) => console.error(err));
  };

  return (
    <div className={styles["info__cantainer"]}>
      <div className={styles["info__contents"]}>
        <div className={styles["info__mypassword"]}>
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
                />
                <Input
                  types="password"
                  placeholder="변경할 비밀번호 입력"
                  name="newPassword"
                  value={newPassword}
                  onChange={handlePassword}
                  error={error.newPassword}
                  helperText={error.newPassword ? "6 ~ 20자의 이름을 입력할 수 있습니다." : `글자 수: ${length}/20`}
                />
                <Input
                  types="password"
                  placeholder="변경할 비밀번호 확인"
                  name="newPasswordCheck"
                  value={newPasswordCheck}
                  onChange={handlePassword}
                  error={error.newPasswordCheck}
                  helperText={error.newPasswordCheck ? "비밀번호가 일치하지 않습니다." : " "}
                />
              </div>
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
