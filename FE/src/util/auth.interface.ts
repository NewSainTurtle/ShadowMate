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
  plannerAccessScope: string;
}

export interface ProfileConfig extends UserConfig {
  userId: number;
}
