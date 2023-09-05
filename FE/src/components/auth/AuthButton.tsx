import React, { MouseEventHandler, ReactNode } from "react";
import styles from "@styles/auth/AuthButton.moudle.scss";
import Text from "@components/common/Text";

interface Props {
  children: ReactNode;
  onClick?: MouseEventHandler<HTMLDivElement>;
}

const AuthButton = ({ children, ...rest }: Props) => {
  return (
    <div className={styles.button} {...rest}>
      <Text types="small">{children}</Text>
    </div>
  );
};

export default AuthButton;
