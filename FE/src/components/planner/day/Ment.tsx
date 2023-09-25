import React, { useEffect, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import { AddPhotoAlternateOutlined, RemoveCircle } from "@mui/icons-material";
import { IconButton } from "@mui/material";
import Text from "@components/common/Text";

interface Props {
  title: string;
  fileImg: boolean;
  maxLength: 50 | 100;
  name: string;
  value: string;
  rows?: number;
  onChange: React.ChangeEventHandler;
}

const FileImg = () => {
  const [imgURL, setImgURL] = useState("");

  const saveImgFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files;
    if (!file) return;
    console.log(file);

    const reader = new FileReader();
    reader.readAsDataURL(file[0]);
    reader.onloadend = () => {
      setImgURL(reader.result as string);
      e.target.value = "";
    };
  };

  const imgDelete = () => {
    setImgURL("");
  };

  return (
    <div className={styles["ment-preview__box"]}>
      <input type="file" id="imageFile" accept="image/*" onChange={(e) => saveImgFile(e)} />
      {imgURL ? (
        <div className={styles["ment-preview__img"]}>
          <img src={imgURL} alt="img-preview" />
          <IconButton disableRipple onClick={() => imgDelete()}>
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

const Ment = ({ title, fileImg, rows, ...rest }: Props) => {
  const { value, maxLength } = rest;
  const [inputCount, setInputCount] = useState(0);

  useEffect(() => {
    setInputCount(value.replace(/<br\s*\/?>/gm, "\n").length);
  }, [value]);

  return (
    <div className={styles["ment-container"]}>
      <div className={styles["ment-content__box"]}>
        <Text types="medium" bold>
          {title}
        </Text>
        {rows == 1 ? <input {...rest} /> : <textarea rows={rows} {...rest} />}
        <Text types="small">
          ({inputCount}/{maxLength}자)
        </Text>
      </div>
      {fileImg && <FileImg />}
    </div>
  );
};

Ment.defaultProps = {
  title: "title",
  value: "내용이 들어갑니다.",
  fileImg: false,
};

export default Ment;
