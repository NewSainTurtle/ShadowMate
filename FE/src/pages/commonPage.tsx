import React, { useState } from "react";
import Text from "@components/common/Text";
import Button from "@components/common/Button";
import Dday from "@components/common/Dday";
import Input from "@components/common/Input";
import FriendProfile, { ProfileConfig } from "@components/common/FriendProfile";
import Profile from "@components/common/Profile";

export const profileInfo: ProfileConfig = {
  userId: 0,
  nickname: "ribbonE",
  message: "방가방가",
  src: "https://avatars.githubusercontent.com/u/85155789?v=4",
};

const commonPage = () => {
  return (
    <>
      <div>
        <Text types="small">프리텐다드 - small</Text>
        <br /> <br />
        <Text types="default">프리텐다드 - default</Text>
        <br /> <br />
        <Text types="semi-medium">프리텐다드 - semi-medium</Text>
        <br /> <br />
        <Text types="medium">프리텐다드 - medium</Text>
        <br /> <br />
        <Text types="semi-large">프리텐다드 - semi-large </Text>
        <br /> <br />
        <Text types="large">프리텐다드 - large</Text>
      </div>
      <div>
        <Button types="blue">소설공유</Button>
        <Button children="♥ 50" types="red" />
        <Button children="일간 보기" types="gray" />
        <Button children="로그아웃" types="gray" />
      </div>
      <div>
        <Dday nearDate={new Date()} comparedDate={new Date()} />
      </div>
      <br />
      <div style={{ width: "25em", display: "flex", flexDirection: "column" }}>
        <Input placeholder="이메일" />
        <Input types="password" placeholder="비밀번호" />
        <Input types="search" placeholder="사용자 닉네임으로 검색" />
      </div>
      <br />
      <div>
        <FriendProfile types="기본" profile={profileInfo} />
        <FriendProfile types="아이콘" profile={profileInfo} />
        <FriendProfile types="삭제" profile={profileInfo} />
        <FriendProfile types="친구 신청" profile={profileInfo} />
        <FriendProfile types="팔로워 신청" profile={profileInfo} />
        <FriendProfile types="요청" profile={profileInfo} />
        <FriendProfile types="취소" profile={profileInfo} />
      </div>
      <br />
      <div>
        <Profile types="기본" profile={profileInfo} />
        <br />
        <Profile types="로그아웃" profile={profileInfo} />
      </div>
    </>
  );
};

export default commonPage;
