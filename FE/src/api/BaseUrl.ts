// 기본 url
const HOST = process.env.REACT_APP_API_URL + "/api/";

// 세부 url
const AUTH = "auth/";
const USERS = "users/";
const FOLLOW = "follow/";
const PLANNERS = "planners/";
const SETTINGS = "planner-settings/";
const SOCIAL = "social/";

interface apiInterface {
  auth: {
    join: () => string; // 회원가입
    login: () => string; // 로그인
    googleLogin: () => string; // 구글 로그인
    nickname: () => string; // 닉네임 중복검사
    emailAuthentication: () => string; // 이메일 인증 (+중복검사)
    emailAuthenticationCheck: () => string; // 이메일 인증 - 응답
  };
  users: {
    getProfiles: (userId: string) => string; // 프로필 조회
    myPage: (userId: string) => string; // 내 정보 설정
    password: (userId: string) => string; // 비밀번호 수정
    userOut: (userId: string) => string; // 회원 탈퇴
    editProfileImg: (userId: string) => string; // 프로필 사진 등록, 수정
  };
  follow: {
    following: (userId: string) => string; // 팔로잉 조회, 삭제
    followers: (userId: string) => string; // 팔로워 조회, 삭제
    searches: (userId: string) => string; // 회원 검색
    requested: (userId: string) => string; // 팔로우 신청, 취소
    receive: (userId: string) => string; // 팔로우 신청 수락, 거절
    receiveList: (userId: string) => string; // 받은 팔로우 신청 목록(친구신청 목록)
  };
  planners: {
    calendars: (userId: string) => string; // 캘린더 조회
    weekly: (userId: string) => string; // 주간 플래너 조회
    weeklyTodos: (userId: string) => string; // 주별 할일 등록, 수정, 삭제
    weeklyTodosStatus: (userId: string) => string; // 주차별 할일 상태 수정
    daily: (userId: string) => string; // 일일 플래너 조회
    dailyTodos: (userId: string) => string; // 일별 할일 등록, 수정, 삭제
    likes: (userId: string) => string; // 좋아요 누르기, 취소
    timetables: (userId: string) => string; // 타임 테이블 표시, 삭제
    retrospections: (userId: string) => string; // 오늘의 회고 편집
    todayGoals: (userId: string) => string; // 오늘의 다짐 편집
    tomorrowGoals: (userId: string) => string; // 내일의 다짐 편집
    retrospectionImages: (userId: string) => string; // 오늘의 회고 사진 업로드
    social: (userId: string) => string; // 소설 공유 , 재공유
  };
  setting: {
    accessScopes: (userId: string) => string; // 플래너 공개 여부
    categories: (userId: string) => string; // 카테고리 목록 조회, 등록, 수정, 삭제
    categoriesColors: (userId: string) => string; // 카테고리 색상 목록 조회
    ddays: (userId: string) => string; // 디데이 목록 조회, 등록, 수정, 삭제
  };
  social: {
    getSocial: () => string; // 공개된 플래너 조회
    searches: () => string; // 닉네임 검색
    delete: (socialId: string) => string; // 내가 공유한 소셜 플래너 삭제
  };
}

const api: apiInterface = {
  auth: {
    join: () => HOST + AUTH + "join",
    login: () => HOST + AUTH + "login",
    googleLogin: () => HOST + AUTH + "social-login",
    nickname: () => HOST + AUTH + "nickname-duplicated",
    emailAuthentication: () => HOST + AUTH + "email-authentication",
    emailAuthenticationCheck: () => HOST + AUTH + "email-authentication" + "/check",
  },
  users: {
    getProfiles: (userId: string) => HOST + USERS + { userId } + "/profiles",
    myPage: (userId: string) => HOST + USERS + { userId } + "/mypages",
    password: (userId: string) => HOST + USERS + { userId } + "/password",
    userOut: (userId: string) => HOST + USERS + { userId },
    editProfileImg: (userId: string) => HOST + USERS + { userId } + "/images",
  },
  follow: {
    following: (userId: string) => HOST + FOLLOW + { userId } + "/following",
    followers: (userId: string) => HOST + FOLLOW + { userId } + "/followers",
    searches: (userId: string) => HOST + FOLLOW + { userId } + "/searches",
    requested: (userId: string) => HOST + FOLLOW + { userId } + "/requested",
    receive: (userId: string) => HOST + FOLLOW + { userId } + "/receive",
    receiveList: (userId: string) => HOST + FOLLOW + { userId } + "/receive-lists",
  },
  planners: {
    calendars: (userId: string) => HOST + PLANNERS + { userId } + "/calendars",
    weekly: (userId: string) => HOST + PLANNERS + { userId } + "/weekly",
    weeklyTodos: (userId: string) => HOST + PLANNERS + { userId } + "/weekly" + "/todos",
    weeklyTodosStatus: (userId: string) => HOST + PLANNERS + { userId } + "/weekly" + "/todos-status",
    daily: (userId: string) => HOST + PLANNERS + { userId } + "/daily",
    dailyTodos: (userId: string) => HOST + PLANNERS + { userId } + "/daily" + "/todos",
    likes: (userId: string) => HOST + PLANNERS + { userId } + "/daily" + "/likes",
    timetables: (userId: string) => HOST + PLANNERS + { userId } + "/daily" + "/timetables",
    retrospections: (userId: string) => HOST + PLANNERS + { userId } + "/daily" + "/retrospections",
    todayGoals: (userId: string) => HOST + PLANNERS + { userId } + "/daily" + "/today-goals",
    tomorrowGoals: (userId: string) => HOST + PLANNERS + { userId } + "/daily" + "/tomorrow-goals",
    retrospectionImages: (userId: string) => HOST + PLANNERS + { userId } + "/daily" + "/retrospection-images",
    social: (userId: string) => HOST + PLANNERS + { userId } + "/daily" + "/social",
  },
  setting: {
    accessScopes: (userId: string) => HOST + SETTINGS + { userId } + "/access-scopes",
    categories: (userId: string) => HOST + SETTINGS + { userId } + "/categories",
    categoriesColors: (userId: string) => HOST + SETTINGS + { userId } + "/categories" + "/colors",
    ddays: (userId: string) => HOST + SETTINGS + { userId } + "/d-days",
  },
  social: {
    getSocial: () => HOST + SOCIAL,
    searches: () => HOST + SOCIAL + "/searches" + "/nicknames",
    delete: (socialId: string) => HOST + SOCIAL + { socialId },
  },
};

export default api;
