import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { RoutineItemConfig } from "@util/planner.interface";
import { PURGE } from "redux-persist";

export const ROUTINES: RoutineItemConfig[] = [
  {
    routineId: 1,
    routineContent: "운동하기",
    startDay: "2023-12-25",
    endDay: "2024-01-09",
    days: ["월", "화", "수"],
    category: {
      categoryId: 1,
      categoryColorCode: "#B2B2B2",
      categoryTitle: "카테고리",
    },
  },
  {
    routineId: 2,
    routineContent: "일기 쓰기",
    startDay: "2024-01-09",
    endDay: "2024-01-20",
    days: ["수", "목", "토"],
    category: {
      categoryId: 1,
      categoryColorCode: "#B2B2B2",
      categoryTitle: "카테고리",
    },
  },
];

interface RoutineConfig {
  routineList: RoutineItemConfig[];
  routineClick: number;
  routineInput: RoutineItemConfig;
}

const initialState: RoutineConfig = {
  routineList: ROUTINES,
  routineClick: 0,
  routineInput: {
    routineId: 0,
    routineContent: "",
    startDay: "",
    endDay: "",
    days: [],
    category: {
      categoryId: 0,
      categoryTitle: "",
      categoryColorCode: "",
    },
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

export const { setRoutineList, setRoutineClick, setRoutineInput } = routineSlice.actions;
export const selectRoutineList = (state: rootState) => state.routine.routineList;
export const selectRoutineClick = (state: rootState) => state.routine.routineClick;
export const selectRoutineInput = (state: rootState) => state.routine.routineInput;

export default routineSlice.reducer;
