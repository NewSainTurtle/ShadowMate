import React, { useState } from "react";
import styles from "@styles/social/Social.module.scss";
import CardList from "@components/social/CardList";
import SocialHeader from "@components/social/SocialHeader";
import { useDebounce } from "@util/EventControlModule";

const SocialPage = () => {
  const [order, setOrder] = useState<"latest" | "popularity">("latest");
  const [searchKeyWord, setSearchKeyWord] = useState("");
  const debounceKeyword = useDebounce(searchKeyWord, 400);

  return (
    <div className={styles["page-container"]}>
      <div className={styles["item-header"]}>
        <SocialHeader
          order={order}
          searchKeyWord={searchKeyWord}
          setSearchKeyWord={setSearchKeyWord}
          setOrder={setOrder}
        />
      </div>
      <div className={styles["item-list"]}>
        <CardList sort={order} search={debounceKeyword} />
      </div>
    </div>
  );
};

export default SocialPage;
