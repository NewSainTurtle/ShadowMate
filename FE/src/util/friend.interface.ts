// 친구정보
export interface friendInfo {
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
}

// 팔로우 목록에서 가능한 친구 프로필 타입
export interface followType {
  types: "팔로워 삭제" | "팔로잉 삭제" | "친구 신청" | "팔로워 신청" | "요청" | "취소";
}

// 팔로워 목록
export interface followerType extends friendInfo {
  followId: number;
  followerId: number;
  isFollow: "취소" | "팔로워 삭제" | "팔로워 신청";
}

// 팔로잉 목록
export interface followingType extends friendInfo {
  followId: number;
  followingId: number;
}

// 팔로우 요청 목록
export interface followRequestType extends friendInfo {
  followRequestId: number;
  requesterId: number;
}

// 친구 검색
export interface friendSearchType extends friendInfo {
  userId: number;
  isFollow: "친구 신청" | "팔로잉 삭제" | "취소";
}
