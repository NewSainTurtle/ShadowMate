import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Input from "@components/common/Input";
import Text from "@components/common/Text";
import Avatar from "@components/common/Avatar";
import SaveIcon from "@mui/icons-material/Save";
import Button from "@components/common/Button";
import { selectUserId, selectUserInfo, setUserInfo, userInfoConfig } from "@store/authSlice";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { firebaseStorage } from "@api/firebaseConfig";
import { resizeImage } from "@util/resizeImage";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { authApi, userApi } from "@api/Api";
import { userRegex } from "@util/regex";

const MyPageInfo = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const myInfoData: userInfoConfig = useAppSelector(selectUserInfo);
  const [userMyInfo, setUserMyInfo] = useState<userInfoConfig>(myInfoData);
  const { email, nickname, profileImage, statusMessage } = userMyInfo;
  const [newNickname, setNewNickname] = useState(nickname);
  const [saveImageFile, setSaveImageFile] = useState<File | null>(null);
  const [isNicknameAuthentication, setNicknameAuthentication] = useState(myInfoData.nickname == nickname);
  const [nicknameErrorMessage, setNicknameErrorMessage] = useState(false);
  const [isErrorButton, setErrorButton] = useState(false);
  const [error, setError] = useState({
    nickname: false,
    statusMessage: false,
  });
  const [length, setLength] = useState({
    nickname: nickname.length,
    statusMessage: statusMessage.length,
  });

  const handleUser = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setLength({ ...length, [name]: value.length });
    if (name == "nickname") {
      if (isErrorButton) setErrorButton(false);
      if (isNicknameAuthentication) setNicknameAuthentication(false);
      if (nicknameErrorMessage) setNicknameErrorMessage(false);
      if (isNicknameAuthentication && myInfoData.nickname != newNickname) deletNickName();
      if (myInfoData.nickname == value) setNicknameAuthentication(true);
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

  const deletNickName = async () => {
    await authApi
      .deleteNickname({ nickname: newNickname })
      .then(() => {
        setNewNickname(nickname);
        setNicknameAuthentication(false);
      })
      .catch((err) => console.error(err));
  };

  const onClickNickName = async () => {
    if (!error.nickname) {
      await authApi
        .nickname({ nickname })
        .then(() => {
          setNewNickname(nickname);
          setNicknameAuthentication(true);
          setErrorButton(false);
        })
        .catch(() => {
          setNicknameAuthentication(false);
          setNicknameErrorMessage(true);
        });
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
    if (!isNicknameAuthentication) {
      setErrorButton(true);
    } else {
      let newProfileImage = profileImage;
      if (saveImageFile != null) {
        const file = await resizeImage(saveImageFile);
        const storageRef = ref(firebaseStorage, `profile/${userId}`);
        await uploadBytes(storageRef, file).then((snapshot) =>
          getDownloadURL(snapshot.ref).then((downloadURL) => (newProfileImage = downloadURL)),
        );
      }
      await userApi
        .myPages(userId, { newNickname, newProfileImage, newStatusMessage: statusMessage })
        .then(() => {
          dispatch(setUserInfo({ ...userMyInfo, profileImage: newProfileImage }));
        })
        .catch((err) => console.error(err));
    }
  };

  const MY_INFO = (() => {
    return [
      { title: "이메일", node: <Input value={email} disabled /> },
      {
        title: "닉네임",
        node: (
          <div className={styles["info__profile-nickname"]}>
            <Input
              placeholder="닉네임"
              name="nickname"
              value={nickname}
              onChange={handleUser}
              error={error.nickname || nicknameErrorMessage || isErrorButton}
              helperText={
                isErrorButton
                  ? "닉네임 중복검사를 해주세요!"
                  : nicknameErrorMessage
                  ? "중복된 닉네임 입니다."
                  : error.nickname
                  ? "공백과 특수문자를 제외한 2~10자의 닉네임을 입력할 수 있습니다."
                  : myInfoData.nickname != nickname && isNicknameAuthentication
                  ? "사용가능한 닉네임 입니다."
                  : `글자 수: ${length.nickname}/10`
              }
            />
            <Button types="gray" onClick={onClickNickName} disabled={isNicknameAuthentication}>
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
            <input type="file" id="imageFile" accept="image/jpeg, image/png" onChange={renderImage} />
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
