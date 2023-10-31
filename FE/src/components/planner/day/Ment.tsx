import React, { Dispatch, SetStateAction, useEffect, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import { AddPhotoAlternateOutlined, RemoveCircle } from "@mui/icons-material";
import { IconButton } from "@mui/material";
import Text from "@components/common/Text";

interface FileImgeProps {
  retrospectionImage: File | null;
  setRetrospectionImage: Dispatch<SetStateAction<File | null>>;
}

interface Props {
  title: string;
  maxLength: 50 | 100;
  name: string;
  value: string;
  isFile?: boolean;
  retrospectionImage?: File | null;
  setRetrospectionImage?: Dispatch<SetStateAction<File | null>>;
  rows?: number;
  onBlur: React.FocusEventHandler;
  onChange: React.ChangeEventHandler;
}

const FileImg = ({ retrospectionImage, setRetrospectionImage }: FileImgeProps) => {
  const [imgPreview, setImgPreview] = useState<string>(() => {
    const reader = new FileReader();
    if (retrospectionImage) {
      reader.readAsDataURL(retrospectionImage);
      reader.onload = () => {
        return reader.result;
      };
    }
    return "";
  });

  const saveImgFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files) return;

    const reader = new FileReader();
    reader.readAsDataURL(e.target.files[0]);
    reader.onloadend = () => {
      setImgPreview(reader.result as string);
      if (e.target.files != null) {
        setRetrospectionImage(e.target.files[0]);
      }
      e.target.value = "";
    };
  };

  const imgDelete = () => {
    setRetrospectionImage(null);
  };

  return (
    <div className={styles["ment-preview__box"]}>
      <input type="file" id="imageFile" accept="image/*" onChange={saveImgFile} />
      {imgPreview ? (
        <div className={styles["ment-preview__img"]}>
          <img src={imgPreview} alt="img-preview" />
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

const Ment = ({ title, rows, isFile, retrospectionImage, setRetrospectionImage, ...rest }: Props) => {
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
      {isFile && <FileImg retrospectionImage={retrospectionImage!} setRetrospectionImage={setRetrospectionImage!} />}
    </div>
  );
};

Ment.defaultProps = {
  title: "title",
  value: "내용이 들어갑니다.",
  retrospectionImage: null,
};

export default Ment;
