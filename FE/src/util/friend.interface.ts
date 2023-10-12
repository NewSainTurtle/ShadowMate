// 친구정보
interface friendInfo {
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
}

interface isFollow {
  isFollow: "취소" | "삭제" | "팔로워 신청";
}

// 팔로워 목록
export interface followerType extends friendInfo, isFollow {
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
export interface friendSearchType extends friendInfo, isFollow {
  userId: number;
}
