import React, { ChangeEvent, useCallback, useEffect, useRef, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";
import Avatar from "@components/common/Avatar";
import ArrowCircleUpIcon from "@mui/icons-material/ArrowCircleUp";
import { DeleteOutline } from "@mui/icons-material";
import { useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo } from "@store/authSlice";
import { selectFriendId } from "@store/friendSlice";
import { plannerApi } from "@api/Api";
import { GuestBookConfig } from "@util/planner.interface";
import Loading from "@components/common/Loading";

const GuestBook = () => {
  const userInfo = useAppSelector(selectUserInfo);
  const userId = useAppSelector(selectUserId);
  let friendId = useAppSelector(selectFriendId);
  friendId = friendId != 0 ? friendId : userId;
  const none = friendId != userId ? "" : "--none";
  const [guestBookList, setGuestBookList] = useState<GuestBookConfig[]>([]);
  const [guestBookInput, setGuestBookInput] = useState<string>("");
  const [last, setLast] = useState(0);
  const preventRef = useRef(false);
  const endRef = useRef<HTMLDivElement | null>(null);
  const obsRef = useRef<HTMLDivElement | null>(null);
  const scrollBarRef = useRef<HTMLDivElement | null>(null);

  const [isEnd, setIsEnd] = useState<boolean>(false);
  const [min, setMin] = useState(0);
  const [prevScrollHeight, setPrevScrollHeight] = useState<number | null | undefined>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const maxLength = 30;

  const handleGuestBookChange = (e: ChangeEvent<HTMLInputElement>) => {
    let input = e.target.value;
    if (input.length > maxLength) input.slice(0, maxLength);
    setGuestBookInput(input);
  };

  const getGuestBook = useCallback(
    async (last: number) => {
      setLoading(true);
      const response = await plannerApi.getGuestBook(friendId, { last: last });
      if (response.status === 200) {
        const list = response.data.data.visitorBookResponses;
        if (list.length > 0) {
          list.sort((a: GuestBookConfig, b: GuestBookConfig) => Number(a.visitorBookId) - Number(b.visitorBookId));
          const min = list[0].visitorBookId;
          setMin(min);
        }
        if (last === 0) setGuestBookList(list);
        else setGuestBookList((prev) => [...list, ...prev]);
        preventRef.current = true;
        setIsEnd(list.length < 10 ? true : false);
      }
      setLoading(false);
    },
    [friendId],
  );

  useEffect(() => {
    if (scrollBarRef.current && prevScrollHeight) {
      scrollBarRef.current?.scrollTo(0, scrollBarRef.current?.scrollHeight - prevScrollHeight);
    }
    setPrevScrollHeight(null);
  }, [guestBookList.length]);

  const handleObserver = async ([entry]: IntersectionObserverEntry[], observer: IntersectionObserver) => {
    if (!isEnd && entry.isIntersecting && preventRef.current) {
      setPrevScrollHeight(scrollBarRef.current?.scrollHeight);
      preventRef.current = false;
      setLast(min);
    }
  };

  useEffect(() => {
    if (!obsRef.current) return;
    const option = {
      threshold: 1,
    };
    const observer = new IntersectionObserver(handleObserver, option);
    if (obsRef.current) {
      observer.observe(obsRef.current);
    }
    return () => observer.disconnect();
  }, [handleObserver, isEnd]);

  useEffect(() => {
    if (last != 0) {
      getGuestBook(last);
    }
  }, [last]);

  useEffect(() => {
    setMin(0);
    setLast(0);
    getGuestBook(0);
  }, [friendId]);

  useEffect(() => {
    // 맨 처음 로딩 시 스크롤이 제일 하단에 위치.
    if (endRef.current && last == 0) {
      endRef.current.scrollIntoView();
    }
  }, [friendId, guestBookList]);

  const addGuestBook = async () => {
    const response = await plannerApi.addGuestBook(friendId, { visitorBookContent: guestBookInput });
    if (response.status === 200) {
      setGuestBookList([...guestBookList, response.data.data]);
      setGuestBookInput("");
    }
  };

  const deleteGuestBook = async (item: GuestBookConfig, idx: number) => {
    const response = await plannerApi.deleteGuestBook(friendId, { visitorBookId: item.visitorBookId });
    if (response.status === 200) {
      let newList = guestBookList.filter((_, i) => idx != i);
      setGuestBookList(newList);
    }
  };

  return (
    <div className={styles["guest"]}>
      <div ref={scrollBarRef} className={styles[`guest__contents${none}`]}>
        {guestBookList && guestBookList.length > 0 ? (
          <>
            {loading && <Loading />}
            {!isEnd && <div ref={obsRef} />}
            {guestBookList.map((item: GuestBookConfig, idx: number) => {
              return (
                <div className={styles["guest__comment"]} key={item.visitorBookId}>
                  <Avatar src={item.visitorProfileImage} sx={{ width: 30, height: 30 }} />
                  <div>
                    <div className={styles["guest__nickname"]}>
                      <Text bold types="small">
                        {item.visitorNickname}
                      </Text>
                      <div>
                        <Text types="small">{item.writeDateTime}</Text>
                        {(friendId === userId || userInfo.nickname === item.visitorNickname) && (
                          <DeleteOutline onClick={() => deleteGuestBook(item, idx)} />
                        )}
                      </div>
                    </div>
                    <Text types="small">{item.visitorBookContent}</Text>
                  </div>
                </div>
              );
            })}
            <div ref={endRef} />
          </>
        ) : (
          <div className={styles["guest__none"]}>
            <Text>방명록이 없습니다.</Text>
          </div>
        )}
      </div>
      {friendId != userId && (
        <div className={styles["guest__input"]}>
          <input
            maxLength={maxLength}
            value={guestBookInput}
            placeholder="방명록을 남겨주세요."
            onChange={handleGuestBookChange}
          />
          <div className={styles["guest__send"]} onClick={addGuestBook}>
            <ArrowCircleUpIcon />
          </div>
        </div>
      )}
    </div>
  );
};

export default GuestBook;
