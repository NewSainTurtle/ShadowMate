import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";

interface AlertConfig {
  type: "success" | "error";
  message: string;
  open: boolean;
}

const initialState: AlertConfig = {
  type: "success",
  message: "",
  open: false,
};

const alertSlice = createSlice({
  name: "alert",
  initialState,
  reducers: {
    setAlertOpen: (status, { payload }: PayloadAction<{ type: "success" | "error"; message: string }>) => {
      status.open = true;
      status.type = payload.type;
      status.message = payload.message;
    },
    setAlertClose: (status) => {
      status.open = false;
    },
  },
});

export const { setAlertOpen, setAlertClose } = alertSlice.actions;
export const selectAlertInfo = (state: rootState) => state.alert;

export default alertSlice.reducer;
