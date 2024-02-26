import Axios from "@api/JsonAxios";
import api from "@api/BaseUrl";
import { MonthConfig, MonthDayConfig } from "@store/planner/monthSlice";
import { RoutineItemConfig } from "@store/mypage/routineSlice";
import { IntroductionConfig } from "@components/planner/month/MonthDetailInfo/Introduction";
import { RoutineUpdateSelectorConfig } from "@components/mypage/MyPageFrame";
import { FollowRequestType, FollowerType, FollowingType, FriendSearchResponse } from "@util/friend.interface";
import {
  CategoryColorConfig,
  CategoryItemConfig,
  DayInfoResponse,
  DdayItemConfig,
  GuestBookConfig,
} from "@util/planner.interface";
import { SocialListType } from "@components/social/CardList";
import { UserInfoConfig } from "@util/auth.interface";
import { WeekConfig } from "@store/planner/weekSlice";
import { AxiosRequestConfig } from "axios";

interface ServerResponse<T> {
  message: string; // 메시지
  data: T; // 데이터 내용
}

export interface ServerErrorResponse {
  code: string;
  message: string;
}

const Get = async <T>(url: string, config?: AxiosRequestConfig) => {
  return await Axios.get<ServerResponse<T>>(url, config);
};
const Post = async <T>(url: string, data?: Record<string, unknown | unknown[]>, config?: AxiosRequestConfig) => {
  return await Axios.post<ServerResponse<T>>(url, data, config);
};
const Put = async <T>(url: string, data?: Record<string, unknown | unknown[]>, config?: AxiosRequestConfig) => {
  return await Axios.put<ServerResponse<T>>(url, data, config);
};
const Delete = async <T>(url: string, data?: Record<string, unknown | unknown[]>) => {
  return await Axios.delete<ServerResponse<T>>(url, { data });
};

export const authApi = {
  join: (data: { email: string; password: string; nickname: string }) => Post(api.auth.join(), data),
  login: (data: { email: string; password: string }) => Post(api.auth.login(), data),
  autoLogin: (headers: { "Auto-Login": string }) => Post(api.auth.autoLogin(), { headers }),
  googleLogin: () => Post(api.auth.googleLogin()),
  logout: (data: { userId: number; type: string }, headers: { "Auto-Login": string }) =>
    Post(api.auth.logout(), data, { headers }),
  nickname: (data: { nickname: string }) => Post(api.auth.nickname(), data),
  deleteNickname: (data: { nickname: string }) => Delete(api.auth.nickname(), { data }),
  emailAuthentication: (data: { email: string }) => Post(api.auth.emailAuthentication(), data),
  emailAuthenticationCheck: (data: { email: string; code: string }) => Post(api.auth.emailAuthenticationCheck(), data),
  token: (userId: number, data: { type: string }) => Post(api.auth.token(userId), data),
};

export const userApi = {
  getProfiles: (userId: number) => Get<UserInfoConfig>(api.users.getProfiles(userId)),
  myPages: (userId: number, data: { newNickname: string; newProfileImage: string; newStatusMessage: string }) =>
    Put(api.users.myPages(userId), data),
  password: (userId: number, data: { oldPassword: string; newPassword: string }) =>
    Put(api.users.password(userId), data),
  userOut: (userId: number) => Delete(api.users.userOut(userId)),
  getIntroduction: (userId: number) => Get<IntroductionConfig>(api.users.introduction(userId)),
  editIntroduction: (userId: number, data: { introduction: string }) => Put(api.users.introduction(userId), data),
};

export const followApi = {
  getFollowing: (userId: number) => Get<FollowingType[]>(api.follow.following(userId)),
  deleteFollowing: (userId: number, data: { followingId: number }) =>
    Delete(api.follow.following(userId), { data: data }),
  getFollwers: (userId: number) => Get<FollowerType[]>(api.follow.followers(userId)),
  deleteFollowers: (userId: number, data: { followerId: number }) =>
    Delete(api.follow.followers(userId), { data: data }),
  addRequested: (userId: number, data: { followingId: number }) =>
    Post<{ followId: number; plannerAcceesScope: string }>(api.follow.requested(userId), data),
  cancelRequested: (userId: number, data: { receiverId: number }) =>
    Delete(api.follow.requested(userId), { data: data }),
  receive: (userId: number, data: { requesterId: number; followReceive: boolean }) =>
    Post(api.follow.receive(userId), data),
  receiveList: (userId: number) => Get<FollowRequestType[]>(api.follow.receiveList(userId)),
  getFollowCount: (userId: number) => Get<MonthConfig["followCount"]>(api.follow.count(userId)),
  searches: (userId: number, params: { nickname: string }) =>
    Get<FriendSearchResponse>(api.follow.searches(userId), { params }),
};

export const plannerApi = {
  calendars: (userId: number, params: { date: string }) =>
    Get<{
      plannerAccessScope: MonthConfig["plannerAccessScope"];
      plannerLikeCount: number;
      todoTotal: number;
      todoComplete: number;
      todoIncomplete: number;
      dayList: MonthDayConfig[];
    }>(api.planners.calendars(userId), { params: params }),
  getGuestBook: (userId: number, params: { last: number }) =>
    Get<{
      visitorBookResponses: {
        visitorBookId: number;
        visitorId: number;
        visitorNickname: string;
        visitorProfileImage: string;
        visitorBookContent: string;
        writeDateTime: string;
      }[];
    }>(api.planners.guestBook(userId), {
      params: params,
    }),
  addGuestBook: (userId: number, data: { visitorBookContent: string }) =>
    Post<GuestBookConfig>(api.planners.guestBook(userId), data),
  deleteGuestBook: (userId: number, data: { visitorBookId: number }) =>
    Delete(api.planners.guestBook(userId), { data: data }),
  weekly: (userId: number, params: { "start-date": string; "end-date": string }) =>
    Get<WeekConfig>(api.planners.weekly(userId), { params: params }),
  addWeeklyTodos: (userId: number, data: { startDate: string; endDate: string; weeklyTodoContent: string }) =>
    Post<{ weeklyTodoId: number }>(api.planners.weeklyTodos(userId), data),
  editWeeklyTodos: (
    userId: number,
    data: { startDate: string; endDate: string; weeklyTodoId: number; weeklyTodoContent: string },
  ) => Put(api.planners.weeklyTodos(userId), data),
  deleteWeeklyTodos: (userId: number, data: { startDate: string; endDate: string; weeklyTodoId: number }) =>
    Delete(api.planners.weeklyTodos(userId), { data: data }),
  weeklyTodosStatus: (
    userId: number,
    data: { startDate: string; endDate: string; weeklyTodoId: number; weeklyTodoStatus: boolean },
  ) => Put(api.planners.weeklyTodosStatus(userId), data),

  daily: (userId: number, params: { date: string }) =>
    Get<DayInfoResponse>(api.planners.daily(userId), { params: params }),
  likes: (userId: number, data: { date: string; anotherUserId: number }) => Post(api.planners.likes(userId), data),
  cancelLikes: (userId: number, data: { date: string }) => Delete(api.planners.likes(userId), { data: data }),
  addDailyTodos: (userId: number, data: { date: string; todoContent: string; categoryId: number }) =>
    Post<{ todoId: number }>(api.planners.dailyTodos(userId), data),
  editDailyTodos: (
    userId: number,
    data: {
      date: string;
      todoId: number;
      todoContent: string;
      categoryId: number;
      todoStatus: "공백" | "완료" | "진행중" | "미완료";
    },
  ) => Put(api.planners.dailyTodos(userId), data),
  deleteDailyTodos: (userId: number, data: { date: string; todoId: number }) =>
    Delete(api.planners.dailyTodos(userId), { data: data }),
  todoSequence: (userId: number, data: { date: string; todoId: number; upperTodoId: number | null }) =>
    Put(api.planners.dailyTodoSequence(userId), data),

  timetables: (userId: number, data: { date: string; todoId: number; startTime: string; endTime: string }) =>
    Post<{ timeTableId: number }>(api.planners.timetables(userId), data),
  deleteTimetable: (userId: number, data: { date: string; todoId: number; timeTableId: number }) =>
    Delete(api.planners.timetables(userId), { data: data }),
  retrospections: (userId: number, data: { date: string; retrospection: string }) =>
    Put(api.planners.retrospections(userId), data),
  todayGoals: (userId: number, data: { date: string; todayGoal: string }) => Put(api.planners.todayGoals(userId), data),
  tomorrowGoals: (userId: number, data: { date: string; tomorrowGoal: string }) =>
    Put(api.planners.tomorrowGoals(userId), data),
  retrospectionImages: (userId: number, data: { date: string; retrospectionImage: string | null }) =>
    Put(api.planners.retrospectionImages(userId), data),
  social: (userId: number, data: { date: string; socialImage: string }) =>
    Post<{ socialId: number }>(api.planners.social(userId), data),
};

export const settingApi = {
  accessScopes: (userId: number, data: { plannerAccessScope: MonthConfig["plannerAccessScope"] }) =>
    Put(api.setting.accessScopes(userId), data),

  categories: (userId: number) => Get<{ categoryList: CategoryItemConfig[] }>(api.setting.categories(userId)),
  categoriesColors: (userId: number) =>
    Get<{ categoryColorList: CategoryColorConfig[] }>(api.setting.categoriesColors(userId)),
  addCategories: (
    userId: number,
    data: { categoryTitle: string; categoryColorId: number; categoryEmoticon: string | null },
  ) => Post<{ categoryId: number }>(api.setting.categories(userId), data),
  editCategories: (
    userId: number,
    data: { categoryId: number; categoryTitle: string; categoryColorId: number; categoryEmoticon: string | null },
  ) => Put(api.setting.categories(userId), data),
  deleteCategories: (userId: number, data: { categoryId: number }) =>
    Delete(api.setting.categories(userId), { data: data }),

  ddays: (userId: number) => Get<{ ddayList: DdayItemConfig[] }>(api.setting.ddays(userId)),
  addDdays: (userId: number, data: { ddayDate: string; ddayTitle: string }) =>
    Post<{ ddayId: number }>(api.setting.ddays(userId), data),
  editDdays: (userId: number, data: { ddayId: number; ddayDate: string; ddayTitle: string }) =>
    Put(api.setting.ddays(userId), data),
  deleteDdays: (userId: number, data: { ddayId: number }) => Delete(api.setting.ddays(userId), { data: data }),
  routines: (userId: number) => Get<{ routineList: RoutineItemConfig[] }>(api.setting.routines(userId)),
  addRoutines: (
    userId: number,
    data: { routineContent: string; startDay: string; endDay: string; categoryId: number; days: string[] },
  ) => Post<{ routineId: number }>(api.setting.routines(userId), data),
  editRoutines: (
    userId: number,
    data: {
      routineId: number;
      order: RoutineUpdateSelectorConfig["order"];
      startDay: string;
      endDay: string;
      routineContent: string;
      categoryId: number;
      days: string[];
    },
  ) => Put(api.setting.routines(userId), data),
  deleteRoutines: (userId: number, data: { routineId: number; order: RoutineUpdateSelectorConfig["order"] }) =>
    Delete(api.setting.routines(userId), { data }),
};

export const socialApi = {
  getSocial: (
    userId: number,
    params: {
      sort: "latest" | "popularity";
      "page-number": number;
      nickname: string;
      "start-date": string;
      "end-date": string;
    },
  ) => Get<{ socialList: SocialListType[] }>(api.social.getSocial(userId), { params: params }),
  delete: (userId: number, socialId: number) => Delete(api.social.delete(userId, socialId)),
};
