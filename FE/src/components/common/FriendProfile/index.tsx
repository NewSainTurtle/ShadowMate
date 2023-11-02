import React, { useEffect, useState } from "react";
import styles from "@styles/common/Profile.module.scss";
import Text from "@components/common/Text";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import { Avatar } from "@mui/material";
import { followApi } from "@api/Api";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { followType } from "@util/friend.interface";

export interface ProfileConfig {
  userId: number;
  nickname: string;
  message: string;
  src: string;
}

interface Props {
  profile: ProfileConfig;
  types: "기본" | "아이콘" | followType["types"];
}

interface ProfileButtonProps extends Omit<Props, "profile"> {
  profileId: number;
}

const ProfileButton = ({ profileId, types }: ProfileButtonProps) => {
  const userId = useAppSelector(selectUserId);

  const handleClick = (() => {
    const followRequested = () => {
      followApi
        .addRequested(userId, { followingId: profileId })
        // .then(() => setRender("요청"))
        .catch((err) => console.error(err));
    };
    const cancelRequested = () => {
      followApi
        .cancelRequested(userId, { receiverId: profileId })
        // .then((res) => console.log("팔로우취소", res))
        .catch((err) => console.error(err));
    };
    const deleteFollower = () => {
      followApi
        .deletefollowers(userId, { followerId: profileId })
        // .then((res) => console.log("팔로워 삭제", res))
        .catch((err) => console.error(err));
    };
    const deleteFollowing = () => {
      followApi
        .deleteFollowing(userId, { followingId: profileId })
        // .then((res) => console.log("팔로잉 삭제", res))
        .catch((err) => console.error(err));
    };

    return {
      followRequested,
      cancelRequested,
      deleteFollower,
      deleteFollowing,
    };
  })();

  const { followRequested, cancelRequested, deleteFollower, deleteFollowing } = handleClick;

  return {
    기본: <></>,
    "팔로워 삭제": <button onClick={deleteFollower}>삭제</button>,
    "팔로잉 삭제": <button onClick={deleteFollowing}>삭제</button>,
    "친구 신청": (
      <button style={{ backgroundColor: "var(--color-btn-blue)" }} onClick={followRequested}>
        친구 신청
      </button>
    ),
    "팔로워 신청": (
      <>
        <button style={{ backgroundColor: "var(--color-btn-blue)" }} onClick={followRequested}>
          친구 신청
        </button>
        <button>삭제</button>
      </>
    ),
    요청: (
      <>
        <button style={{ backgroundColor: "var(--color-btn-blue)" }}>수락</button>
        <button>거절</button>
      </>
    ),
    취소: (
      <button style={{ backgroundColor: "var(--color-gray-2)" }} onClick={cancelRequested}>
        <span style={{ color: "var(--color-text)" }}>요청 취소</span>
      </button>
    ),
    아이콘: (
      <div onClick={followRequested}>
        <PersonAddIcon />
      </div>
    ),
  }[types];
};

const FriendProfile = ({ types, profile }: Props) => {
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
      <div className={styles["fprofile_button"]}>{<ProfileButton profileId={profile.userId} types={types} />}</div>
    </div>
  );
};

export default FriendProfile;
