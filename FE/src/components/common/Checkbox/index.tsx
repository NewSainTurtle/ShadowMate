import React, { InputHTMLAttributes } from "react";
import styles from "@styles/common/Checkbox.module.scss";
import Text from "@components/common/Text";

interface CheckBoxProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

const CheckBox = ({ id, label, ...props }: CheckBoxProps) => {
  return (
    <div className={styles.checkbox}>
      <input id={id} type="checkbox" {...props} />
      {label && (
        <label htmlFor={id}>
          <Text types="small">{label}</Text>
        </label>
      )}
    </div>
  );
};

export default CheckBox;
