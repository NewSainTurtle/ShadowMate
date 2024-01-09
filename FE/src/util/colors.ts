import themes from "@styles/global/themes.scss";

export const monthStatusColor = [
  "var(--color-calendar-0)",
  "var(--color-calendar-10)",
  "var(--color-calendar-60)",
  "var(--color-calendar-100)",
];

export const colors = {
  colorBlack: themes["color-black"],
  colorWhite: themes["color-white"],
  colorDark: themes["color-dark"],
  colorWarning: themes["color-warning"],
  colorGray_Dark_1: themes["color-gray-dark-1"],
  colorGray_Dark_4: themes["color-gray-dark-4"],
  colorGray_Light_1: themes["color-gray-light-1"],
  colorGray_Light_4: themes["color-gray-light-4"],
};

export type ColorsKeyTypes = keyof typeof colors;

export default colors;
