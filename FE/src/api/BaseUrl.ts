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
    nickname: () => string; // 닉네임 중복검사, 삭제
    emailAuthentication: () => string; // 이메일 인증 (+중복검사)
    emailAuthenticationCheck: () => string; // 이메일 인증 - 응답
  };
  users: {
    getProfiles: (userId: number) => string; // 프로필 조회
    myPages: (userId: number) => string; // 내 정보 설정
    password: (userId: number) => string; // 비밀번호 수정
    userOut: (userId: number) => string; // 회원 탈퇴
    searches: (userId: number) => string; // 회원 검색
  };
  follow: {
    following: (userId: number) => string; // 팔로잉 조회, 삭제
    followers: (userId: number) => string; // 팔로워 조회, 삭제
    requested: (userId: number) => string; // 팔로우 신청, 취소
    receive: (userId: number) => string; // 팔로우 신청 수락, 거절
    receiveList: (userId: number) => string; // 받은 팔로우 신청 목록(친구신청 목록)
    count: (userId: number) => string;
  };
  planners: {
    calendars: (userId: number) => string; // 캘린더 조회
    weekly: (userId: number) => string; // 주간 플래너 조회
    weeklyTodos: (userId: number) => string; // 주별 할일 등록, 수정, 삭제
    weeklyTodosStatus: (userId: number) => string; // 주차별 할일 상태 수정
    daily: (userId: number) => string; // 일일 플래너 조회
    dailyTodos: (userId: number) => string; // 일별 할일 등록, 수정, 삭제
    likes: (userId: number) => string; // 좋아요 누르기, 취소
    timetables: (userId: number) => string; // 타임 테이블 표시, 삭제
    retrospections: (userId: number) => string; // 오늘의 회고 편집
    todayGoals: (userId: number) => string; // 오늘의 다짐 편집
    tomorrowGoals: (userId: number) => string; // 내일의 다짐 편집
    retrospectionImages: (userId: number) => string; // 오늘의 회고 사진 업로드
    social: (userId: number) => string; // 소셜 공유, 재공유
  };
  setting: {
    accessScopes: (userId: number) => string; // 플래너 공개 여부
    categories: (userId: number) => string; // 카테고리 목록 조회, 등록, 수정, 삭제
    categoriesColors: (userId: number) => string; // 카테고리 색상 목록 조회
    ddays: (userId: number) => string; // 디데이 목록 조회, 등록, 수정, 삭제
  };
  social: {
    getSocial: (userId: number) => string; // 공개된 플래너 조회
    searches: (userId: number) => string; // 닉네임 검색
    delete: (userId: number, socialId: number) => string; // 내가 공유한 소셜 플래너 삭제
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
    getProfiles: (userId: number) => HOST + USERS + userId + "/profiles",
    myPages: (userId: number) => HOST + USERS + userId + "/mypages",
    password: (userId: number) => HOST + USERS + userId + "/password",
    userOut: (userId: number) => HOST + USERS + userId,
    searches: (userId: number) => HOST + USERS + userId + "/searches",
  },
  follow: {
    following: (userId: number) => HOST + FOLLOW + userId + "/following",
    followers: (userId: number) => HOST + FOLLOW + userId + "/followers",
    requested: (userId: number) => HOST + FOLLOW + userId + "/requested",
    receive: (userId: number) => HOST + FOLLOW + userId + "/receive",
    receiveList: (userId: number) => HOST + FOLLOW + userId + "/receive-lists",
    count: (userId: number) => HOST + FOLLOW + userId + "/counts",
  },
  planners: {
    calendars: (userId: number) => HOST + PLANNERS + userId + "/calendars",
    weekly: (userId: number) => HOST + PLANNERS + userId + "/weekly",
    weeklyTodos: (userId: number) => HOST + PLANNERS + userId + "/weekly" + "/todos",
    weeklyTodosStatus: (userId: number) => HOST + PLANNERS + userId + "/weekly" + "/todos-status",
    daily: (userId: number) => HOST + PLANNERS + userId + "/daily",
    dailyTodos: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/todos",
    likes: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/likes",
    timetables: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/timetables",
    retrospections: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/retrospections",
    todayGoals: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/today-goals",
    tomorrowGoals: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/tomorrow-goals",
    retrospectionImages: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/retrospection-images",
    social: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/social",
  },
  setting: {
    accessScopes: (userId: number) => HOST + SETTINGS + userId + "/access-scopes",
    categories: (userId: number) => HOST + SETTINGS + userId + "/categories",
    categoriesColors: (userId: number) => HOST + SETTINGS + userId + "/categories" + "/colors",
    ddays: (userId: number) => HOST + SETTINGS + userId + "/d-days",
  },
  social: {
    getSocial: (userId: number) => HOST + SOCIAL + userId,
    searches: (userId: number) => HOST + SOCIAL + userId + "/searches" + "/nicknames",
    delete: (userId: number, socialId: number) => HOST + SOCIAL + userId + "/" + socialId,
  },
};

export default api;
