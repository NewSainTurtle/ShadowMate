import React, { useEffect, useState } from "react";
import styles from "@styles/common/Profile.module.scss";
import Text from "@components/common/Text";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import { Avatar } from "@mui/material";
import { followApi, userApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { followType } from "@util/friend.interface";
import { setFollowState, setFriendInfo } from "@store/friendSlice";
import { useNavigate } from "react-router-dom";

export interface ProfileConfig {
  userId: number;
  nickname: string;
  profileImage: string;
  statusMessage: string;
}

interface Props {
  profile: ProfileConfig;
  types: "기본" | "아이콘" | followType["types"];
}

interface ProfileButtonProps extends Omit<Props, "profile"> {
  profileId: number;
  nickname: string;
}

const ProfileButton = ({ profileId, types, nickname }: ProfileButtonProps) => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const [state, setState] = useState(0);
  const [type, setType] = useState(types);

  useEffect(() => {
    if (state != 0) dispatch(setFollowState(state));
    if (types == "기본") {
      userApi.searches(userId, { nickname }).then((res) => {
        if (res.data.data.isFollow == "EMPTY") setType("아이콘");
        else setType("기본");
      });
    }
  }, [state]);

  const handleClick = (() => {
    const followRequested = async () => {
      await followApi
        .addRequested(userId, { followingId: profileId })
        .then(() => setState(1))
        .catch((err) => console.error(err));
    };
    const cancelRequested = async () => {
      await followApi
        .cancelRequested(userId, { receiverId: profileId })
        .then(() => setState(2))
        .catch((err) => console.error(err));
    };
    const receiveAcceptiance = async () => {
      await followApi
        .receive(userId, { requesterId: profileId, followReceive: true })
        .then(() => setState(3))
        .catch((err) => console.error(err));
    };
    const receiveRefusal = async () => {
      await followApi
        .receive(userId, { requesterId: profileId, followReceive: false })
        .then(() => setState(4))
        .catch((err) => console.error(err));
    };
    const deleteFollower = async () => {
      await followApi
        .deleteFollowers(userId, { followerId: profileId })
        .then(() => setState(5))
        .catch((err) => console.error(err));
    };
    const deleteFollowing = async () => {
      await followApi
        .deleteFollowing(userId, { followingId: profileId })
        .then(() => setState(6))
        .catch((err) => console.error(err));
    };

    return {
      followRequested,
      cancelRequested,
      receiveAcceptiance,
      receiveRefusal,
      deleteFollower,
      deleteFollowing,
    };
  })();

  const { followRequested, cancelRequested, receiveAcceptiance, receiveRefusal, deleteFollower, deleteFollowing } =
    handleClick;

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
        <button onClick={deleteFollower}>삭제</button>
      </>
    ),
    요청: (
      <>
        <button style={{ backgroundColor: "var(--color-btn-blue)" }} onClick={receiveAcceptiance}>
          수락
        </button>
        <button onClick={receiveRefusal}>거절</button>
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
  }[type];
};

const FriendProfile = ({ types, profile }: Props) => {
  const dispatch = useAppDispatch();
  const navigator = useNavigate();
  const { userId, profileImage, nickname, statusMessage } = profile;

  const handleMoveToFriendProfile = () => {
    dispatch(setFriendInfo(profile));
    navigator("/month");
  };

  return (
    <div className={styles["fprofile_container"]}>
      <div className={styles["profile_img"]} onClick={handleMoveToFriendProfile}>
        <Avatar src={profileImage} />
      </div>
      <div className={styles["fprofile_content"]} onClick={handleMoveToFriendProfile}>
        <Text types="semi-medium" bold>
          {nickname}
        </Text>
        <Text types="default">{statusMessage}</Text>
      </div>
      <div className={styles["fprofile_button"]}>
        {<ProfileButton types={types} profileId={userId} nickname={nickname} />}
      </div>
    </div>
  );
};

export default FriendProfile;
