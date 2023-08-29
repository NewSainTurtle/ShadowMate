import React, { useEffect } from "react";
import { Route, Routes } from "react-router-dom";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import "./App.scss";
import Header from "@components/common/Header";
import MainPage from "@pages/MainPage";
import LoginPage from "@pages/LoginPage";
import CommonPage from "@pages/commonPage";

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
      <Routes>
        <Route path="/common" element={<CommonPage />} />
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    </ThemeProvider>
  );
};
export default App;
