// 팔로우 신청한 간단 이미지
export const followSendImgs: string[] = [
  "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/ad4b287b-3d15-4756-a476-06bab40cefaa",
  "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/d4ee7bff-299f-49de-a372-91b7a15076d2",
  "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/56212d87-c2e8-44c8-ac40-53e3a0b55e47",
];

// 팔로우 요청 받은 간단 이미지
export const followGetImgs: string[] = [
  "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/9e66fca0-45bd-4708-8ea7-0dc5baca0acf",
  "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/dbb27de8-a9ce-4fc3-a495-302a6b62c34e",
  "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/4effef28-b7ea-4c77-90bb-6c570c4c66cd",
];

// followId : 테이블 ID, followerId : 친구 Id
export const followerListData = [
  {
    followId: 1,
    followerId: 1,
    email: "ribbonE@todo.mate",
    nickname: "ribbonE",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/ad4b287b-3d15-4756-a476-06bab40cefaa",
    statusMessage: "방가방가",
    isfollow: false,
  },
  {
    followId: 2,
    followerId: 2,
    email: "토롱이@todo.mate",
    nickname: "토롱이",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/d4ee7bff-299f-49de-a372-91b7a15076d2",
    statusMessage: "인생은 생각하는대로 흘러간다.",
    isfollow: false,
  },
  {
    followId: 3,
    followerId: 3,
    email: "곰돌이@todo.mate",
    nickname: "곰돌이 푸",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/56212d87-c2e8-44c8-ac40-53e3a0b55e47",
    statusMessage: "오늘 점심은 꿀이다",
    isfollow: true,
  },
];

// 내가 받은 리스트
export const followingListData = [
  {
    followId: 1,
    followerId: 5,
    email: "mung@todo.mate",
    nickname: "mung",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/9e66fca0-45bd-4708-8ea7-0dc5baca0acf",
    statusMessage: "멍멍이는 멍멍하지",
  },
  {
    followId: 2,
    followerId: 6,
    email: "냥냥@todo.mate",
    nickname: "냥냥",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/dbb27de8-a9ce-4fc3-a495-302a6b62c34e",
    statusMessage: "냥냥이은 냥냥하다",
  },
  {
    followId: 3,
    followerId: 7,
    email: "세계@todo.mate",
    nickname: "세계평화",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/4effef28-b7ea-4c77-90bb-6c570c4c66cd",
    statusMessage: "세계 정복을 목표로 공부하기",
  },
];
