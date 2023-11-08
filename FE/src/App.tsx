import React, { useEffect, useLayoutEffect, useRef, useState } from "react";
import { Route, Routes, useLocation, useNavigate } from "react-router-dom";
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
import { selectLoginState, setLogin, setLogout } from "@store/authSlice";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import Modal from "@components/common/Modal";
import TokenExpiration from "@components/common/Modal/TokenExpiration";
import { selectModal, setModalClose } from "@store/modalSlice";
import { persistor } from "@hooks/configStore";

const theme = createTheme({
  typography: {
    fontFamily: "Pretendard-Regular",
  },
});

const App = () => {
  const dispatch = useAppDispatch();
  const navigator = useNavigate();
  const location = useLocation();
  const [pathName, setPathName] = useState(false);
  const isLogin = useAppSelector(selectLoginState);
  const { isOpen } = useAppSelector(selectModal);

  const handleTokenExpiration = () => {
    dispatch(setLogout());
    persistor.purge(); // 리덕스 초기화
    navigator("/login");
  };

  useEffect(() => {
    const theme = localStorage.getItem("theme");
    let isDarkMode = window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
    if (!theme) localStorage.setItem("theme", isDarkMode);
    else isDarkMode = theme;

    document.documentElement.setAttribute("data-theme", isDarkMode);
  }, []);

  useLayoutEffect(() => {
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
      {isLogin && (
        <Modal
          types="oneBtn"
          open={isOpen}
          onClose={() => dispatch(setModalClose())}
          onClick={handleTokenExpiration}
          onClickMessage="로그인 페이지로 이동"
          prevent
        >
          <TokenExpiration />
        </Modal>
      )}
      <div id="App" style={pathName ? {} : { marginLeft: "10em" }}>
        <Routes>
          <Route element={<PrivateRoute isLogin={isLogin} option={false} />}>
            <Route path="/" element={<LandingPage />} />
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
          <Route path="/common" element={<CommonPage />} />
        </Routes>
      </div>
    </ThemeProvider>
  );
};
export default App;
