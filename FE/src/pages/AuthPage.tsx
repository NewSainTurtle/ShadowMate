import React, { useEffect, useState } from "react";
import Login from "@components/auth/login/Login";
import AuthHeader from "@components/auth/AuthHeader";
import { useLocation } from "react-router-dom";
import Signup from "@components/auth/signup/Signup";

const AuthPage = () => {
  const location = useLocation();
  const [pathName, setPathName] = useState("");

  useEffect(() => {
    setPathName(location.pathname);
  }, [location.pathname]);

  return (
    <>
      <AuthHeader />
      {{ "/login": <Login />, "/signup": <Signup /> }[pathName]}
    </>
  );
};

export default AuthPage;
