// 기본 url
const HOST = process.env.REACT_APP_API_URL + "/api/";

// 세부 url
const AUTH = "auth/";
const USER = "user/";

interface apiInterface {
  auth: {
    join: () => string; // 회원가입
    login: () => string; // 로그인
    logout: () => string; //로그아웃
    googleLogin: () => string; // 구글 로그인
    nickname: () => string; // 닉네임 중복검사
    duplicatedCheck: () => string; // 이메일 인증 (+중복검사), 응답
    token: () => string; // 토큰 확인
  };
  user: {
    getProfiles: (userId: string) => string; // 프로필 조회
  };
}

const api: apiInterface = {
  auth: {
    join: () => HOST + AUTH + "join",
    login: () => HOST + AUTH + "login",
    logout: () => HOST + AUTH + "logout",
    googleLogin: () => HOST + AUTH + "social-login",
    nickname: () => HOST + AUTH + "nickname-duplicated",
    duplicatedCheck: () => HOST + AUTH + "email-certificated",
    token: () => HOST + AUTH + "token",
  },
  user: {
    getProfiles: (userId: string) => HOST + USER + { userId } + "/profiles",
  },
};

export default api;
