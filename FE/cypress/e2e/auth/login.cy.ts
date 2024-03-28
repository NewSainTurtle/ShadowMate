describe("로그인 화면", () => {
  beforeEach(() => {
    // 페이지를 /login으로 이동
    cy.visit("/login");

    cy.get("[data-cy=emailInput]").as("emailInput");
    cy.get("[data-cy=passwordInput]").as("passwordInput");
  });

  it("아이디와 비밀번호를 입력한 뒤 로그인 버튼을 클릭하여 /month으로 이동", () => {
    // when - 아이디와 비밀번호를 입력하고, 로그인 버튼을 클릭한다.
    cy.get("@emailInput").type("shadowmate127@naver.com");
    cy.get("@passwordInput").type("shadowmate127!");

    cy.get("@emailInput").invoke("val").should("eq", "shadowmate127@naver.com");
    cy.get("@passwordInput").invoke("val").should("eq", "shadowmate127!");

    cy.get("[data-cy=loginButton]").should("exist").click();

    // then - 로그인에 성공하고 캘린더 페이지로 이동한다.
    cy.url().should("eq", "http://localhost:3000/month");
  });

  it("아이디 및 비밀번호가 누락된 경우 경고 메시지 출력", () => {
    // when - 아이디와 비밀번호가 누락된 상태에서 로그인 버튼을 누른다.
    cy.get("@emailInput").invoke("val").should("eq", "");
    cy.get("@passwordInput").invoke("val").should("eq", "");

    cy.get("[data-cy=loginButton]").should("exist").click();

    // then - 캘린더 페이지로 이동하지 않고, 경고 메시지 (helpertext)가 뜬다.
    cy.get("#helper-text__email").should("be.visible").and("contain", "이메일을 입력해주세요.");
    cy.get("#helper-text__password").should("be.visible").and("contain", "비밀번호를 입력해주세요.");
    cy.url().should("eq", "http://localhost:3000/login");
  });

  it("아이디만 작성하고 로그인 버튼 클릭 시 비밀번호 경고 메시지 출력", () => {
    // when - 아이디만 작성, 비밀번호가 누락된 상태에서 로그인 버튼을 누른다.
    cy.get("@emailInput").type("shadowmate127@naver.com");

    cy.get("@emailInput").invoke("val").should("eq", "shadowmate127@naver.com");
    cy.get("@passwordInput").invoke("val").should("eq", "");

    cy.get("[data-cy=loginButton]").should("exist").click();

    // then - 아이디 경고 메시지는 뜨지 않음, 비밀번호 경고 메시지 (helpertext)만 뜬다.
    cy.get("#helper-text__email").should("not.exist");
    cy.get("#helper-text__password").should("be.visible").and("contain", "비밀번호를 입력해주세요.");
    cy.url().should("eq", "http://localhost:3000/login");
  });

  it("비밀번호만 작성하고 로그인 버튼 클릭 시 이메일 경고 메시지 출력", () => {
    // when - 비밀번호만 작성, 이메일이 누락된 상태에서 로그인 버튼을 누른다.
    cy.get("@passwordInput").type("shadowmate127!");

    cy.get("@emailInput").invoke("val").should("eq", "");
    cy.get("@passwordInput").invoke("val").should("eq", "shadowmate127!");

    cy.get("[data-cy=loginButton]").should("exist").click();

    // then - 비밀번호 경고 메시지는 뜨지 않음, 이메일 경고 메시지 (helpertext)만 뜬다.
    cy.get("#helper-text__email").should("be.visible").and("contain", "이메일을 입력해주세요.");
    cy.get("#helper-text__password").should("not.exist");
    cy.url().should("eq", "http://localhost:3000/login");
  });

  it("회원가입 버튼 클릭 시 회원가입 페이지로 이동", () => {
    cy.get("[data-cy=signupButton]").should("be.visible").click();
    cy.url().should("eq", "http://localhost:3000/signup");
  });
});
