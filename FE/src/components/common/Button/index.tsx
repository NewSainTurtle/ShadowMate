import React, { DOMAttributes, MouseEventHandler, ReactNode } from "react";
import styles from "@styles/common/Button.module.scss";
import Text from "../Text";

interface Props {
  children: ReactNode;
  types?: "gray" | "red" | "blue";
  onClick?: MouseEventHandler<HTMLDivElement>;
}

const Button = ({ children, types = "blue", ...rest }: Props) => {
  return (
    <div className={`${styles.button} ${styles[`__${types}`]}`} {...rest}>
      <Text types="small">{children}</Text>
    </div>
  );
};

export default Button;
