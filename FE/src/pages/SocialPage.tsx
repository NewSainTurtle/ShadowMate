import React, { useState } from "react";
import styles from "@styles/social/Social.module.scss";
import CardList from "@components/social/CardList";
import SocialHeader from "@components/social/SocialHeader";

const SocialPage = () => {
  const [order, setOrder] = useState("최신순");
  const [inputValue, setInputValue] = useState("");
  const [searchValue, setSearchValue] = useState("");

  return (
    <div className={styles["page-container"]}>
      <div className={styles["item-header"]}>
        <SocialHeader
          order={order}
          inputValue={inputValue}
          setInputValue={setInputValue}
          setSearchValue={setSearchValue}
          setOrder={setOrder}
        />
      </div>
      <div className={styles["item-list"]}>
        <CardList search={searchValue} />
      </div>
    </div>
  );
};

export default SocialPage;
