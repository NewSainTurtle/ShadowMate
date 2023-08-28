import React, { DOMAttributes, ReactNode } from "react";
import styles from "@styles/common/Button.module.scss";
import Text from "../Text";

interface ButtonProps {
  children: ReactNode;
  type?: "gray" | "red" | "blue";
  onClick?: DOMAttributes<HTMLDivElement>;
}

const Button = ({ children, type = "blue", onClick }: ButtonProps) => {
  return (
    <div className={`${styles.button} ${styles[`__${type}`]}`}>
      <Text type="small">{children}</Text>
    </div>
  );
};

export default Button;
