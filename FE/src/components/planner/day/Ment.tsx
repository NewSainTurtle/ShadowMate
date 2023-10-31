import React, { Dispatch, SetStateAction, useEffect, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import { AddPhotoAlternateOutlined, RemoveCircle } from "@mui/icons-material";
import { IconButton } from "@mui/material";
import Text from "@components/common/Text";
import { firebaseStorage } from "@api/firebaseConfig";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { plannerApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { selectDate, selectDayInfo, setDayRetrospectionImage } from "@store/planner/daySlice";

interface Props {
  title: string;
  maxLength: 50 | 100;
  name: string;
  value: string;
  isFile?: boolean;
  rows?: number;
  onBlur: React.FocusEventHandler;
  onChange: React.ChangeEventHandler;
}

const FileImg = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const date = useAppSelector(selectDate);
  const { retrospectionImage } = useAppSelector(selectDayInfo);

  const saveImage = async (retrospectionImage: string | null) => {
    await plannerApi
      .retrospectionImages(userId, { date, retrospectionImage })
      .then(() => dispatch(setDayRetrospectionImage(retrospectionImage)))
      .catch((err) => console.error(err));
  };

  const renderImage = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files) return;

    const reader = new FileReader();
    reader.readAsDataURL(e.target.files[0]);
    reader.onloadend = () => {
      if (e.target.files != null) {
        const file = e.target.files[0];
        const storageRef = ref(firebaseStorage, `retrospections/${file.name}`);

        uploadBytes(storageRef, file).then((snapshot) =>
          getDownloadURL(snapshot.ref).then((downloadURL) => {
            saveImage(downloadURL);
          }),
        );
      }
      e.target.value = "";
    };
  };

  const imgDelete = () => {
    saveImage(null);
  };

  return (
    <div className={styles["ment-preview__box"]}>
      <input type="file" id="imageFile" accept="image/*" onChange={renderImage} />
      {retrospectionImage ? (
        <div className={styles["ment-preview__img"]}>
          <img src={retrospectionImage} alt="img-preview" />
          <IconButton disableRipple onClick={imgDelete}>
            <RemoveCircle fontSize="small" />
          </IconButton>
        </div>
      ) : (
        <label htmlFor="imageFile">
          <div className={styles["ment-preview__img__none"]}>
            <AddPhotoAlternateOutlined />
          </div>
        </label>
      )}
    </div>
  );
};

const Ment = ({ title, rows, isFile, ...rest }: Props) => {
  const { value, maxLength } = rest;
  const [inputCount, setInputCount] = useState(0);

  useEffect(() => {
    setInputCount(value.replace(/<br\s*\/?>/gm, "\n").length);
  }, [value]);

  const handleOnKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      (document.activeElement as HTMLElement).blur();
    }
  };

  return (
    <div className={styles["ment-container"]}>
      <div className={styles["ment-content__box"]}>
        <Text types="semi-medium" bold>
          {title}
        </Text>
        {rows == 1 ? <input {...rest} onKeyDown={handleOnKeyPress} /> : <textarea rows={rows} {...rest} />}
        <Text types="small">
          ({inputCount}/{maxLength}자)
        </Text>
      </div>
      {isFile && <FileImg />}
    </div>
  );
};

Ment.defaultProps = {
  title: "title",
  value: "내용이 들어갑니다.",
};

export default Ment;
