import React, { DOMAttributes, ReactNode } from "react";
import styles from "@styles/common/Button.module.scss";
import Text from "../Text";

interface Props {
  children: ReactNode;
  types?: "gray" | "red" | "blue";
  onClick?: DOMAttributes<HTMLDivElement>;
}

const Button = ({ children, types = "blue", onClick }: Props) => {
  return (
    <div className={`${styles.button} ${styles[`__${types}`]}`}>
      <Text types="small">{children}</Text>
    </div>
  );
};

export default Button;
