import React, { useEffect, useLayoutEffect, useState } from "react";
import { Route, Routes, useLocation, useNavigate } from "react-router-dom";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import styles from "./App.scss";
import Header from "@components/common/Header";
import DayPage from "@pages/Planner/DayPage";
import MonthPage from "@pages/Planner/MonthPage";
import WeekPage from "@pages/Planner/WeekPage";
import SocialPage from "@pages/SocialPage";
import NotFoundPage from "@pages/NotFoundPage";
import MyPage from "@pages/MyPage";
import AuthPage from "@pages/AuthPage";
import LandingPage from "@pages/LandingPage";
import PrivateRoute from "@util/PrivateRoute";
import Alert from "@components/common/Alert";
import Modal from "@components/common/Modal";
import TokenExpiration from "@components/common/Modal/TokenExpiration";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectLoginState, selectType, selectUserId, setLogin, setLogout } from "@store/authSlice";
import { selectModal, setModalClose } from "@store/modalSlice";
import { selectAlertInfo, setAlertClose } from "@store/alertSlice";
import { persistor } from "@hooks/configStore";
import { authApi } from "@api/Api";
import { AxiosError } from "axios";

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
  const userId = useAppSelector(selectUserId);
  const loginType = useAppSelector(selectType);
  const isLogin = useAppSelector(selectLoginState);
  const { isOpen } = useAppSelector(selectModal);
  const { type, message, open } = useAppSelector(selectAlertInfo);

  const handleTokenExpiration = () => {
    const autoLoginKey = localStorage.getItem("AL");
    const headers = {
      "Auto-Login": autoLoginKey ?? "",
    };
    authApi
      .logout({ userId, type: loginType }, headers)
      .then(() => {
        dispatch(setModalClose());
        dispatch(setLogout());
      })
      .then(() => {
        persistor.purge();
        localStorage.removeItem("AL");
        navigator("/login");
      })
      .catch((err) => console.log(err));
  };

  const handleAutoLogin = async (key: string) => {
    try {
      const headers = {
        "Auto-Login": key,
      };
      const res = await authApi.autoLogin(null, headers);
      const accessToken = res.headers["authorization"];
      const userId = res.headers["id"];
      const type = res.headers["type"];
      dispatch(setLogin({ accessToken, userId, type }));
      navigator("/month");
    } catch (err) {
      if (err instanceof AxiosError) {
        if (err.response?.status === 403) {
          localStorage.removeItem("AL");
        }
      }
    }
  };

  useEffect(() => {
    const theme = localStorage.getItem("theme");
    let isDarkMode = window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
    if (!theme) localStorage.setItem("theme", isDarkMode);
    else isDarkMode = theme;

    document.documentElement.setAttribute("data-theme", isDarkMode);
  }, []);

  useLayoutEffect(() => {
    setPathName(["/day", "/week", "/month", "/social", "/mypage", "/search", "/category"].includes(location.pathname));
  }, [location.pathname]);

  useEffect(() => {
    const key = localStorage.getItem("AL");
    if (key && !isLogin) handleAutoLogin(key);
  }, []);

  return (
    <div className={pathName ? styles["App__header"] : ""}>
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
          </Routes>
        </div>
        <Alert types={type} open={open} onClose={() => dispatch(setAlertClose())} message={message} />
      </ThemeProvider>
    </div>
  );
};
export default App;
