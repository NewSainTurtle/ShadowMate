import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Input from "@components/common/Input";
import Text from "@components/common/Text";
import { Avatar } from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import Button from "@components/common/Button";
import Modal from "@components/common/Modal";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import { selectUserInfo, setUserInfo, userInfoConfig } from "@store/authSlice";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { userApi } from "@api/Api";

const MyPageInfo = () => {
  const dispatch = useAppDispatch();
  const myInfoData = useAppSelector(selectUserInfo);
  const [userMyInfo, setUserMyInfo] = useState<userInfoConfig>(myInfoData);
  const [imgPreview, setImgPreview] = useState<string>("");
  const [password, setPassword] = useState({
    oldPassword: "",
    newPassword: "",
    newPasswordCheck: "",
  });
  const { userId, email, nickname, profileImage, statusMessage } = userMyInfo;
  const { oldPassword, newPassword, newPasswordCheck } = password;
  const errorCheck = !(newPassword == "" || newPasswordCheck == "") && newPassword != newPasswordCheck;
  const [Modalopen, setModalOpen] = useState(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  const handleUser = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserMyInfo({
      ...userMyInfo,
      [e.target.name]: e.target.value,
    });
  };

  const handlePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword({
      ...password,
      [e.target.name]: e.target.value,
    });
  };

  const deleteImgFile = () => {
    setUserMyInfo({
      ...userMyInfo,
      profileImage: "",
    });
    setImgPreview("");
  };

  const saveImgFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files) return;
    const reader = new FileReader();
    reader.readAsDataURL(e.target.files[0]);
    reader.onloadend = () => {
      if (e.target.files != null)
        setUserMyInfo({
          ...userMyInfo,
          profileImage: e.target.files[0] as unknown as string,
        });
      setImgPreview(reader.result as string);
      e.target.value = "";
    };
  };

  const saveMyInfo = () => {
    userApi
      .myPage(userId.toString(), { nickname, statusMessage })
      .then((res) => {
        console.log("프로필 변경 성공", res);
        // dispatch(setUserInfo({ ...myInfoData, email, nickname, profileImage, statusMessage }));
      })
      .catch((err) => console.error("프로필 변경 실패", err))
      .then(() => {
        // 사진이 달라지면
        if (userMyInfo.profileImage != myInfoData.profileImage) {
          if (userMyInfo.profileImage == "") {
            userApi
              .deleteProfileImg(userId.toString())
              .then((res) => console.log("사진 삭제 성공", res))
              .catch((err) => console.error("사진 삭제 실패", err));
          } else {
            userApi
              .editProfileImg(userId.toString(), { newProfileImage: userMyInfo.profileImage })
              .then((res) => console.log("사진 변경 성공", res))
              .catch((err) => console.error("사진 변경 실패", err));
          }
        }
      })
      .then(() => {
        // 비밀번호가 변경되면
        if (!errorCheck) {
          userApi
            .password(userId.toString(), { oldPassword, newPassword })
            .then((res) => console.log("비밀번호 변경 성공", res))
            .catch((err) => console.error("비밀번호 변경 실패", err));
        }
      });
  };

  const deleteProfile = () => {
    userApi
      .userOut(userId.toString())
      .then((res) => console.log("회원탈퇴 성공", res))
      .catch((err) => console.error("회원탈퇴 실패", err));
  };

  const MY_INFO = (() => {
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
            <input type="file" id="imageFile" accept="image/*" onChange={saveImgFile} />
            <label htmlFor="imageFile">
              <Avatar src={imgPreview} />
            </label>
            {imgPreview && <div className={styles["button--delete"]} onClick={deleteImgFile} />}
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
        <div onClick={handleOpen}>
          <Text>회원 탈퇴</Text>
        </div>
      </div>
      <div className={styles["info__button"]}>
        <div className={styles["info__button--save"]} onClick={saveMyInfo}>
          <SaveIcon />
          <Text>저장</Text>
        </div>
      </div>

      <Modal open={Modalopen} onClose={handleClose}>
        <div className={styles["info__modal"]}>
          <WarningAmberRoundedIcon />
          <Text types="small">
            <>정말 탈퇴하시겠습니까?</>
            <br />
            <></>
          </Text>
          <Text types="small">계정 탈퇴는 취소할 수 없습니다</Text>
          <div>
            <div onClick={handleClose}>
              <Text types="small">취소</Text>
            </div>
            <div onClick={deleteProfile}>
              <Text types="small">탈퇴</Text>
            </div>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default MyPageInfo;
