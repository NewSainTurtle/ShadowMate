import React from "react";
import Text from "@components/common/Text";
import colors from "@util/colors";
import dayjs from "dayjs";

interface Props {
  nearDate: string | number | Date | dayjs.Dayjs;
  comparedDate: string | number | Date | dayjs.Dayjs;
}

const Dday = ({ nearDate, comparedDate }: Props) => {
  const isVisible = nearDate !== null;

  const dday = (() => {
    if (isVisible) {
      let date = dayjs(comparedDate).format("YYYY-MM-DD");
      let cacl = dayjs(date).diff(dayjs(nearDate), "day");
      return cacl == 0 ? "-DAY" : cacl < 0 ? cacl : "+" + cacl;
    }
  })();

  const style: React.CSSProperties = {
    color: colors.colorGray_Light_4,
    visibility: isVisible ? "visible" : "hidden",
  };

  return (
    <span style={style}>
      <Text types="semi-medium" bold>
        D{dday}
      </Text>
    </span>
  );
};

export default Dday;
