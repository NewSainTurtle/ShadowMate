import Axios from "@api/JsonAxios";
import api from "@api/BaseUrl";
import { MonthConfig } from "@store/planner/monthSlice";

export const authApi = {
  join: (data: { email: string; password: string; nickname: string }) => Axios.post(api.auth.join(), data),
  login: (data: { email: string; password: string }) => Axios.post(api.auth.login(), data),
  googleLogin: () => Axios.post(api.auth.googleLogin()),
  nickname: (data: { nickname: string }) => Axios.post(api.auth.nickname(), data),
  deleteNickname: (data: { nickname: string }) => Axios.delete(api.auth.nickname(), { data }),
  emailAuthentication: (data: { email: string }) => Axios.post(api.auth.emailAuthentication(), data),
  emailAuthenticationCheck: (data: { email: string; code: string }) =>
    Axios.post(api.auth.emailAuthenticationCheck(), data),
};

export const userApi = {
  getProfiles: (userId: number) => Axios.get(api.users.getProfiles(userId)),
  myPages: (userId: number, data: { newNickname: string; newProfileImage: string; newStatusMessage: string }) =>
    Axios.put(api.users.myPages(userId), data),
  password: (userId: number, data: { oldPassword: string; newPassword: string }) =>
    Axios.put(api.users.password(userId), data),
  userOut: (userId: number) => Axios.delete(api.users.userOut(userId)),
  searches: (userId: number, params: { nickname: string }) => Axios.get(api.users.searches(userId), { params }),
  getIntroduction: (userId: number) => Axios.get(api.users.introduction(userId)),
  editIntroduction: (userId: number, data: { introduction: string }) => Axios.put(api.users.introduction(userId), data),
};

export const followApi = {
  getFollowing: (userId: number) => Axios.get(api.follow.following(userId)),
  deleteFollowing: (userId: number, data: { followingId: number }) =>
    Axios.delete(api.follow.following(userId), { data: data }),
  getFollwers: (userId: number) => Axios.get(api.follow.followers(userId)),
  deleteFollowers: (userId: number, data: { followerId: number }) =>
    Axios.delete(api.follow.followers(userId), { data: data }),
  addRequested: (userId: number, data: { followingId: number }) => Axios.post(api.follow.requested(userId), data),
  cancelRequested: (userId: number, data: { receiverId: number }) =>
    Axios.delete(api.follow.requested(userId), { data: data }),
  receive: (userId: number, data: { requesterId: number; followReceive: boolean }) =>
    Axios.post(api.follow.receive(userId), data),
  receiveList: (userId: number) => Axios.get(api.follow.receiveList(userId)),
  getFollowCount: (userId: number) => Axios.get(api.follow.count(userId)),
};

export const plannerApi = {
  calendars: (userId: number, params: { date: string }) =>
    Axios.get(api.planners.calendars(userId), { params: params }),
  getGuestBook: (userId: number, params: { last: number }) =>
    Axios.get(api.planners.guestBook(userId), { params: params }),
  addGuestBook: (userId: number, data: { visitorBookContent: string }) =>
    Axios.post(api.planners.guestBook(userId), data),
  deleteGuestBook: (userId: number, data: { visitorBookId: number }) =>
    Axios.delete(api.planners.guestBook(userId), { data: data }),
  weekly: (userId: number, params: { "start-date": string; "end-date": string }) =>
    Axios.get(api.planners.weekly(userId), { params: params }),
  addWeeklyTodos: (userId: number, data: { startDate: string; endDate: string; weeklyTodoContent: string }) =>
    Axios.post(api.planners.weeklyTodos(userId), data),
  editWeeklyTodos: (
    userId: number,
    data: { startDate: string; endDate: string; weeklyTodoId: number; weeklyTodoContent: string },
  ) => Axios.put(api.planners.weeklyTodos(userId), data),
  deleteWeeklyTodos: (userId: number, data: { startDate: string; endDate: string; weeklyTodoId: number }) =>
    Axios.delete(api.planners.weeklyTodos(userId), { data: data }),
  weeklyTodosStatus: (
    userId: number,
    data: { startDate: string; endDate: string; weeklyTodoId: number; weeklyTodoStatus: boolean },
  ) => Axios.put(api.planners.weeklyTodosStatus(userId), data),

  daily: (userId: number, params: { date: string }) => Axios.get(api.planners.daily(userId), { params: params }),
  likes: (userId: number, data: { date: string; anotherUserId: number }) =>
    Axios.post(api.planners.likes(userId), data),
  cancelLikes: (userId: number, data: { date: string }) => Axios.delete(api.planners.likes(userId), { data: data }),
  addDailyTodos: (userId: number, data: { date: string; todoContent: string; categoryId: number }) =>
    Axios.post(api.planners.dailyTodos(userId), data),
  editDailyTodos: (
    userId: number,
    data: {
      date: string;
      todoId: number;
      todoContent: string;
      categoryId: number;
      todoStatus: "공백" | "완료" | "미완료";
    },
  ) => Axios.put(api.planners.dailyTodos(userId), data),
  deleteDailyTodos: (userId: number, data: { date: string; todoId: number }) =>
    Axios.delete(api.planners.dailyTodos(userId), { data: data }),

  timetables: (userId: number, data: { date: string; todoId: number; startTime: string; endTime: string }) =>
    Axios.post(api.planners.timetables(userId), data),
  deleteTimetable: (userId: number, data: { date: string; todoId: number }) =>
    Axios.delete(api.planners.timetables(userId), { data: data }),
  retrospections: (userId: number, data: { date: string; retrospection: string }) =>
    Axios.put(api.planners.retrospections(userId), data),
  todayGoals: (userId: number, data: { date: string; todayGoal: string }) =>
    Axios.put(api.planners.todayGoals(userId), data),
  tomorrowGoals: (userId: number, data: { date: string; tomorrowGoal: string }) =>
    Axios.put(api.planners.tomorrowGoals(userId), data),
  retrospectionImages: (userId: number, data: { date: string; retrospectionImage: string | null }) =>
    Axios.put(api.planners.retrospectionImages(userId), data),
  social: (userId: number, data: { date: string; socialImage: string }) =>
    Axios.post(api.planners.social(userId), data),
};

export const settingApi = {
  accessScopes: (userId: number, data: { plannerAccessScope: MonthConfig["plannerAccessScope"] }) =>
    Axios.put(api.setting.accessScopes(userId), data),

  categories: (userId: number) => Axios.get(api.setting.categories(userId)),
  categoriesColors: (userId: number) => Axios.get(api.setting.categoriesColors(userId)),
  addCategories: (
    userId: number,
    data: { categoryTitle: string; categoryColorId: number; categoryEmoticon: string | null },
  ) => Axios.post(api.setting.categories(userId), data),
  editCategories: (
    userId: number,
    data: { categoryId: number; categoryTitle: string; categoryColorId: number; categoryEmoticon: string | null },
  ) => Axios.put(api.setting.categories(userId), data),
  deleteCategories: (userId: number, data: { categoryId: number }) =>
    Axios.delete(api.setting.categories(userId), { data: data }),

  ddays: (userId: number) => Axios.get(api.setting.ddays(userId)),
  addDdays: (userId: number, data: { ddayDate: string; ddayTitle: string }) =>
    Axios.post(api.setting.ddays(userId), data),
  editDdays: (userId: number, data: { ddayId: number; ddayDate: string; ddayTitle: string }) =>
    Axios.put(api.setting.ddays(userId), data),
  deleteDdays: (userId: number, data: { ddayId: number }) => Axios.delete(api.setting.ddays(userId), { data: data }),
};

export const socialApi = {
  getSocial: (userId: number, params: { sort: "latest" | "popularity"; pageNumber: number }) =>
    Axios.get(api.social.getSocial(userId), { params: params }),
  searches: (userId: number, data: { nickname: string; sort: "latest" | "popularity"; pageNumber: number }) =>
    Axios.post(api.social.searches(userId), data),
  delete: (userId: number, socialId: number) => Axios.delete(api.social.delete(userId, socialId)),
};
