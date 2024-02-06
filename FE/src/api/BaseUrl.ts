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
    join: () => HOST + AUTH + "join",
    login: () => HOST + AUTH + "login",
    autoLogin: () => HOST + AUTH + "auto-login",
    googleLogin: () => HOST + AUTH + "social-login",
    logout: () => HOST + AUTH + "logout",
    nickname: () => HOST + AUTH + "nickname-duplicated",
    emailAuthentication: () => HOST + AUTH + "email-authentication",
    emailAuthenticationCheck: () => HOST + AUTH + "email-authentication" + "/check",
    token: (userId: number) => HOST + AUTH + "token/" + userId,
  },
  users: {
    getProfiles: (userId: number) => HOST + USERS + userId + "/profiles",
    myPages: (userId: number) => HOST + USERS + userId + "/mypages",
    password: (userId: number) => HOST + USERS + userId + "/password",
    userOut: (userId: number) => HOST + USERS + userId,
    searches: (userId: number) => HOST + USERS + userId + "/searches",
    introduction: (userId: number) => HOST + USERS + userId + "/introduction",
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
    guestBook: (userId: number) => HOST + PLANNERS + userId + "/monthly" + "/visitor-books",
    weekly: (userId: number) => HOST + PLANNERS + userId + "/weekly",
    weeklyTodos: (userId: number) => HOST + PLANNERS + userId + "/weekly" + "/todos",
    weeklyTodosStatus: (userId: number) => HOST + PLANNERS + userId + "/weekly" + "/todos-status",
    daily: (userId: number) => HOST + PLANNERS + userId + "/daily",
    dailyTodos: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/todos",
    dailyTodoSequence: (userId: number) => HOST + PLANNERS + userId + "/daily" + "/todo-sequence",
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
    routines: (userId: number) => HOST + SETTINGS + userId + "/routines",
  },
  social: {
    getSocial: (userId: number) => HOST + SOCIAL + userId,
    searches: (userId: number) => HOST + SOCIAL + userId + "/searches" + "/nicknames",
    delete: (userId: number, socialId: number) => HOST + SOCIAL + userId + "/" + socialId,
  },
};

export default api;
