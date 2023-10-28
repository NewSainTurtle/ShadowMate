export const userRegex = {
  email:
    /([!#-'*+/-9=?A-Z^-~-]+(.[!#-'*+/-9=?A-Z^-~-]+)*|\"([]!#-[^-~ \t]|(\\[\t -~]))+\")@([!#-'*+/-9=?A-Z^-~-]+(.[!#-'*+/-9=?A-Z^-~-]+)*|[[\t -Z^-~]*])$/,
  password: /^[a-zA-Z\\d!?^&*@#]{6,20}$/,
  nickname: /^[ㄱ-ㅎ가-힣a-zA-Z\\d]{2,10}$/,
};

export const removeWhitespaceRegex = /\s/g;
