import React, { ReactNode } from "react";
import Text from "@components/common/Text";
import colors from "@util/colors";

interface Props {
  children: ReactNode;
}

const Dday = ({ children }: Props) => {
  const style = {
    color: colors.colorGray_Light_4,
  };

  return (
    <span style={style}>
      <Text types="semi-medium" bold>
        D{children}
      </Text>
    </span>
  );
};

export default Dday;
