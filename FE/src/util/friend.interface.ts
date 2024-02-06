// 친구정보
import { FriendInfoConfig } from "./auth.interface";

// 팔로우 목록에서 가능한 친구 프로필 타입
export interface FollowType {
  types: "팔로워 삭제" | "팔로잉 삭제" | "친구 신청" | "팔로워 신청" | "요청" | "취소" | "";
}

// 팔로워 목록
export interface FollowerType extends FriendInfoConfig {
  followId: number;
  followerId: number;
  isFollow: "취소" | "팔로워 삭제" | "팔로워 신청";
}

// 팔로잉 목록
export interface FollowingType extends FriendInfoConfig {
  followId: number;
  followingId: number;
}

// 팔로우 요청 목록
export interface FollowRequestType extends FriendInfoConfig {
  followRequestId: number;
  requesterId: number;
}

// 친구 검색
export interface FriendSearchType extends FriendInfoConfig {
  userId: number;
  isFollow: "친구 신청" | "팔로잉 삭제" | "취소" | "";
}

export interface FriendSearchResponse extends FriendInfoConfig {
  userId: number;
  isFollow: "EMPTY" | "FOLLOW" | "REQUESTED";
}
