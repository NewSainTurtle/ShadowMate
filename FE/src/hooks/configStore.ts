import { configureStore, combineReducers } from "@reduxjs/toolkit";
import authReducer from "@store/authSlice";

const rootReducer = combineReducers({
  auth: authReducer,
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
