import React from "react";
import { Navigate, Outlet } from "react-router-dom";

interface Props {
  isLogin: boolean;
  option: boolean;
}
/**
 * @param 로그인 여부, 접근 가능 여부
 * @returns (로그인 & 노옵션 : /month) ,(노로그인 & 옵션 : /)
 */

const PrivateRoute = ({ isLogin, option }: Props) => {
  if (isLogin && !option) return <Navigate to="/month" />;
  else if (!isLogin && option) return <Navigate to="/" />;
  return <Outlet />;
};

export default PrivateRoute;
