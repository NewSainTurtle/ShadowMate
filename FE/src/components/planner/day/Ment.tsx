import React, { Dispatch, SetStateAction, useEffect, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import { AddPhotoAlternateOutlined, RemoveCircle } from "@mui/icons-material";
import { IconButton } from "@mui/material";
import Text from "@components/common/Text";

interface FileImgeProps {
  fileImg: string;
  setFileImg: Dispatch<SetStateAction<string>>;
}

interface Props {
  title: string;
  maxLength: 50 | 100;
  name: string;
  value: string;
  isFile?: boolean;
  fileImg?: string;
  setFileImg?: Dispatch<SetStateAction<string>>;
  rows?: number;
  onBlur: React.FocusEventHandler;
  onChange: React.ChangeEventHandler;
}

const FileImg = ({ fileImg, setFileImg }: FileImgeProps) => {
  const saveImgFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files;
    if (!file) return;

    const reader = new FileReader();
    reader.readAsDataURL(file[0]);
    reader.onloadend = () => {
      setFileImg(reader.result as string);
      e.target.value = "";
    };
  };

  const imgDelete = () => {
    setFileImg("");
  };

  return (
    <div className={styles["ment-preview__box"]}>
      <input type="file" id="imageFile" accept="image/*" onChange={saveImgFile} />
      {fileImg ? (
        <div className={styles["ment-preview__img"]}>
          <img src={fileImg} alt="img-preview" />
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

const Ment = ({ title, rows, isFile, fileImg, setFileImg, ...rest }: Props) => {
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
      {isFile && <FileImg fileImg={fileImg!} setFileImg={setFileImg!} />}
    </div>
  );
};

Ment.defaultProps = {
  title: "title",
  value: "내용이 들어갑니다.",
  fileImg: "",
};

export default Ment;
