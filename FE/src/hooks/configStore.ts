import { configureStore, combineReducers } from "@reduxjs/toolkit";
import authReducer from "@store/authSlice";
import monthReducer from "@store/planner/monthSlice";
import weekReducer from "@store/planner/weekSlice";
import dayReducer from "@store/planner/daySlice";
import mypageReducer from "@store/mypageSlice";
import frinedReducer from "@store/friendSlice";

import { persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage";
import persistStore from "redux-persist/es/persistStore";

const rootReducer = combineReducers({
  auth: authReducer,
  month: monthReducer,
  week: weekReducer,
  day: dayReducer,
  mypage: mypageReducer,
  friend: frinedReducer,
});

const persistConfig = {
  key: "root",
  whitelist: ["auth"],
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
