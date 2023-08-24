import React, { useEffect } from "react";
import { Route, Routes } from "react-router-dom";
import Header from "@components/common/Header";
import MainPage from "@pages/MainPage";
import LoginPage from "@pages/LoginPage";
import CommonPage from "@pages/commonPage";

const App = () => {
  useEffect(() => {
    document.documentElement.setAttribute("data-theme", "light");
  }, []);

  return (
    <>
      <Header />
      <Routes>
        <Route path="/common" element={<CommonPage />} />
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    </>
  );
};
export default App;
