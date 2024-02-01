import React, { ChangeEvent, KeyboardEvent, useCallback, useEffect, useRef, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";
import Avatar from "@components/common/Avatar";
import ArrowCircleUpIcon from "@mui/icons-material/ArrowCircleUp";
import Loading from "@components/common/Loading";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
import { DeleteOutline } from "@mui/icons-material";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId, selectUserInfo } from "@store/authSlice";
import { selectFriendId, setFriendInfo } from "@store/friendSlice";
import { plannerApi, userApi } from "@api/Api";
import { GuestBookConfig } from "@util/planner.interface";
import { useNavigate } from "react-router-dom";

const GuestBook = () => {
  const dispatch = useAppDispatch();
  const navigator = useNavigate();
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

  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const [deleteItem, setDeleteItem] = useState({ id: 0, idx: -1 });
  const handleDeleteModalOpen = () => setDeleteModalOpen(true);
  const handleDeleteModalClose = () => setDeleteModalOpen(false);

  const handleGuestBookChange = (e: ChangeEvent<HTMLInputElement>) => {
    const input = e.target.value;
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
        setIsEnd(list.length < 10);
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

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (guestBookInput === "") return;
    if (e.key == "Enter") {
      if (e.nativeEvent.isComposing) return;
      addGuestBook();
    }
  };

  const addGuestBook = async () => {
    const response = await plannerApi.addGuestBook(friendId, { visitorBookContent: guestBookInput });
    if (response.status === 200) {
      setGuestBookList([...guestBookList, response.data.data]);
      setGuestBookInput("");
    }
  };

  const deleteGuestBook = async (id: number, idx: number) => {
    const response = await plannerApi.deleteGuestBook(friendId, { visitorBookId: id });
    if (response.status === 200) {
      const newList = guestBookList.filter((_, i) => idx != i);
      setGuestBookList(newList);
      handleDeleteModalClose();
    }
  };

  const handleMoveToFriendProfile = (id: number, nickname: string) => {
    userApi
      .searches(userId, { nickname })
      .then((res) => {
        const response = res.data.data;
        dispatch(setFriendInfo(response));
        navigator("/month");
      })
      .catch((err) => {
        console.log(err);
      });
  };

  return (
    <>
      <div className={styles["guest"]}>
        <div ref={scrollBarRef} className={styles[`guest__contents${none}`]}>
          {guestBookList && guestBookList.length > 0 ? (
            <>
              {loading && <Loading />}
              {!isEnd && <div ref={obsRef} />}
              {guestBookList.map((item: GuestBookConfig, idx: number) => {
                const mine = friendId === userId || userInfo.nickname === item.visitorNickname ? "--mine" : "";
                return (
                  <div className={styles["guest__comment"]} key={item.visitorBookId}>
                    <Avatar
                      src={item.visitorProfileImage}
                      sx={{ width: 30, height: 30, cursor: "pointer" }}
                      onClick={() => handleMoveToFriendProfile(item.visitorId, item.visitorNickname)}
                    />
                    <div>
                      <div className={styles["guest__nickname"]}>
                        <div onClick={() => handleMoveToFriendProfile(item.visitorId, item.visitorNickname)}>
                          <Text bold types="small">
                            {item.visitorNickname}
                          </Text>
                        </div>
                        <div>
                          <Text types="small">{item.writeDateTime}</Text>
                          <div className={styles[`guest__delete${mine}`]}>
                            <DeleteOutline
                              onClick={() => {
                                setDeleteItem({ id: item.visitorBookId, idx });
                                handleDeleteModalOpen();
                              }}
                            />
                          </div>
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
              onKeyDown={handleOnKeyPress}
            />
            <div className={styles["guest__send"]} onClick={addGuestBook}>
              <ArrowCircleUpIcon />
            </div>
          </div>
        )}
      </div>
      <Modal
        types="twoBtn"
        open={deleteModalOpen}
        onClose={handleDeleteModalClose}
        onClick={() => deleteGuestBook(deleteItem.id, deleteItem.idx)}
        onClickMessage="삭제"
        warning
      >
        <DeleteModal types="방명록" />
      </Modal>
    </>
  );
};

export default GuestBook;
