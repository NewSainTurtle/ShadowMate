import React, { useState } from "react";
import styles from "@components/mypage/MyPage.module.scss";
import Profile from "@components/common/Profile";
import { profileInfo } from "./commonPage";
import MyPageTab from "@components/mypage/MyPageTab";
import MyPageDiary from "@components/mypage/details/MyPageDiary";
import MyPageFrame from "@components/mypage/MyPageFrame";
import MyPageInfo from "@components/mypage/details/MyPageInfo";
import MyPageFriend from "@components/mypage/details/MyPageFriend";

const MyPage = () => {
  const [tabName, setTabName] = useState<string>("info");

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
              info: <MyPageInfo />,
              diary: <MyPageDiary />,
              category: <MyPageFrame title="카테고리" />,
              dday: <MyPageFrame title="디데이" />,
              friend: <MyPageFriend />,
            }[tabName]
          }
        </div>
      </div>
    </div>
  );
};

export default MyPage;
