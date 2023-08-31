import React, { DOMAttributes } from "react";
import styles from "@styles/common/FriendProfile.module.scss";
import Text from "@components/common/Text";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import colors from "@util/colors";
import { StringLiteral } from "typescript";

export interface ProfileProps {
  nickname: string;
  message: string;
  src: string;
}

interface Props {
  types: "삭제" | "추가" | "요청" | "아이콘";
  profile: ProfileProps;
  onClick?: DOMAttributes<HTMLDivElement>;
}

const buttons = {
  삭제: <button className={styles.button}>수락</button>,
  추가: (
    <button className={styles.button} style={{ backgroundColor: "var(--color-btn-blue)" }}>
      친구 추가
    </button>
  ),
  요청: (
    <>
      <button className={styles.button} style={{ backgroundColor: "var(--color-btn-blue)" }}>
        수락
      </button>
      <button className={styles.button}>거절</button>
    </>
  ),
  아이콘: (
    <div>
      <PersonAddIcon />
    </div>
  ),
};

const FriendProfile = ({ types, profile, onClick }: Props) => {
  return (
    <div className={styles.container}>
      <div className={styles.img}>
        <img src={profile.src} alt="profile img" />
      </div>
      <div className={styles.profile}>
        <Text types="semi-medium" bold>
          {profile.nickname}
        </Text>
        <Text types="default">{profile.message}</Text>
      </div>
      <div className={styles.button}>{buttons[types]}</div>
    </div>
  );
};

export default FriendProfile;
