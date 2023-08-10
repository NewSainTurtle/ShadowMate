import { cy, it } from "local-cypress";

describe("로그인", () => {
  it("아이디와 비밀번호를 입력한 뒤 버튼을 클릭하여 /home으로 이동하기", () => {
    // 페이지를 /login으로 이동
    cy.visit("/login");

    // id-input dom 요소 가져오는 것을 idInput으로 alias처리
    cy.get("[data-cy=id-input]").as("idInput");
    // idInput에 해당되는 alias에 '입력한 아이디'값을 입력
    cy.get("@idInput").type("입력한 아이디");
    // password-input dom 요소에 '1234'값을 입력
    cy.get("[data-cy=password-input]").type("1234");

    // id-input dom 요소에 입력되어 있는 value값을 추출한 뒤 '입력한 아이디'와 같은지 검증
    cy.get("[data-cy=id-input]").invoke("val").should("eq", "입력한 아이디");
    // password-input dom 요소로 만들어진 jquery object를 cypress object로 변환하여 value값이 '1234'인지 검증
    cy.get("[data-cy=password-input]").then(($passwordInput) => {
      cy.wrap($passwordInput).should("have.value", "1234");
    });

    // login-button dom 요소가 존재하는지 확인 후 클릭 이벤트 발생
    cy.get("[data-cy=login-button]").should("exist").click();
    // 페이지가 /home으로 이동되었는지 검증
    cy.url().should("eq", "http://localhost:3000/");
  });
});
