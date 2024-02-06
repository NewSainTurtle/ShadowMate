export interface UserConfig {
  nickname: string;
  profileImage: string;
  statusMessage: string;
}

export interface FriendInfoConfig extends UserConfig {
  email: string;
}

export interface UserInfoConfig extends UserConfig {
  email: string;
  plannerAccessScope: "전체공개" | "친구공개" | "비공개";
}

export interface ProfileConfig extends UserConfig {
  userId: number;
}
