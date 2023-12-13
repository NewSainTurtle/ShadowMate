import React, { ReactNode } from "react";
import styles from "@styles/common/Text.module.scss";

interface Props {
  children: ReactNode;
  types?: "small" | "default" | "semi-medium" | "medium" | "semi-large" | "large";
  bold?: boolean;
  title?: boolean;
}

const Text = ({ types = "default", bold, title, children }: Props) => {
  const style = {
    fontWeight: title ? "800" : bold ? "600" : "400",
  };

  return (
    <>
      <span className={`${styles[`text-${types}`]}`} style={style}>
        {children}
      </span>
    </>
  );
};

Text.defaultProps = {
  children: "내용이 들어갑니다.",
  types: "default",
};

export default Text;
