import React from "react";
import styles from "@styles/common/Modal.module.scss";

interface Props {
  types: string;
}

const checkPostposition = (target: string) => {
  const charCode = target.charCodeAt(target.length - 1);
  const consonantCode = (charCode - 44032) % 28;
  if (consonantCode === 0) return `${target}를`;
  return `${target}을`;
};

const DeleteModal = ({ types }: Props) => {
  return <div className={styles["contents"]}>{checkPostposition(types)} 삭제하시겠습니까?</div>;
};

export default DeleteModal;
