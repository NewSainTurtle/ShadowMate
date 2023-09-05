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
  types: "기본" | "삭제" | "추가" | "요청" | "아이콘";
}

interface ProfileProps extends Props {
  profile: ProfileConfig;
  onClick?: MouseEventHandler<HTMLDivElement>;
}

const ProfileButton = ({ types }: Props) => {
  return {
    기본: <></>,
    삭제: <button>삭제</button>,
    추가: <button style={{ backgroundColor: "var(--color-btn-blue)" }}>친구 추가</button>,
    요청: (
      <>
        <button style={{ backgroundColor: "var(--color-btn-blue)" }}>수락</button>
        <button>거절</button>
      </>
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
    <div className={styles.fprofile_container}>
      <div className={styles.profile_img}>
        <Avatar src={profile?.src} sx={{ width: 48, height: 48 }} />
      </div>
      <div className={styles.fprofile_content}>
        <Text types="semi-medium" bold>
          {profile?.nickname}
        </Text>
        <Text types="default">{profile?.message}</Text>
      </div>
      <div className={styles.fprofile_button} {...rest}>
        {<ProfileButton types={types} />}
      </div>
    </div>
  );
};

export default FriendProfile;
