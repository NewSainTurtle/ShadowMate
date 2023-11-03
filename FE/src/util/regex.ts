export const userRegex = {
  email: /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/,
  password: /^[a-zA-Z\\d!?^&*@#]{6,20}$/,
  nickname: /^[ㄱ-ㅎ가-힣a-zA-Z\\d]{2,10}$/,
  statusMessage: /^[ㄱ-ㅎ가-힣a-zA-Z\\d]{0,20}$/,
};

export const removeWhitespaceRegex = /\s/g;
