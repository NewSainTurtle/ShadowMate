import { configureStore, combineReducers } from "@reduxjs/toolkit";
import authReducer from "@store/authSlice";
import monthReducer from "@store/planner/monthSlice";
import weekReducer from "@store/planner/weekSlice";
import dayReducer from "@store/planner/daySlice";
import categoryReducer from "@store/mypage/categorySlice";
import ddayReducer from "@store/mypage/ddaySlice";
import routineReducer from "@store/mypage/routineSlice";
import frinedReducer from "@store/friendSlice";
import modalReducer from "@store/modalSlice";
import alertReducer from "@store/alertSlice";

import { persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage";
import persistStore from "redux-persist/es/persistStore";
import { encryptTransform } from "redux-persist-transform-encrypt";

const rootReducer = combineReducers({
  auth: authReducer,
  month: monthReducer,
  week: weekReducer,
  day: dayReducer,
  category: categoryReducer,
  dday: ddayReducer,
  routine: routineReducer,
  friend: frinedReducer,
  modal: modalReducer,
  alert: alertReducer,
});

const persistConfig = {
  key: "root",
  transforms: [
    encryptTransform({
      secretKey: process.env.REACT_APP_ENCRYPT_PRIVATE_KEY!,
      onError: (err) => console.log(err),
    }),
  ],
  whitelist: ["auth", "category", "dday"],
  storage,
};

export const store = configureStore({
  reducer: persistReducer(persistConfig, rootReducer),
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});

export const persistor = persistStore(store);

export type AppDispatch = typeof store.dispatch;
export type rootState = ReturnType<typeof store.getState>;
