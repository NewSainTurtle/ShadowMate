import React, { useState } from "react";
import styles from "@styles/Main.module.scss";

const MainPage = () => {
  const [number, setNumber] = useState<number>(0);

  return (
    <>
      <div className={styles.MyComponent}>
        <h1 className={styles.header01}>Test Main</h1>
        <h2>Hello. Jest!!</h2>
        <h4>number: {number}</h4>
        <button onClick={() => setNumber(number + 3)}>증가</button>
        <button onClick={() => setNumber(number - 2)}>감소</button>
      </div>
    </>
  );
};

export default MainPage;
