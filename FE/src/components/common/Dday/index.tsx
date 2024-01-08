import React, { useEffect } from "react";
import Text from "@components/common/Text";
import colors from "@util/colors";
import dayjs from "dayjs";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { selectFriendId } from "@store/friendSlice";

interface Props {
  nearDate: string | number | Date | dayjs.Dayjs;
  comparedDate: string | number | Date | dayjs.Dayjs;
}

const Dday = ({ nearDate, comparedDate }: Props) => {
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const isVisible = nearDate !== null && userId == friendId;

  const dday = (() => {
    if (isVisible) {
      let date = dayjs(comparedDate).format("YYYY-MM-DD");
      let cacl = dayjs(date).diff(dayjs(nearDate), "day");

      if (cacl == 0) return "-DAY";
      return cacl < 0 ? cacl : "+" + cacl;
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
