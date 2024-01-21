import React, { useRef } from "react";
import styles from "@styles/social/Social.module.scss";
import CardList from "@components/social/CardList";
import SocialHeader from "@components/social/SocialHeader";

const SocialPage = () => {
  const scrollRef = useRef<HTMLDivElement>(null);

  return (
    <div className={styles["page-container"]}>
      <div className={styles["item-header"]}>
        <SocialHeader />
      </div>
      <div ref={scrollRef} className={styles["item-list"]}>
        <CardList scrollRef={scrollRef} />
      </div>
    </div>
  );
};

export default SocialPage;
