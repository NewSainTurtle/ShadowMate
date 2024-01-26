import React from "react";
import styles from "@styles/social/Social.module.scss";
import { Divider, IconButton, InputAdornment, TextField, TextFieldProps } from "@mui/material";
import { DateRange, Search } from "@mui/icons-material";
import colors from "@util/colors";
import Text from "@components/common/Text";

interface Props {
  name: string;
  placeholder: string;
  value?: string | number;
  onCalendarClick: () => void;
  onMyClick: () => void;
}

const SocialInput = ({ onCalendarClick, onMyClick, ...rest }: Props & TextFieldProps) => {
  return (
    <TextField
      className={styles["searchInput"]}
      type="text"
      sx={{
        "& .MuiInputBase-input.Mui-disabled": {
          WebkitTextFillColor: colors.colorGray_Light_4,
        },
      }}
      InputProps={{
        startAdornment: (
          <InputAdornment position="start" className={styles["searchInput__icon"]}>
            <Search />
          </InputAdornment>
        ),
        endAdornment: (
          <>
            <Divider className={styles["divider"]} orientation="vertical" />
            <IconButton className={styles["searchInput__button"]} disableRipple onClick={onCalendarClick}>
              <DateRange />
            </IconButton>
            <Divider className={styles["divider"]} orientation="vertical" />
            <IconButton className={styles["searchInput__button"]} disableRipple onClick={onMyClick}>
              <Text bold>My</Text>
            </IconButton>
          </>
        ),
      }}
      FormHelperTextProps={{
        className: styles["input__helper-text"],
      }}
      inputProps={{ maxLength: 10 }}
      {...rest}
      variant="standard"
    />
  );
};

export default SocialInput;
