// 친구정보
interface friendInfo {
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
}

// 팔로잉 목록(내가 받은 모든)
export interface following extends friendInfo {
  followId: number;
  followerId: number;
}

// 팔로워 목록(내가 신청한 모든)
export interface follower extends following {
  isfollow: boolean;
}

// 팔로우 요청 목록(내가 신청한 사람만)
export interface followSendType extends friendInfo {
  followRequestId: number;
}

// 팔로우 받은 목록(나한테 친구 신청했는데 승락 안 한 사람..?)
export interface followGetType extends followSendType {
  receiveId: number;
}
