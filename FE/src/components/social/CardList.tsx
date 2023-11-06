import React, { useCallback, useEffect, useRef, useState } from "react";
import styles from "@styles/social/Social.module.scss";
import CardItem from "@components/social/CardItem";
// import { socialListData } from "@util/data/SocialData";
import { ProfileConfig } from "@components/common/FriendProfile";
import { socialApi } from "@api/Api";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import Loading from "@components/common/Loading";

export interface SocialListType extends ProfileConfig {
  socialId: number;
  socialImage: string;
  dailyPlannerDay: string;
}

const CardList = ({ order, search }: { order: string; search: string }) => {
  const userId = useAppSelector(selectUserId);
  const obsRef = useRef(null);
  const [List, setList] = useState<SocialListType[]>([]);
  const [pageNumber, setPageNumber] = useState(1);
  const [load, setLoad] = useState(false);
  const preventRef = useRef(true);
  const endRef = useRef(false);

  useEffect(() => {
    const observer = new IntersectionObserver(obsHandler, { threshold: 0.5 });
    if (obsRef.current) observer.observe(obsRef.current);
    return () => {
      observer.disconnect();
    };
  }, []);

  useEffect(() => {
    if (pageNumber !== 1) getPost();
  }, [pageNumber]);

  const obsHandler = (entries: any[]) => {
    const target = entries[0];
    if (!endRef.current && target.isIntersecting && preventRef.current) {
      preventRef.current = false;
      setPageNumber((prev) => prev + 1);
    }
  };

  const getPost = useCallback(async () => {
    const sort = order == "최신순" ? "latest" : "popularity";
    setLoad(true);

    socialApi
      .getSocial(userId, { sort, pageNumber })
      .then((res) => {
        const response = res.data.data;
        console.log(response);
        if (pageNumber == response.totalPage) endRef.current = true;
        setList((prev) => [...prev, ...response.socialList]); //리스트 추가
      })
      .catch((err) => console.error(err))
      .finally(() => setLoad(false));
  }, [pageNumber]);

  return (
    <div className={styles["card-list"]}>
      {List.map((social, idx) => (
        <CardItem key={idx} item={social} />
      ))}
      {load && <Loading />}
      <div className="" ref={obsRef} />
    </div>
  );
};

export default CardList;
