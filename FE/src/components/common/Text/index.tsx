import React, { ReactNode } from "react";
import styles from "@styles/common/Text.module.scss";

interface Props {
  children: ReactNode;
  type?: "small" | "default" | "semi-medium" | "medium" | "semi-large" | "large";
  bold?: boolean;
}

const Text = ({ type = "default", bold, children }: Props) => {
  const style = {
    fontWeight: bold ? "600" : "400",
  };

  return (
    <>
      <span className={`${styles[`text-${type}`]}`} style={style}>
        {children}
      </span>
    </>
  );
};

Text.defaultProps = {
  children: "내용이 들어갑니다.",
  type: "default",
};

export default Text;
