import React, { Component } from "react";
import { Navigate, Outlet } from "react-router-dom";

interface Props {
  isLogin: boolean;
}

const PrivateRoute = ({ isLogin }: Props) => {
  return isLogin ? <Outlet /> : <Navigate to="/login" />;
};

export default PrivateRoute;
