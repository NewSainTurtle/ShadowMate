import React, { useState } from "react";
import styles from "@styles/common/Input.module.scss";
import { IconButton, InputAdornment, TextField, TextFieldProps } from "@mui/material";
import { Visibility, VisibilityOff, Search } from "@mui/icons-material";
import colors from "@util/colors";

interface Props {
  name: string;
  placeholder: string;
  types: "default" | "password" | "search";
  value?: string | number;
}

const Input = ({ types, ...rest }: Props & TextFieldProps) => {
  const [showPassword, setShowPassword] = useState(false);
  const handleClickShowPassword = () => setShowPassword((show) => !show);

  return (
    <TextField
      className={styles["input"]}
      type={types == "password" && !showPassword ? "password" : "text"}
      sx={{
        "& .MuiInputBase-input.Mui-disabled": {
          WebkitTextFillColor: colors.colorGray_Light_4,
        },
      }}
      InputProps={
        types == "password"
          ? {
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton onClick={handleClickShowPassword} className={styles.input__icon}>
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              ),
            }
          : types == "search"
          ? {
              startAdornment: (
                <InputAdornment position="start" className={styles.input__icon}>
                  <Search />
                </InputAdornment>
              ),
            }
          : {}
      }
      FormHelperTextProps={{
        className: styles["input__helper-text"],
      }}
      {...rest}
      variant="standard"
    />
  );
};

Input.defaultProps = {
  name: "input이름",
  placeholder: "input설명",
  types: "default",
};

export default Input;
