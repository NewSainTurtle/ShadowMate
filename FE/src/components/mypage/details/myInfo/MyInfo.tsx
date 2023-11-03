import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Input from "@components/common/Input";
import Text from "@components/common/Text";
import { Avatar } from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import Button from "@components/common/Button";
import Modal from "@components/common/Modal";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import { selectUserId, selectUserInfo, setUserInfo, userInfoConfig } from "@store/authSlice";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { firebaseStorage } from "@api/firebaseConfig";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { userApi } from "@api/Api";

const MyPageInfo = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const myInfoData = useAppSelector(selectUserInfo);
  const [userMyInfo, setUserMyInfo] = useState<userInfoConfig>(myInfoData);
  const [saveImageFile, setSaveImageFile] = useState<File | null>(null);
  const { email, nickname, profileImage, statusMessage } = userMyInfo;
  const [Modalopen, setModalOpen] = useState(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

  const handleUser = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserMyInfo({
      ...userMyInfo,
      [e.target.name]: e.target.value,
    });
  };

  const deleteImgFile = () => {
    setUserMyInfo({
      ...userMyInfo,
      profileImage: "",
    });
  };

  const renderImage = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files) return;
    const reader = new FileReader();
    reader.readAsDataURL(e.target.files[0]);
    reader.onloadend = () => {
      setUserMyInfo({ ...userMyInfo, profileImage: reader.result as string });
      if (e.target.files != null) {
        setSaveImageFile(e.target.files[0]);
      }
      e.target.value = "";
    };
  };

  const saveMyInfo = async () => {
    let newProfileImage = profileImage;
    if (saveImageFile != null) {
      const file = saveImageFile;
      const storageRef = ref(firebaseStorage, `profile/${file.name}`);
      await uploadBytes(storageRef, file).then((snapshot) =>
        getDownloadURL(snapshot.ref).then((downloadURL) => (newProfileImage = downloadURL)),
      );
    }
    await userApi
      .myPages(userId, { newNickname: nickname, newProfileImage, newStatusMessage: statusMessage })
      .then(() => {
        dispatch(setUserInfo({ ...userMyInfo, profileImage: newProfileImage }));
      })
      .catch((err) => console.error(err));
  };

  const deleteProfile = () => {
    userApi
      .userOut(userId)
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
            <input type="file" id="imageFile" accept="image/*" onChange={renderImage} />
            <label htmlFor="imageFile">
              <Avatar src={profileImage} />
            </label>
            {profileImage && <div className={styles["button--delete"]} onClick={deleteImgFile} />}
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
