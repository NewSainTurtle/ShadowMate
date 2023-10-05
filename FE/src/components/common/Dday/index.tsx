import React, { ReactNode, useMemo } from "react";
import Text from "@components/common/Text";
import colors from "@util/colors";
import dayjs from "dayjs";

interface Props {
  comparedDate: string | number | Date | dayjs.Dayjs;
}

const Dday = ({ comparedDate }: Props) => {
  const tempDday = dayjs("2023-11-16"); // (임시)가장 최근 DDAY설정한 날짜

  const dday = (function () {
    let date = dayjs(comparedDate).format("YYYY-MM-DD");
    let cacl = dayjs(date).diff(dayjs(tempDday), "day");
    return cacl == 0 ? "-DAY" : cacl < 0 ? cacl : "+" + cacl;
  })();

  const style = {
    color: colors.colorGray_Light_4,
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
