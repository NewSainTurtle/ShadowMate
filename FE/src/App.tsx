import React, { useEffect, useState } from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import "./App.scss";
import Header from "@components/common/Header";
import MainPage from "@pages/MainPage";
import DayPage from "@pages/Planner/DayPage";
import MonthPage from "@pages/Planner/MonthPage";
import WeekPage from "@pages/Planner/WeekPage";
import SocialPage from "@pages/SocialPage";
import CommonPage from "@pages/commonPage";
import NotFoundPage from "@pages/NotFoundPage";
import MyPage from "@pages/Mypage";
import AuthPage from "@pages/AuthPage";

const theme = createTheme({
  typography: {
    fontFamily: "Pretendard-Regular",
  },
});

const App = () => {
  const location = useLocation();
  const [pathName, setPathName] = useState(false);

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", "light");
  }, []);

  useEffect(() => {
    setPathName(["/", "/login", "/signup"].includes(location.pathname));
  }, [location.pathname]);

  return (
    <ThemeProvider theme={theme}>
      {!pathName && <Header />}
      <div id="App" style={pathName ? {} : { marginLeft: "10em" }}>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/common" element={<CommonPage />} />
          <Route path="/login" element={<AuthPage />} />
          <Route path="/signup" element={<AuthPage />} />

          <Route path="/day" element={<DayPage />} />
          <Route path="/week" element={<WeekPage />} />
          <Route path="/month" element={<MonthPage />} />

          <Route path="/social" element={<SocialPage />} />
          <Route path="/mypage" element={<MyPage />} />

          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </div>
    </ThemeProvider>
  );
};
export default App;
