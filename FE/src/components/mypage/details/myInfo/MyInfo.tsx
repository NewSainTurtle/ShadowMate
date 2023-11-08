import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Input from "@components/common/Input";
import Text from "@components/common/Text";
import { Avatar } from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import Button from "@components/common/Button";
import { selectUserId, selectUserInfo, setUserInfo, userInfoConfig } from "@store/authSlice";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { firebaseStorage } from "@api/firebaseConfig";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { userApi } from "@api/Api";
import { userRegex } from "@util/regex";

const MyPageInfo = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const myInfoData: userInfoConfig = useAppSelector(selectUserInfo);
  const [userMyInfo, setUserMyInfo] = useState<userInfoConfig>(myInfoData);
  const { email, nickname, profileImage, statusMessage } = userMyInfo;
  const [saveImageFile, setSaveImageFile] = useState<File | null>(null);
  const [isNickanmeAuthentication, setNickanmeAuthentication] = useState(myInfoData.nickname == nickname);
  const [isErrorButton, setErrorButton] = useState(false);
  const [error, setError] = useState({
    nickname: false,
    statusMessage: false,
  });
  const [length, setLength] = useState({
    nickname: nickname.length,
    statusMessage: statusMessage.length,
  });

  const handleUser = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setLength({ ...length, [name]: value.length });
    if (name == "nickname") {
      if (setErrorButton) setErrorButton(false);
      if (isNickanmeAuthentication) setNickanmeAuthentication(false);
      if (myInfoData.nickname == value) setNickanmeAuthentication(true);
      if (!userRegex.nickname.test(value)) setError({ ...error, [name]: true });
      else setError({ ...error, [name]: false });
    } else if (name == "statusMessage") {
      if (!userRegex.statusMessage.test(value)) setError({ ...error, [name]: true });
      else setError({ ...error, [name]: false });
    }
    setUserMyInfo({
      ...userMyInfo,
      [name]: value,
    });
  };

  const onClickNickName = () => {
    if (userRegex.nickname.test(nickname)) {
      setNickanmeAuthentication(true);
      setErrorButton(false);
      //백 API 수정 필요
      // authApi
      //   .nickname({ nickname })
      //   .then(() => {
      //     setNickanmeAuthentication(true);
      //     setErrorMessage("");
      //   })
      //   .catch((err) => {
      //     setNickanmeAuthentication(false);
      //     setErrorMessage("중복된 닉네임 입니다");
      //   });
    } else {
      setNickanmeAuthentication(false);
    }
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
    if (myInfoData == userMyInfo) return;
    if (!isNickanmeAuthentication) {
      setErrorButton(true);
    } else {
      let newProfileImage = profileImage;
      if (saveImageFile != null) {
        const file = saveImageFile;
        const storageRef = ref(firebaseStorage, `profile/${userId}`);
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
    }
  };

  const errorButtonStyle = isErrorButton ? "--error" : "";

  const MY_INFO = (() => {
    return [
      { title: "이메일", node: <Input value={email} disabled /> },
      {
        title: "닉네임",
        node: (
          <div className={`${styles["info__profile-nickname"]} ${styles[errorButtonStyle]}`}>
            <Input
              placeholder="닉네임"
              name="nickname"
              value={nickname}
              onChange={handleUser}
              error={error.nickname}
              helperText={
                error.nickname
                  ? "공백을 제외한 2 ~ 10자의 닉네임을 입력할 수 있습니다."
                  : `글자 수: ${length.nickname}/10`
              }
            />
            <Button types="gray" onClick={onClickNickName} disabled={isNickanmeAuthentication}>
              중복검사
            </Button>
          </div>
        ),
      },
      {
        title: "상태 메세지",
        node: (
          <Input
            placeholder="상태 메세지 입력"
            name="statusMessage"
            value={statusMessage}
            onChange={handleUser}
            error={error.statusMessage}
            helperText={
              error.statusMessage
                ? "0 ~ 20자의 상태 메세지를 입력할 수 있습니다."
                : `글자 수: ${length.statusMessage}/20`
            }
          />
        ),
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

export default MyPageInfo;
