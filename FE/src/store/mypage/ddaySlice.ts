import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { DdayItemConfig } from "@util/planner.interface";
import { PURGE } from "redux-persist";

interface DdayConfig {
  ddayList: DdayItemConfig[];
  ddayClick: number;
  ddayInput: DdayItemConfig;
}

const initialState: DdayConfig = {
  ddayList: [],
  ddayClick: 0,
  ddayInput: {
    ddayId: 0,
    ddayTitle: "",
    ddayDate: new Date(),
  },
};

const ddaySlice = createSlice({
  name: "dday",
  initialState,
  reducers: {
    setDdayList: (state, { payload }: PayloadAction<DdayItemConfig[]>) => {
      state.ddayList = payload;
    },
    setDdayClick: (state, { payload }: PayloadAction<number>) => {
      state.ddayClick = payload;
    },
    setDdayInput: (state, { payload }: PayloadAction<DdayItemConfig>) => {
      state.ddayInput = payload;
    },
  },
  extraReducers: (builder) => {
    // redux-persist 초기화
    builder.addCase(PURGE, () => initialState);
  },
});

export const { setDdayList, setDdayClick, setDdayInput } = ddaySlice.actions;

export const selectDdayList = (state: rootState) => state.dday.ddayList;
export const selectDdayClick = (state: rootState) => state.dday.ddayClick;
export const selectDdayInput = (state: rootState) => state.dday.ddayInput;

export default ddaySlice.reducer;
