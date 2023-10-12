import React, { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";

const MonthPage = () => {
  const [day, setDay] = useState("");

  return (
    <>
      <span>MonthPage</span>
      <p>
        <input
          type="date"
          value={day}
          onChange={(e) => {
            setDay(e.target.value);
          }}
        />
        <NavLink to={"/day"} state={{ date: day }}>
          이동
        </NavLink>
      </p>
    </>
  );
};

export default MonthPage;
