export const userRegex = {
  email: /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/,
  password: /^[\w!?^&*@#]{6,20}$/,
  nickname: /^[ㄱ-ㅎ가-힣ㅏ-ㅣ\w]{2,10}$/,
  statusMessage: /^[ㄱ-ㅎ가-힣ㅏ-ㅣ\w\s]{0,20}$/,
};

export const removeWhitespaceRegex = /\s/g;
