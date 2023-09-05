import React from "react";
import Login from "@components/auth/login/Login";
import AuthHeader from "@components/auth/AuthHeader";

const LoginPage = () => {
  return (
    <>
      <AuthHeader />
      <Login />
    </>
  );
};

export default LoginPage;
