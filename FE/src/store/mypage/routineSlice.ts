import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";
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

export interface InitRoutineItemConfig {
  routineContent: string;
  startDay: string | Date;
  endDay: string | Date;
  category: CategoryItemConfig | null;
  days: string[];
}

interface RoutineConfig {
  routineList: RoutineItemConfig[];
  routineClick: number;
  routineInput: RoutineItemConfig | InitRoutineItemConfig;
  routineIsInit: boolean;
}

const initialState: RoutineConfig = {
  routineList: [],
  routineClick: 0,
  routineInput: {
    routineId: 0,
    routineContent: "",
    startDay: "",
    endDay: "",
    days: [],
    category: {
      categoryId: BASIC_CATEGORY_ITEM.categoryId,
      categoryTitle: BASIC_CATEGORY_ITEM.categoryTitle,
      categoryColorCode: BASIC_CATEGORY_ITEM.categoryColorCode,
    },
  },
  routineIsInit: false,
};

const routineSlice = createSlice({
  name: "routine",
  initialState,
  reducers: {
    setRoutineList: (state, { payload }: PayloadAction<RoutineItemConfig[]>) => {
      // console.log(payload);
      state.routineList = payload;
    },
    setRoutineClick: (state, { payload }: PayloadAction<number>) => {
      state.routineClick = payload;
    },
    setRoutineInput: (state, { payload }: PayloadAction<RoutineItemConfig | InitRoutineItemConfig>) => {
      state.routineInput = payload;
    },
    setRoutineIsInit: (state, { payload }: PayloadAction<boolean>) => {
      state.routineIsInit = payload;
    },
  },
  extraReducers: (builder) => {
    // redux-persist 초기화
    builder.addCase(PURGE, () => initialState);
  },
});

export const BASIC_ROUTINE_INPUT = initialState.routineInput;
export const { setRoutineList, setRoutineClick, setRoutineInput, setRoutineIsInit } = routineSlice.actions;
export const selectRoutineList = (state: rootState) => state.routine.routineList;
export const selectRoutineClick = (state: rootState) => state.routine.routineClick;
export const selectRoutineInput = (state: rootState) => state.routine.routineInput;
export const selectRoutineIsInit = (state: rootState) => state.routine.routineIsInit;

export default routineSlice.reducer;
