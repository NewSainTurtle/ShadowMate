import React, { useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Profile from "@components/common/Profile";
import { profileInfo } from "./commonPage";
import MyPageTab from "@components/mypage/MyPageTab";
import MyPageDiary from "@components/mypage/details/diary/Diary";
import MyPageFrame from "@components/mypage/MyPageFrame";
import MyPageInfo from "@components/mypage/details/myInfo/MyInfo";
import MyFriend from "@components/mypage/details/friend/MyFriend";
import { settingApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { setCategoryColors, setCategoryInput, setCategoryList } from "@store/mypageSlice";
import { CategoryConfig } from "@util/planner.interface";

const MyPage = () => {
  const dispatch = useAppDispatch();
  const [tabName, setTabName] = useState<string>("내 정보 확인");
  const userId: number = useAppSelector(selectUserId);

  const getCategoryList = () => {
    settingApi
      .categories(userId)
      .then((res) => {
        let response: CategoryConfig[] = res.data.data.categoryList;
        if (response.length != 0) {
          dispatch(setCategoryList(response));
          dispatch(setCategoryInput(response[0]));
        }
      })
      .catch((err) => console.log(err));
  };

  const getCategoryColors = () => {
    settingApi
      .categoriesColors(userId)
      .then((res) => {
        const response = res.data.data.categoryColorList;
        dispatch(setCategoryColors(response));
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    getCategoryList();
    getCategoryColors();
  }, []);

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
