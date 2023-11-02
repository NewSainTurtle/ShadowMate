// 친구정보
export interface friendInfo {
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
}

// 팔로워 목록
export interface followerType extends friendInfo {
  followId: number;
  followerId: number;
  isFollow: "취소" | "삭제" | "팔로워 신청";
}

// 팔로잉 목록
export interface followingType extends friendInfo {
  followId: number;
  followerId: number;
  isFollow: "삭제";
}

// 팔로우 요청 목록
export interface followRequestType extends friendInfo {
  followRequestId: number;
  receiveId: number;
  isFollow: "요청";
}

// 친구 검색
export interface friendSearchType extends friendInfo {
  userId: number;
  isFollow: "친구 신청" | "취소";
}
