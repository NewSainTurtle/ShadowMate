import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Input from "@components/common/Input";
import Text from "@components/common/Text";
import { Avatar } from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import Button from "@components/common/Button";
import { selectUserInfo, userInfoConfig } from "@store/authSlice";
import { useAppSelector } from "@hooks/hook";

const MyPageInfo = () => {
  const myInfoData = useAppSelector(selectUserInfo);
  const [userInfo, setUserInfo] = useState<userInfoConfig>(myInfoData);
  const [password, setPassword] = useState({
    nowPassword: "",
    newPassword: "",
    newPasswordCheck: "",
  });

  const handleUser = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserInfo({
      ...userInfo,
      [e.target.name]: e.target.value,
    });
  };

  const handlePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword({
      ...password,
      [e.target.name]: e.target.value,
    });
  };

  const saveImgFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files;
    if (!file) return;

    const reader = new FileReader();
    reader.readAsDataURL(file[0]);
    reader.onloadend = () => {
      setUserInfo({
        ...userInfo,
        profileImage: reader.result as string,
      });
      e.target.value = "";
    };
  };

  const MY_INFO = (() => {
    const { email, nickname, profileImage, statusMessage } = userInfo;
    const { nowPassword, newPassword, newPasswordCheck } = password;

    return [
      { title: "이메일", node: <Input value={email} disabled /> },
      {
        title: "닉네임",
        node: (
          <div className={styles["info__profile-nickname"]}>
            <Input placeholder="닉네임" name="nickname" value={nickname} onChange={handleUser} />
            <Button types="gray">중복검사</Button>
          </div>
        ),
      },
      {
        title: "상태 메세지",
        node: <Input placeholder="상태 메세지 입력" name="statusMessage" value={statusMessage} onChange={handleUser} />,
      },
      {
        title: "프로필 사진",
        node: (
          <div className={styles["info__profile-img"]}>
            <input type="file" id="imageFile" accept="image/*" onChange={(e) => saveImgFile(e)} />
            <label htmlFor="imageFile">
              <Avatar src={profileImage} />
            </label>
            {profileImage && <div className={styles["button--delete"]} />}
          </div>
        ),
      },
      {
        title: "비밀번호 변경",
        node: (
          <div className={styles["info__profile-password"]}>
            <Input
              types="password"
              placeholder="현재 비밀번호 입력"
              name="nowPassword"
              value={nowPassword}
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
            />
          </div>
        ),
      },
    ];
  })();

  return (
    <div className={styles["info__cantainer"]}>
      <div className={styles["info__contents"]}>
        {MY_INFO.map((item, idx) => (
          <div className={styles["info__line"]} key={idx}>
            <div>
              <Text>{item.title}</Text>
            </div>
            <div>{item.node}</div>
          </div>
        ))}
        <div>
          <Text>회원 탈퇴</Text>
        </div>
      </div>
      <div className={styles["info__button"]}>
        <div className={styles["info__button--save"]}>
          <SaveIcon />
          <Text>저장</Text>
        </div>
      </div>
    </div>
  );
};

export default MyPageInfo;
