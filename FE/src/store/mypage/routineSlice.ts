import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { CategoryItemConfig } from "@util/planner.interface";
import { PURGE } from "redux-persist";

export interface RoutineItemConfig {
  routineId: number;
  routineContent: string;
  startDay: string | Date;
  endDay: string | Date;
  days: string[];
  category: CategoryItemConfig | null;
}
interface RoutineConfig {
  routineList: RoutineItemConfig[];
  routineClick: number;
  routineInput: RoutineItemConfig;
}

const initialState: RoutineConfig = {
  routineList: [],
  routineClick: 0,
  routineInput: {
    routineId: 0,
    routineContent: "",
    startDay: new Date(),
    endDay: new Date(),
    days: [],
    category: null,
  },
};

const routineSlice = createSlice({
  name: "routine",
  initialState,
  reducers: {
    setRoutineList: (state, { payload }: PayloadAction<RoutineItemConfig[]>) => {
      state.routineList = payload;
    },
    setRoutineClick: (state, { payload }: PayloadAction<number>) => {
      state.routineClick = payload;
    },
    setRoutineInput: (state, { payload }: PayloadAction<RoutineItemConfig>) => {
      state.routineInput = payload;
    },
  },
  extraReducers: (builder) => {
    // redux-persist 초기화
    builder.addCase(PURGE, () => initialState);
  },
});

export const BASIC_ROUTINE_INPUT = initialState.routineInput;
export const { setRoutineList, setRoutineClick, setRoutineInput } = routineSlice.actions;
export const selectRoutineList = (state: rootState) => state.routine.routineList;
export const selectRoutineClick = (state: rootState) => state.routine.routineClick;
export const selectRoutineInput = (state: rootState) => state.routine.routineInput;

export default routineSlice.reducer;
