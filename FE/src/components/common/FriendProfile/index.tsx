import React, { useEffect, useState } from "react";
import styles from "@styles/common/Profile.module.scss";
import Text from "@components/common/Text";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import Avatar from "@components/common/Avatar";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
import { followApi, userApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { followType } from "@util/friend.interface";
import { selectFollowState, setFollowState, setFriendInfo } from "@store/friendSlice";
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
  const [type, setType] = useState(types);

  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const handleDeleteModalOpen = () => setDeleteModalOpen(true);
  const handleDeleteModalClose = () => setDeleteModalOpen(false);

  useEffect(() => {
    if (types == "기본") {
      userApi.searches(userId, { nickname }).then((res) => {
        if (res.data.data.isFollow == "EMPTY") setType("아이콘");
        else setType("기본");
      });
    }
  }, []);

  const handleClick = (() => {
    const followRequested = async () => {
      await followApi
        .addRequested(userId, { followingId: profileId })
        .then(() => dispatch(setFollowState(1 + profileId)))
        .catch((err) => console.error(err));
    };
    const cancelRequested = async () => {
      await followApi
        .cancelRequested(userId, { receiverId: profileId })
        .then(() => dispatch(setFollowState(2 + profileId)))
        .catch((err) => console.error(err));
    };
    const receiveAcceptiance = async () => {
      await followApi
        .receive(userId, { requesterId: profileId, followReceive: true })
        .then(() => dispatch(setFollowState(3 + profileId)))
        .catch((err) => console.error(err));
    };
    const receiveRefusal = async () => {
      await followApi
        .receive(userId, { requesterId: profileId, followReceive: false })
        .then(() => dispatch(setFollowState(4 + profileId)))
        .catch((err) => console.error(err));
    };
    const deleteFollower = async () => {
      await followApi
        .deleteFollowers(userId, { followerId: profileId })
        .then(() => dispatch(setFollowState(5 + profileId)))
        .catch((err) => console.error(err));
    };
    const deleteFollowing = async () => {
      await followApi
        .deleteFollowing(userId, { followingId: profileId })
        .then(() => dispatch(setFollowState(6 + profileId)))
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
    "팔로워 삭제": (
      <>
        <button onClick={handleDeleteModalOpen}>삭제</button>
        <Modal
          types="twoBtn"
          open={deleteModalOpen}
          onClose={handleDeleteModalClose}
          onClick={deleteFollower}
          onClickMessage="삭제"
          warning
        >
          <DeleteModal types="팔로워" />
        </Modal>
      </>
    ),
    "팔로잉 삭제": (
      <>
        <button onClick={handleDeleteModalOpen}>삭제</button>
        <Modal
          types="twoBtn"
          open={deleteModalOpen}
          onClose={handleDeleteModalClose}
          onClick={deleteFollowing}
          onClickMessage="삭제"
          warning
        >
          <DeleteModal types="팔로잉" />
        </Modal>
      </>
    ),
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
        <button onClick={handleDeleteModalOpen}>삭제</button>
        <Modal
          types="twoBtn"
          open={deleteModalOpen}
          onClose={handleDeleteModalClose}
          onClick={deleteFollower}
          onClickMessage="삭제"
          warning
        >
          <DeleteModal types="팔로워 신청" />
        </Modal>
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
  const followState = useAppSelector(selectFollowState);

  const handleMoveToFriendProfile = () => {
    dispatch(setFriendInfo(profile));
    navigator("/month");
  };

  return (
    <div className={styles["fprofile_container"]}>
      <div className={styles["fprofile_img"]} onClick={handleMoveToFriendProfile}>
        <Avatar src={profileImage} />
      </div>
      <div className={styles["fprofile_content"]} onClick={handleMoveToFriendProfile}>
        <Text types="semi-medium" bold>
          {nickname}
        </Text>
        <Text types="default">{statusMessage}</Text>
      </div>
      <div className={styles["fprofile_button"]} key={followState}>
        {<ProfileButton types={types} profileId={userId} nickname={nickname} />}
      </div>
    </div>
  );
};

export default FriendProfile;
