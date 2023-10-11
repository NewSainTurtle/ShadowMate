import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Profile from "@components/common/Profile";
import { profileInfo } from "./commonPage";
import MyPageTab from "@components/mypage/MyPageTab";
import MyPageDiary from "@components/mypage/details/diary/Diary";
import MyPageFrame from "@components/mypage/MyPageFrame";
import MyPageInfo from "@components/mypage/details/myInfo/MyInfo";
import Following from "@components/mypage/details/friend/Following";
import Follower from "@components/mypage/details/friend/Follower";
import Search from "@components/mypage/details/friend/Search";
import MyFriend from "@components/mypage/details/friend/MyFriend";

const MyPage = () => {
  const [tabName, setTabName] = useState<string>("내 정보 확인");

  return (
    <div className={styles["mypage__container"]}>
      <div className={styles["mypage__profile"]}>
        <Profile types="로그아웃" profile={profileInfo} />
      </div>
      <div className={styles["mypage__setting"]}>
        <MyPageTab setTabName={setTabName} />
        <div className={styles["mypage__contents"]}>
          {
            {
              "내 정보 확인": <MyPageInfo />,
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
