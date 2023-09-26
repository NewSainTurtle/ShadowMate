import React, { useEffect, useState } from "react";
import styles from "@styles/social/social.module.scss";
import CardItem from "@components/social/CardItem";
import { socialListData } from "@util/data/SocialData";
import { ProfileConfig } from "@components/common/FriendProfile";

export interface SocialDataType {
  socialId: number;
  socialImage: string;
  dailyPlannerDay: string;
  user: ProfileConfig;
}

const CardList = ({ search }: { search: string }) => {
  const [socialList, setSocialList] = useState<SocialDataType[]>(socialListData);

  return (
    <div className={styles["card-list"]}>
      {socialList.map((social, idx) => (
        <CardItem key={idx} item={social} />
      ))}
    </div>
  );
};

export default CardList;
