import { followRequestType, followerType, followingType, friendSearchType } from "@util/friend.interface";

// followId : 테이블 ID, followerId : 친구 Id
export const followerListData: followerType[] = [
  {
    followId: 1,
    followerId: 1,
    email: "ribbonE@todo.mate",
    nickname: "ribbonE",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/ad4b287b-3d15-4756-a476-06bab40cefaa",
    statusMessage: "방가방가",
    isfollow: "취소",
  },
  {
    followId: 2,
    followerId: 2,
    email: "토롱이@todo.mate",
    nickname: "토롱이",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/d4ee7bff-299f-49de-a372-91b7a15076d2",
    statusMessage: "인생은 생각하는대로 흘러간다.",
    isfollow: "삭제",
  },
  {
    followId: 3,
    followerId: 3,
    email: "곰돌이@todo.mate",
    nickname: "곰돌이 푸",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/56212d87-c2e8-44c8-ac40-53e3a0b55e47",
    statusMessage: "오늘 점심은 꿀이다",
    isfollow: "팔로워 신청",
  },
  {
    followId: 4,
    followerId: 1,
    email: "ribbonE@todo.mate",
    nickname: "ribbonE",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/ad4b287b-3d15-4756-a476-06bab40cefaa",
    statusMessage: "방가방가",
    isfollow: "취소",
  },
  {
    followId: 5,
    followerId: 2,
    email: "토롱이@todo.mate",
    nickname: "토롱이",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/d4ee7bff-299f-49de-a372-91b7a15076d2",
    statusMessage: "인생은 생각하는대로 흘러간다.",
    isfollow: "삭제",
  },
  {
    followId: 6,
    followerId: 3,
    email: "곰돌이@todo.mate",
    nickname: "곰돌이 푸",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/56212d87-c2e8-44c8-ac40-53e3a0b55e47",
    statusMessage: "오늘 점심은 꿀이다",
    isfollow: "팔로워 신청",
  },
];

export const followingListData: followingType[] = [
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

export const followRequestData: followRequestType[] = [
  {
    followRequestId: 1,
    email: "mung@todo.mate",
    nickname: "mung",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/9e66fca0-45bd-4708-8ea7-0dc5baca0acf",
    statusMessage: "멍멍이는 멍멍하지",
    receiveId: 5,
  },
  {
    followRequestId: 2,
    email: "냥냥@todo.mate",
    nickname: "냥냥",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/dbb27de8-a9ce-4fc3-a495-302a6b62c34e",
    statusMessage: "냥냥이은 냥냥하다",
    receiveId: 6,
  },
  {
    followRequestId: 3,
    email: "세계@todo.mate",
    nickname: "세계평화",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/4effef28-b7ea-4c77-90bb-6c570c4c66cd",
    statusMessage: "세계 정복을 목표로 공부하기",
    receiveId: 7,
  },
];

export const FriendSearch: friendSearchType[] = [
  {
    userId: 1,
    email: "ribbonE@todo.mate",
    nickname: "ribbonE",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/ad4b287b-3d15-4756-a476-06bab40cefaa",
    statusMessage: "방가방가",
    isfollow: "취소",
  },
  {
    userId: 2,
    email: "토롱이@todo.mate",
    nickname: "토롱이",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/d4ee7bff-299f-49de-a372-91b7a15076d2",
    statusMessage: "인생은 생각하는대로 흘러간다.",
    isfollow: "삭제",
  },
  {
    userId: 3,
    email: "곰돌이@todo.mate",
    nickname: "곰돌이 푸",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/56212d87-c2e8-44c8-ac40-53e3a0b55e47",
    statusMessage: "오늘 점심은 꿀이다",
    isfollow: "팔로워 신청",
  },
  {
    userId: 1,
    email: "mung@todo.mate",
    nickname: "mung",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/9e66fca0-45bd-4708-8ea7-0dc5baca0acf",
    statusMessage: "멍멍이는 멍멍하지",
    isfollow: "취소",
  },
  {
    userId: 2,
    email: "냥냥@todo.mate",
    nickname: "냥냥",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/dbb27de8-a9ce-4fc3-a495-302a6b62c34e",
    statusMessage: "냥냥이은 냥냥하다",
    isfollow: "삭제",
  },
  {
    userId: 3,
    email: "세계@todo.mate",
    nickname: "세계평화",
    profileImage: "https://github.com/NewSainTurtle/ShadowMate/assets/83412032/4effef28-b7ea-4c77-90bb-6c570c4c66cd",
    statusMessage: "세계 정복을 목표로 공부하기",
    isfollow: "팔로워 신청",
  },
];
