import React, { useCallback, useEffect, useLayoutEffect, useRef, useState } from "react";
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

const CardList = ({ sort, nickname }: { sort: "latest" | "popularity"; nickname: string }) => {
  const userId = useAppSelector(selectUserId);
  const [list, setList] = useState<SocialListType[]>([]);
  const [load, setLoad] = useState(false);
  const [pageNumber, setPageNumber] = useState(1);
  const preventRef = useRef(true);
  const endRef = useRef(false);
  const obsRef = useRef(null);

  // 무한 스크롤
  useLayoutEffect(() => {
    preventRef.current = false;
    endRef.current = false;
    setPageNumber(1);
    getPost(1);
  }, [sort, nickname.length]);

  useEffect(() => {
    const observer = new IntersectionObserver(obsHandler);
    if (obsRef.current) observer.observe(obsRef.current);
    return () => observer.disconnect();
  }, []);

  useLayoutEffect(() => {
    if (pageNumber != 1) getPost(pageNumber);
  }, [pageNumber]);

  const obsHandler = async (entries: any[]) => {
    const target = entries[0];
    if (!endRef.current && target.isIntersecting && preventRef.current) {
      preventRef.current = false;
      setPageNumber((prev) => prev + 1);
    }
  };

  const getPost = useCallback(
    async (pageNumber: number) => {
      setLoad(true);
      try {
        let response: { totalPage: number; socialList: [] };
        if (nickname.length >= 2)
          response = (await socialApi.searches(userId, { nickname, sort, pageNumber })).data.data;
        else response = (await socialApi.getSocial(userId, { sort, pageNumber })).data.data;

        if (pageNumber == response.totalPage) endRef.current = true;
        if (pageNumber == 1) {
          if (response.totalPage >= 2) setPageNumber(2);
          setList(response.socialList);
        } else setList((prev) => [...prev, ...response.socialList]);
        preventRef.current = true;
      } catch (err) {
        console.error(err);
      } finally {
        setLoad(false);
      }
    },
    [pageNumber, sort, nickname.length],
  );

  // 내 공유 플래너 삭제
  const handleDelete = (idx: number, socialId: number) => {
    socialApi
      .delete(userId, socialId)
      .then(() => {
        const copyList = [...list];
        copyList.splice(idx, 1);
        setList(copyList);
      })
      .catch((err) => console.error(err));
  };

  return (
    <div className={styles["card-list"]}>
      {nickname.length >= 2 && list.length == 0 ? (
        <div className={styles["card-list--none"]}>검색된 결과가 존재하지 않습니다.</div>
      ) : (
        <>
          {list.map((social, idx) => (
            <CardItem key={idx.toString() + social.socialId} idx={idx} item={social} handleDelete={handleDelete} />
          ))}
        </>
      )}
      {load && <div className={styles["card-list--load"]} />}
      <div ref={obsRef} />
    </div>
  );
};

export default CardList;
