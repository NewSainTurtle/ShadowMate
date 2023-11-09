import React, { useRef, useState } from "react";
import styles from "@styles/social/Social.module.scss";
import CardList from "@components/social/CardList";
import SocialHeader from "@components/social/SocialHeader";
import { useDebounce } from "@util/EventControlModule";

const SocialPage = () => {
  const [order, setOrder] = useState<"latest" | "popularity">("latest");
  const [searchKeyWord, setSearchKeyWord] = useState("");
  const debounceKeyword = useDebounce(searchKeyWord, 400);
  const scrollRef = useRef<HTMLDivElement>(null);

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
      <div ref={scrollRef} className={styles["item-list"]}>
        <CardList sort={order} nickname={debounceKeyword} scrollRef={scrollRef} />
      </div>
    </div>
  );
};

export default SocialPage;
