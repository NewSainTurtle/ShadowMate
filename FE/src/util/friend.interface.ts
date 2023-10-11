// 친구정보
interface friendInfo {
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
}

interface isfollow {
  isfollow: "취소" | "삭제" | "팔로워신청";
}

// 팔로워 목록
export interface followerType extends friendInfo, isfollow {
  followId: number;
  followerId: number;
}

// 팔로잉 목록
export interface followingType extends friendInfo {
  followId: number;
  followerId: number;
}

// 팔로우 요청 목록
export interface followRequestType extends friendInfo {
  followRequestId: number;
  receiveId: number;
}

// 친구 검색

export interface friendSearchType extends friendInfo, isfollow {
  userId: number;
}
