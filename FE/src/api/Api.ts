import Axios from "@api/JsonAxios";
import api from "@api/BaseUrl";

export const authApi = {
  join: (data: { email: string; password: string; nickname: string }) => Axios.post(api.auth.join(), data),
  login: (data: { email: string; password: string }) => Axios.post(api.auth.login(), data),
  googleLogin: () => Axios.post(api.auth.googleLogin()),
  nickname: (data: { nickname: string }) => Axios.post(api.auth.nickname(), data),
  emailAuthentication: (data: { email: string }) => Axios.post(api.auth.emailAuthentication(), data),
  emailAuthenticationCheck: (data: { email: string; code: string }) =>
    Axios.post(api.auth.emailAuthenticationCheck(), data),
};

export const userApi = {
  getProfiles: (userId: string) => Axios.get(api.users.getProfiles(userId)),
  myPage: (userId: string, data: { nickname: string; statusMessage: string }) =>
    Axios.put(api.users.myPage(userId), data),
  password: (userId: string, data: { oldPassword: string; newPassword: string }) =>
    Axios.post(api.users.password(userId), data),
  userOut: (userId: string) => Axios.delete(api.users.userOut(userId)),
  editProfileImg: (userId: string, data: { newProfileImage: string }) =>
    Axios.put(api.users.editProfileImg(userId), data),
  deleteProfileImg: (userId: string) => Axios.delete(api.users.editProfileImg(userId)),
};

export const followApi = {
  getFollowing: (userId: string) => Axios.get(api.follow.following(userId)),
  deleteFollowing: (userId: string, data: { followingId: number }) =>
    Axios.delete(api.follow.following(userId), { data: data }),
  getFollwers: (userId: string) => Axios.get(api.follow.followers(userId)),
  deleteFollows: (userId: string, data: { followingId: number }) =>
    Axios.delete(api.follow.followers(userId), { data: data }),
  searches: (userId: string, params: { nickname: string }) =>
    Axios.post(api.follow.searches(userId), {}, { params: params }),
  addRequested: (userId: string, data: { followingId: number }) => Axios.post(api.follow.requested(userId), data),
  canelRequested: (userId: string, data: { followingId: number }) =>
    Axios.delete(api.follow.requested(userId), { data: data }),
  receive: (userId: string, data: { requesertId: number; followReceive: boolean }) =>
    Axios.post(api.follow.receive(userId), data),
  receiveList: (userId: string) => Axios.get(api.follow.receiveList(userId)),
};

export const plannerApi = {
  calendars: (userId: string, params: { date: string }) =>
    Axios.get(api.planners.calendars(userId), { params: params }),

  weekly: (userId: string, params: { "start-date": string; "end-date": string }) =>
    Axios.get(api.planners.weekly(userId), { params: params }),
  addWeeklyTodos: (userId: string, data: { startDate: number; endDate: string; weeklyTodoContent: string }) =>
    Axios.post(api.planners.weeklyTodos(userId), data),
  editWeeklyTodos: (
    userId: string,
    data: { startDate: number; endDate: string; weeklyTodoId: number; weeklyTodoContent: string },
  ) => Axios.put(api.planners.weeklyTodos(userId), data),
  deleteWeeklyTodos: (userId: string, data: { startDate: number; endDate: string; weeklyTodoId: number }) =>
    Axios.delete(api.planners.weeklyTodos(userId), { data: data }),
  weeklyTodosStatus: (
    userId: string,
    data: { startDate: number; endDate: string; weeklyTodoId: number; weeklyTodoStatus: boolean },
  ) => Axios.put(api.planners.weeklyTodosStatus(userId), data),

  daily: (userId: string, params: { date: string }) => Axios.get(api.planners.daily(userId), { params: params }),
  likes: (userId: string, data: { date: string; anotherUserId: number }) =>
    Axios.post(api.planners.likes(userId), data),
  cancleLikes: (userId: string, data: { date: string }) => Axios.delete(api.planners.likes(userId), { data: data }),
  addDailyTodos: (userId: string, data: { date: string; todoContent: string; categoryId: number }) =>
    Axios.post(api.planners.dailyTodos(userId), data),
  editDailyTodos: (
    userId: string,
    data: {
      date: string;
      todoId: number;
      todoContent: string;
      categoryId: number;
      todoStatus: "공백" | "완료" | "미완료";
    },
  ) => Axios.put(api.planners.dailyTodos(userId), data),
  deleteDailyTodos: (userId: string, data: { date: string; todoId: number }) =>
    Axios.delete(api.planners.dailyTodos(userId), { data: data }),

  timetables: (userId: string, data: { date: string; todoId: number; startTime: string; endTime: string }) =>
    Axios.post(api.planners.timetables(userId), data),
  deleteTimetable: (userId: string, data: { date: string; todoId: number }) =>
    Axios.delete(api.planners.timetables(userId), { data: data }),
  retrospections: (userId: string, data: { date: string; retrospection: string }) =>
    Axios.put(api.planners.retrospections(userId), data),
  todayGoals: (userId: string, data: { date: string; todayGoal: string }) =>
    Axios.put(api.planners.todayGoals(userId), data),
  tomorrowGoals: (userId: string, data: { date: string; tomorrowGoal: string }) =>
    Axios.put(api.planners.tomorrowGoals(userId), data),
  retrospectionImages: (userId: string, data: { date: string; retrospectionImage: string | null }) =>
    Axios.put(api.planners.retrospectionImages(userId), data),
  social: (userId: string, data: { date: string; socialImage: string }) =>
    Axios.post(api.planners.social(userId), data),
};

export const settingApi = {
  accessScopes: (userId: string, data: { plannerAccessScope: "전체공개" | "친구공개" | "비공개" }) =>
    Axios.put(api.setting.accessScopes(userId), data),

  categories: (userId: string) => Axios.get(api.setting.categories(userId)),
  categoriesColors: (userId: string) => Axios.get(api.setting.categoriesColors(userId)),
  addCategories: (
    userId: string,
    data: { categoryTitle: string; categoryColorId: number; categoryEmoticon: string | null },
  ) => Axios.post(api.setting.categories(userId), data),
  editCategories: (
    userId: string,
    data: { categoryId: number; categoryTitle: string; categoryColorId: number; categoryEmoticon: string | null },
  ) => Axios.put(api.setting.categories(userId), data),
  deleteCategories: (userId: string, data: { categoryId: number }) =>
    Axios.delete(api.setting.categories(userId), { data: data }),

  ddays: (userId: string) => Axios.get(api.setting.ddays(userId)),
  addDdays: (userId: string, data: { ddayDate: string; ddayTime: string }) =>
    Axios.post(api.setting.ddays(userId), data),
  editDdays: (userId: string, data: { ddayId: number; ddayDate: string; ddayTime: string }) =>
    Axios.put(api.setting.ddays(userId), data),
  deleteDdays: (userId: string, data: { ddayId: number }) => Axios.delete(api.setting.ddays(userId), { data: data }),
};

export const socialApi = {
  getSocial: (params: { sort: "latest" | "popularity"; pageNumber: number }) =>
    Axios.get(api.social.getSocial(), { params: params }),
  searches: (data: { nickname: string; sort: "latest" | "popularity"; pageNumber: number }) =>
    Axios.post(api.social.searches(), data),
  delete: (socialId: string) => Axios.delete(api.social.delete(socialId)),
};
