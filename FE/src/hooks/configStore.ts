import { configureStore, combineReducers } from "@reduxjs/toolkit";
import authReducer from "@store/authSlice";
import weekReducer from "@store/planner/weekSlice";
import dayReducer from "@store/planner/daySlice";
import { persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage";
import persistStore from "redux-persist/es/persistStore";


const rootReducer = combineReducers({
  auth: authReducer,
  week: weekReducer,
  day: dayReducer,
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
