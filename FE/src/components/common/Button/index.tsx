import React, { MouseEventHandler, ReactNode } from "react";
import styles from "@styles/common/Button.module.scss";
import Text from "@components/common/Text";

interface Props {
  children: ReactNode;
  types?: "gray" | "red" | "blue";
  onClick?: MouseEventHandler<HTMLButtonElement>;
  disabled?: boolean;
}

const Button = ({ children, types = "blue", ...rest }: Props) => {
  return (
    <button className={[styles.button, styles[`__${types}`]].join(" ")} {...rest}>
      <Text types="small">{children}</Text>
    </button>
  );
};

export default Button;
