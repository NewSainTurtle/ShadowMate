import React, { ReactNode, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import { AddPhotoAlternateOutlined, RemoveCircle } from "@mui/icons-material";
import { IconButton } from "@mui/material";
import Text from "@components/common/Text";

interface Props {
  title: string;
  children: ReactNode;
  fileImg: boolean;
}

const FileImg = () => {
  const [imgURL, setImgURL] = useState("");

  const saveImgFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files;
    if (!file) return;

    const reader = new FileReader();
    reader.readAsDataURL(file[0]);
    reader.onloadend = () => {
      setImgURL(reader.result as string);
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

const RemarksColumn = ({ title, children, fileImg }: Props) => {
  return (
    <div className={styles["ment-container"]}>
      <div className={styles["ment-content__box"]}>
        <Text types="medium" bold>
          {title}
        </Text>
        <Text types="semi-medium">{children}</Text>
      </div>
      {fileImg && <FileImg />}
    </div>
  );
};

RemarksColumn.defaultProps = {
  title: "title",
  children: "내용이 들어갑니다.",
  fileImg: false,
};

export default RemarksColumn;
