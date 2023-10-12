import Axios from "@api/JsonAxios";
import api from "@api/BaseUrl";

export const authApi = {
  login: (props: { email: string; password: string }) => Axios.post(api.auth.login(), props),
  join: (params: { email: string; password: string; nickname: string }) => Axios.post(api.auth.join(), params),
};

export const userApi = {
  profiles: (userId: string) => Axios.get(api.user.getProfiles(userId)),
};