import React, { useEffect, useLayoutEffect, useState } from "react";
import { Route, Routes, useLocation, useNavigate } from "react-router-dom";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import styles from "./App.scss";
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
import Alert from "@components/common/Alert";
import Modal from "@components/common/Modal";
import TokenExpiration from "@components/common/Modal/TokenExpiration";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectLoginState, setLogin, setLogout } from "@store/authSlice";
import { selectModal, setModalClose } from "@store/modalSlice";
import { selectAlertInfo, setAlertClose } from "@store/alertSlice";
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
  const { type, message, open } = useAppSelector(selectAlertInfo);

  const handleTokenExpiration = () => {
    dispatch(setModalClose());
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
    setPathName(["/day", "/week", "/month", "/social", "/mypage", "/search", "/category"].includes(location.pathname));
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
    <div className={styles[pathName ? "App__header" : "App__none"]}>
      <ThemeProvider theme={theme}>
        {pathName && <Header />}
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
        <div>
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
              <Route path="/search" element={<MyPage name="친구 검색" />} />
              <Route path="/category" element={<MyPage name="카테고리 설정" />} />
            </Route>

            <Route path="*" element={<NotFoundPage />} />
            <Route path="/common" element={<CommonPage />} />
          </Routes>
        </div>
        <Alert types={type} open={open} onClose={() => dispatch(setAlertClose())} message={message} />
      </ThemeProvider>
    </div>
  );
};
export default App;
