// 기본 url
const HOST = process.env.REACT_APP_API_URL + "/api/";

// 세부 url
const AUTH = "auth/";
const USERS = "users/";
const FOLLOW = "follow/";
const PLANNERS = "planners/";
const SETTINGS = "planner-settings/";
const SOCIAL = "social/";

const api = {
  auth: {
    join: () => HOST + AUTH + "join", // 회원가입
    login: () => HOST + AUTH + "login", // 로그인
    autoLogin: () => HOST + AUTH + "auto-login", // 자동로그인
    googleLogin: () => HOST + AUTH + "social-login", // 구글 로그인
    logout: () => HOST + AUTH + "logout", // 로그아웃
    nickname: () => HOST + AUTH + "nickname-duplicated", // 닉네임 중복검사, 삭제
    emailAuthentication: () => HOST + AUTH + "email-authentication", // 이메일 인증 (+중복검사)
    emailAuthenticationCheck: () => HOST + AUTH + "email-authentication" + "/check", // 이메일 인증 - 응답
    token: (userId: number) => HOST + AUTH + "token/" + userId, // 토큰 재발급
  },
  users: {
    getProfiles: (userId: number) => HOST + USERS + userId + "/profiles", // 프로필 조회
    myPages: (userId: number) => HOST + USERS + userId + "/mypages", // 내 정보 설정
    password: (userId: number) => HOST + USERS + userId + "/password", // 비밀번호 수정
    userOut: (userId: number) => HOST + USERS + userId, // 회원 탈퇴
    searches: (userId: number) => HOST + USERS + userId + "/searches", // 회원 검색
    introduction: (userId: number) => HOST + USERS + userId + "/introduction", // 소개글 수정
  },
  follow: {
    following: (userId: number) => HOST + FOLLOW + userId + "/following", // 팔로잉 조회
    followers: (userId: number) => HOST + FOLLOW + userId + "/followers", // 팔로워 조회
    requested: (userId: number) => HOST + FOLLOW + userId + "/requested", // 팔로우 신청, 취소
    receive: (userId: number) => HOST + FOLLOW + userId + "/receive", // 팔로우 신청 수락, 거절
    receiveList: (userId: number) => HOST + FOLLOW + userId + "/receive-lists", // 받은 팔로우 신청 목록(친구신청 목록)
    count: (userId: number) => HOST + FOLLOW + userId + "/counts", // 팔로우 수 조회
  },
  planners: {
    calendars: (userId: number) => HOST + PLANNERS + userId + "/calendars", // 캘린더 조회
    guestBook: (userId: number) => HOST + PLANNERS + userId + "/monthly" + "/visitor-books", // 방명록 조회, 등록, 삭제
    weekly: (userId: number) => HOST + PLANNERS + userId + "/weekly", // 주간 플래너 조회
    weeklyTodos: (userId: number) => HOST + PLANNERS + userId + "/weekly" + "/todos", // 주별 할일 등록, 수정, 삭제
    weeklyTodosStatus: (userId: number) => HOST + PLANNERS + userId + "/weekly" + "/todos-status", // 주차별 할일 상태 수정
    daily: (userId: number) => HOST + PLANNERS + userId + "/daily", // 일일 플래너 조회
    dailyTodos: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/todos", // 일별 할일 등록, 수정, 삭제
    dailyTodoSequence: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/todo-sequence", // 일별 할일 순서 변경
    likes: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/likes", // 좋아요 누르기, 취소
    timetables: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/timetables", // 타임 테이블 표시, 삭제
    retrospections: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/retrospections", // 오늘의 회고 편집
    todayGoals: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/today-goals", // 오늘의 다짐 편집
    tomorrowGoals: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/tomorrow-goals", // 내일의 다짐 편집
    retrospectionImages: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/retrospection-images", // 오늘의 회고 사진 업로드
    social: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/social", // 소셜 공유, 재공유
  },
  setting: {
    accessScopes: (userId: number) => HOST + SETTINGS + userId + "/access-scopes", // 플래너 공개 여부
    categories: (userId: number) => HOST + SETTINGS + userId + "/categories", // 카테고리 목록 조회, 등록, 수정, 삭제
    categoriesColors: (userId: number) => HOST + SETTINGS + userId + "/categories" + "/colors", // 카테고리 색상 목록 조회
    ddays: (userId: number) => HOST + SETTINGS + userId + "/d-days", // 디데이 목록 조회, 등록, 수정, 삭제
    routines: (userId: number) => HOST + SETTINGS + userId + "/routines", // 루틴 조회, 등록, 수정, 삭제
  },
  social: {
    getSocial: (userId: number) => HOST + SOCIAL + userId, // 공개된 플래너 조회
    searches: (userId: number) => HOST + SOCIAL + userId + "/searches" + "/nicknames", // 닉네임 검색
    delete: (userId: number, socialId: number) => HOST + SOCIAL + userId + "/" + socialId, // 내가 공유한 소셜 플래너 삭제
  },
};

export default api;
