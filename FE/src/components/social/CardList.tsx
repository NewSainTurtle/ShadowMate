import React, { useEffect, useRef, useState } from "react";
import styles from "@styles/social/Social.module.scss";
import CardItem from "@components/social/CardItem";
import { ProfileConfig } from "@components/common/FriendProfile";
import { socialApi } from "@api/Api";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";

export interface SocialListType extends ProfileConfig {
  socialId: number;
  socialImage: string;
  dailyPlannerDay: string;
}

const CardList = ({ sort, search: nickname }: { sort: "latest" | "popularity"; search: string }) => {
  const userId = useAppSelector(selectUserId);
  const [list, setList] = useState<SocialListType[]>([]);
  const [pageNumber, setPageNumber] = useState(1);
  const preventRef = useRef(true);
  const endRef = useRef(false);
  const obsRef = useRef(null);

  useEffect(() => {
    preventRef.current = false;
    endRef.current = false;
    setPageNumber(1);
    getPost();
  }, [sort, nickname.length]);

  useEffect(() => {
    const observer = new IntersectionObserver(obsHandler);
    if (obsRef.current) observer.observe(obsRef.current);
    return () => observer.disconnect();
  }, []);

  const obsHandler = async (entries: any[]) => {
    const target = entries[0];
    if (!endRef.current && target.isIntersecting && preventRef.current) {
      preventRef.current = false;
      setPageNumber((prev) => prev + 1);
    }
  };

  useEffect(() => {
    if (pageNumber != 1) getPost();
  }, [pageNumber]);

  const getPost = async () => {
    if (nickname.length >= 2) {
      socialApi
        .searches(userId, { nickname, sort, pageNumber })
        .then((res) => {
          const response = res.data.data;
          if (pageNumber == response.totalPage) endRef.current = true;
          if (pageNumber == 1) setList(response.socialList);
          else setList((prev) => [...prev, ...response.socialList]);
          preventRef.current = true;
        })
        .catch((err) => console.error(err));
    } else {
      socialApi
        .getSocial(userId, { sort, pageNumber })
        .then((res) => {
          const response = res.data.data;
          if (pageNumber == response.totalPage) endRef.current = true;
          if (pageNumber == 1) setList(response.socialList);
          else setList((prev) => [...prev, ...response.socialList]);
          preventRef.current = true;
        })
        .catch((err) => console.error(err));
    }
  };

  return (
    <div className={styles["card-list"]}>
      {list.map((social, idx) => (
        <CardItem key={social.socialId + idx} item={social} />
      ))}

      {!list.length && <span>검색된 결과가 존재하지 않습니다.</span>}

      <div ref={obsRef} style={{ backgroundColor: "lightpink" }} />
    </div>
  );
};

export default CardList;
