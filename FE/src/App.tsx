import React, { useEffect, useState } from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import "./App.scss";
import Header from "@components/common/Header";
import DayPage from "@pages/Planner/DayPage";
import MonthPage from "@pages/Planner/MonthPage";
import WeekPage from "@pages/Planner/WeekPage";
import SocialPage from "@pages/SocialPage";
import CommonPage from "@pages/commonPage";
import NotFoundPage from "@pages/NotFoundPage";
import MyPage from "@pages/MyPage";
import AuthPage from "@pages/AuthPage";
import LandingPage from "@pages/LandingPage";
import PrivateRoute from "@util/PrivateRoute";
import { selectLoginState, setLogin } from "@store/authSlice";
import { useAppDispatch, useAppSelector } from "@hooks/hook";

const theme = createTheme({
  typography: {
    fontFamily: "Pretendard-Regular",
  },
});

const App = () => {
  const dispatch = useAppDispatch();
  const location = useLocation();
  const [pathName, setPathName] = useState(false);
  const isLogin = useAppSelector(selectLoginState);

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", "light");
  }, []);

  useEffect(() => {
    setPathName(["/", "/login", "/signup"].includes(location.pathname));
  }, [location.pathname]);

  useEffect(() => {
    // 자동 로그인 확인 시
    const accessToken = localStorage.getItem("accessToken") || "";
    const id = localStorage.getItem("id");
    let userId = 0;
    if (id) userId = parseInt(id);
    if (accessToken && id) dispatch(setLogin({ accessToken: accessToken, userId: userId }));
  }, []);

  return (
    <ThemeProvider theme={theme}>
      {!pathName && <Header />}
      <div id="App" style={pathName ? {} : { marginLeft: "10em" }}>
        <Routes>
          <Route element={<PrivateRoute isLogin={isLogin} option={false} />}>
            <Route path="/" element={<LandingPage />} />
            <Route path="/common" element={<CommonPage />} />
            <Route path="/login" element={<AuthPage />} />
            <Route path="/signup" element={<AuthPage />} />
          </Route>

          <Route element={<PrivateRoute isLogin={isLogin} option={true} />}>
            <Route path="/day" element={<DayPage />} />
            <Route path="/week" element={<WeekPage />} />
            <Route path="/month" element={<MonthPage />} />

            <Route path="/social" element={<SocialPage />} />
            <Route path="/mypage" element={<MyPage />} />
          </Route>

          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </div>
    </ThemeProvider>
  );
};
export default App;
