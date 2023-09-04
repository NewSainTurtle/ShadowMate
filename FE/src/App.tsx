import React, { useEffect } from "react";
import { Route, Routes } from "react-router-dom";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import "./App.scss";
import Header from "@components/common/Header";
import MainPage from "@pages/MainPage";
import LoginPage from "@pages/LoginPage";
import SignupPage from "@pages/SignupPage";
import DayPage from "@pages/Planner/DayPage";
import MonthPage from "@pages/Planner/MonthPage";
import WeekPage from "@pages/Planner/WeekPage";
import SocialPage from "@pages/SocialPage";
import CommonPage from "@pages/commonPage";
import NotFoundPage from "@pages/NotFoundPage";
import MyPage from "@pages/Mypage";

const theme = createTheme({
  typography: {
    fontFamily: "Pretendard-Regular",
  },
});

const App = () => {
  useEffect(() => {
    document.documentElement.setAttribute("data-theme", "light");
  }, []);

  return (
    <ThemeProvider theme={theme}>
      <Header />
      <div id="App" style={{ marginLeft: "10em" }}>
        <Routes>
          <Route path="/common" element={<CommonPage />} />
          
          <Route path="/" element={<MainPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          
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
