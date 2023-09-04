import React, { useState } from "react";
import styles from "@styles/common/Input.module.scss";
import { IconButton, InputAdornment, TextField } from "@mui/material";
import { Visibility, VisibilityOff, Search } from "@mui/icons-material";

type Props = {
  name: string;
  placeholder: string;
  types: "default" | "password" | "search";
  value?: string | number;
  disabled?: boolean;
  onChange?: React.ChangeEventHandler<HTMLInputElement>;
  onKeyPress?: React.KeyboardEventHandler<HTMLInputElement>;
};

const InputStyles = ({ types, ...rest }: Props) => {
  const [showPassword, setShowPassword] = useState(false);
  const handleClickShowPassword = () => setShowPassword((show) => !show);

  return (
    <TextField
      className={styles.input}
      type={types == "password" && !showPassword ? "password" : "text"}
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
      {...rest}
      variant="standard"
    />
  );
};

InputStyles.defaultProps = {
  name: "input이름",
  placeholder: "input설명",
  types: "default",
};

export default InputStyles;
