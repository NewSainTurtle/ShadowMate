import React, { DOMAttributes, MouseEventHandler } from "react";
import styles from "@styles/common/Profile.module.scss";
import Text from "@components/common/Text";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import { Avatar } from "@mui/material";

export interface ProfileConfig {
  nickname: string;
  message: string;
  src: string;
}

interface Props {
  types: "기본" | "삭제" | "친구 신청" | "팔로워 신청" | "요청" | "취소" | "아이콘";
}

interface ProfileProps extends Props {
  profile: ProfileConfig;
  onClick?: MouseEventHandler<HTMLDivElement>;
}

const ProfileButton = ({ types }: Props) => {
  return {
    기본: <></>,
    삭제: <button>삭제</button>,
    "친구 신청": <button style={{ backgroundColor: "var(--color-btn-blue)" }}>친구 신청</button>,
    "팔로워 신청": (
      <>
        <button style={{ backgroundColor: "var(--color-btn-blue)" }}>친구 신청</button>
        <button>거절</button>
      </>
    ),
    요청: (
      <>
        <button style={{ backgroundColor: "var(--color-btn-blue)" }}>수락</button>
        <button>거절</button>
      </>
    ),
    취소: (
      <button style={{ backgroundColor: "var(--color-gray-2)" }}>
        <span style={{ color: "var(--color-text)" }}>요청 취소</span>
      </button>
    ),
    아이콘: (
      <div>
        <PersonAddIcon />
      </div>
    ),
  }[types];
};

const FriendProfile = ({ types, profile, ...rest }: ProfileProps) => {
  return (
    <div className={styles["fprofile_container"]}>
      <div className={styles["profile_img"]}>
        <Avatar src={profile?.src} />
      </div>
      <div className={styles["fprofile_content"]}>
        <Text types="semi-medium" bold>
          {profile?.nickname}
        </Text>
        <Text types="default">{profile?.message}</Text>
      </div>
      <div className={styles["fprofile_button"]} {...rest}>
        {<ProfileButton types={types} />}
      </div>
    </div>
  );
};

export default FriendProfile;
