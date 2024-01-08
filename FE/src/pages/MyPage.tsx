import React, { useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Profile from "@components/common/Profile";
import MyPageTab from "@components/mypage/MyPageTab";
import MyPageDiary from "@components/mypage/details/diary/Diary";
import MyPageFrame from "@components/mypage/MyPageFrame";
import MyPageInfo from "@components/mypage/details/myInfo/MyInfo";
import MyFriend from "@components/mypage/details/friend/MyFriend";
import MyPassword from "@components/mypage/details/myInfo/MyPassword";
import CancelMembership from "@components/mypage/details/myInfo/CancelMembership";
import { useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo, UserInfoConfig } from "@store/authSlice";

interface Props {
  name?: string;
}

const MyPage = ({ name }: Props) => {
  const [tabName, setTabName] = useState<string>(name ? name : "내 정보 확인");
  const userId: number = useAppSelector(selectUserId);
  const userInfo: UserInfoConfig = useAppSelector(selectUserInfo);

  return (
    <div className={styles["mypage__container"]}>
      <div className={styles["mypage__profile"]}>
        <Profile
          types="로그아웃"
          profile={(() => {
            const { nickname, profileImage, statusMessage } = userInfo;
            return {
              userId,
              nickname,
              profileImage,
              statusMessage,
            };
          })()}
        />
      </div>
      <div className={styles["mypage__setting"]}>
        <MyPageTab tabName={tabName} setTabName={setTabName} />
        <div className={styles["mypage__contents"]}>
          {
            {
              "내 정보 확인": <MyPageInfo />,
              "비밀번호 변경": <MyPassword />,
              회원탈퇴: <CancelMembership />,
              "다이어리 설정": <MyPageDiary />,
              "카테고리 설정": <MyPageFrame title="카테고리" />,
              "디데이 설정": <MyPageFrame title="디데이" />,
              "팔로워 목록": <MyFriend title="팔로워" />,
              "팔로잉 목록": <MyFriend title="팔로잉" />,
              "친구 검색": <MyFriend title="친구검색" />,
            }[tabName]
          }
        </div>
      </div>
    </div>
  );
};

export default MyPage;
