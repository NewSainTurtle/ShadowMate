import React, { MouseEventHandler, ReactNode } from "react";
import styles from "@styles/auth/AuthButton.module.scss";
import Text from "@components/common/Text";

interface Props {
  children: ReactNode;
  onClick?: MouseEventHandler<HTMLDivElement>;
}

const AuthButton = ({ children, ...rest }: Props) => {
  return (
    <div className={styles.button} {...rest}>
      <Text types="medium">{children}</Text>
    </div>
  );
};

export default AuthButton;
