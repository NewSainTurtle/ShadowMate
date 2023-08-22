import React from "react";
import styles from "@styles/common/Text.module.scss";
import colors, { ColorsKeyTypes } from "src/util/colors";

interface Props {
  value: string;
  type?: "smallText" | "caption" | "text" | "groupTitle" | "textTitle" | "pageTitle" | "title";
  bold?: boolean;
  color?: ColorsKeyTypes;
  en?: boolean;
}

const Text = ({ value, type = "text", bold, color = "colorBlack", en }: Props) => {
  console.log(color);
  const style = {
    fontFamily: en ? "Montserrat" : "Pretendard-Regular",
    fontWeight: bold ? "600" : "400",
    color: colors[color],
  };

  return (
    <>
      <span className={`${styles[type]}`} style={style}>
        {value}
      </span>
    </>
  );
};

Text.defaultProps = {
  value: "내용이 들어갑니다.",
  type: "text",
  color: "black",
};

export default Text;
