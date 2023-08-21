import React from "react";
import styles from "@styles/common/Header.module.scss";

const Toggle = () => {
  const onChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.target.checked
      ? document.documentElement.setAttribute("data-theme", "dark")
      : document.documentElement.setAttribute("data-theme", "light");
  };

  return (
    <>
      <div className={styles.toggle}>
        <input type="checkbox" id="switch" name="mode" onChange={onChange} />
        <label htmlFor="switch">Toggle</label>
      </div>
    </>
  );
};

export default Toggle;
