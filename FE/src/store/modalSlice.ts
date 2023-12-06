import React from "react";
import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";

interface ModalConfig {
  isOpen: boolean;
  isPopupVisible: boolean;
}

const initialState: ModalConfig = {
  isOpen: false,
  isPopupVisible: false,
};

const modalSlice = createSlice({
  name: "modal",
  initialState,
  reducers: {
    setModalOpen: (state) => {
      state.isOpen = true;
    },
    setModalClose: (state) => {
      state.isOpen = false;
    },
    setPopupOpen: (state) => {
      state.isPopupVisible = true;
    },
    setPopupClose: (state) => {
      state.isPopupVisible = false;
    },
  },
});

export const { setModalOpen, setModalClose, setPopupOpen, setPopupClose } = modalSlice.actions;
export const selectModal = (state: rootState) => state.modal;
export const selectPopupVisible = (state: rootState) => state.modal.isPopupVisible;

export default modalSlice.reducer;
