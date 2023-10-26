import { configureStore, combineReducers } from "@reduxjs/toolkit";
import authReducer from "@store/authSlice";
import dayReducer from "@store/planner/daySlice";
import weekReducer from "@store/weekSlice";

const rootReducer = combineReducers({
  auth: authReducer,
  week: weekReducer,
  day: dayReducer,
});

export const store = configureStore({
  reducer: rootReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});

export type AppDispatch = typeof store.dispatch;
export type rootState = ReturnType<typeof store.getState>;
