import React from "react";
import ReactDOM from "react-dom/client";
import App from "src/App";
import { BrowserRouter } from "react-router-dom";
import { StyledEngineProvider } from "@mui/material/styles";
import { persistor, store } from "@hooks/configStore";
import { Provider } from "react-redux";
import { PersistGate } from "redux-persist/integration/react";
import "src/index.scss";

const root = ReactDOM.createRoot(document.getElementById("root") as HTMLElement);

root.render(
  <BrowserRouter>
    <StyledEngineProvider injectFirst>
      <Provider store={store}>
        <PersistGate persistor={persistor}>
          <App />
        </PersistGate>
      </Provider>
    </StyledEngineProvider>
  </BrowserRouter>,
);
