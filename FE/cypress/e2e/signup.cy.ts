import { cy, it } from "local-cypress";

describe("회원가입 화면", async () => {
  beforeEach(() => {
    cy.visit("/signup");

    // 회원가입 약관 동의
    cy.get('[data-cy="allAgreedCheckbox"]').check();
    cy.get("[data-cy=nextButton]").click();

    cy.get("[data-cy=emailInput]").as("emailInput");
    cy.get("[data-cy=passwordInput]").as("passwordInput");
    cy.get("[data-cy=passwordCheckInput]").as("passwordCheckInput");
    cy.get("[data-cy=nicknameInput]").as("nicknameInput");
  });

  /** 이메일 중복 검사 */
  function emailAuthentication(email: string) {
    cy.get("@emailInput").clear().type(email);
    cy.intercept("POST", "/api/auth/email-authentication");
    cy.get('[data-cy="emailButton"]').click();
  }

  /** 이메일 인증 코드 검사 */
  function emailAuthenticationCheck(code: string) {
    cy.intercept("POST", "/api/auth/email-authentication/check", (req) => {
      req.reply({
        statusCode: 200,
      });
    });
    cy.get("[data-cy=emailCodeInput]").clear().type(code);
    cy.get("[data-cy=emailCodeButton]").click();
  }

  /** 닉네임 중복 검사 */
  function nicknameAuthentication(nickanme: string) {
    // when - 닉네임 중복 검사 성공
    cy.intercept("POST", "/api/auth/nickname-duplicated", (req) => {
      req.reply({
        statusCode: 200,
      });
    });
    cy.get("[data-cy=nicknameInput]").clear().type(nickanme);
    cy.get("[data-cy=nicknameButton]").click();
  }

  /** 이메일 테스트 */
  it("이메일 형식이 아닌 경우 경고 메세지 출력", () => {
    cy.get("@emailInput").clear().type("shadowmate");
    cy.get("@emailInput").blur();
    cy.contains("이메일 형식이 올바르지 않습니다.");
  });

  it("중복 이메일인 경우 경고 메세지 출력", () => {
    cy.get("@emailInput").clear().type("shadowmate127@naver.com");
    cy.get("[data-cy=emailButton]").click();
    cy.contains("중복된 이메일입니다.").should("be.visible");
  });

  /** 이메일 인증 코드 테스트 */
  it("이메일 중복 확인 후 인증 코드 실패인 경우 경고 메세지 출력", () => {
    emailAuthentication("test@naver.com");
    cy.get("[data-cy=emailCodeInput]").clear().type("code12");
    cy.get("[data-cy=emailCodeButton]").click();
    cy.contains("이메일 인증 코드가 일치하지 않습니다.").should("be.visible");
  });

  /** 비밀번호 */
  it("비밀번호가 6글자 미만인 경우 경고 메세지 출력", () => {
    cy.get("@passwordInput").clear().type("test").blur();
    cy.contains("6~20자의 영문, 숫자, 특수문자(!?^&*@#)를 사용해 주세요.");
  });

  it("비밀번호가 , 20글자 초과인 경우 20글자까지만 보이기", () => {
    cy.get("@passwordInput").clear().type("test127test127test127").blur();
    cy.get("@passwordInput").invoke("val").should("have.length", 20);
  });

  it("비밀번호와 비밀번호 확인 값이 같지 않을 경우 경고 메세지 출력", () => {
    cy.get("@passwordInput").clear().type("testUser");
    cy.get("@passwordCheckInput").clear().type("testUser127").blur();
    cy.contains("비밀번호가 일치하지 않습니다.");
  });

  /** 닉네임 */
  it("닉네임이 특수문자를 포함한 경우 경고 메세지 출력", () => {
    cy.get("@nicknameInput").clear().type("user⭐").blur();
    cy.contains("2~10자의 특수문자를 제외한 문자를 사용해 주세요.");
  });

  it("닉네임이 2글자 미만일 경우 경고 메세지 출력", () => {
    cy.get("@nicknameInput").clear().type("t").blur();
    cy.contains("2~10자의 특수문자를 제외한 문자를 사용해 주세요.");
  });

  it("닉네임이 10글자 초과인 경우 10글자까지만 보이기", () => {
    cy.get("@nicknameInput").clear().type("testUsertestUser").blur();
    cy.get("@nicknameInput").invoke("val").should("have.length", 10);
  });

  it("중복된 닉네임일 경우 경고 메세지 출력", () => {
    cy.get("@nicknameInput").clear().type("섀도우메이트");
    cy.get("[data-cy=nicknameButton]").click();
    cy.contains("중복된 닉네임입니다.").should("be.visible");
  });

  const email = "test127@shadowmate.com";
  const password = "testUser127";
  const code = "code12";
  const nickname = "섀도우";

  it("모든 회원정보를 입력하고 인증을 안 했을 경우 경고 메세지 출력", () => {
    cy.get("@emailInput").type(email);
    cy.get("@passwordInput").type(password);
    cy.get("@passwordCheckInput").type(password);
    cy.get("@nicknameInput").type(nickname);

    // when - 이메일 중복 검사를 안 했을 경우
    cy.get("[data-cy=signupButton]").click();
    // then
    cy.contains("메일을 인증을 해주세요.").should("be.visible");

    // when - 이메일 인증을 안 했을 경우
    emailAuthentication(email);
    cy.get("[data-cy=emailCodeInput]").clear().type(code);
    cy.get("[data-cy=signupButton]").click();
    // then
    cy.contains("인증코드를 확인 해주세요.").should("be.visible");

    // when - 닉네임 중복검사를 안 했을 경우
    emailAuthentication(email);
    emailAuthenticationCheck(code);
    cy.get("[data-cy=signupButton]").click();
    // then
    cy.contains("닉네임 중복을 확인 해주세요.").should("be.visible");
  });

  it("회원정보를 입력과 인증을 모두 완료한 뒤에 가입 버튼을 누르면 /login 화면으로 이동", () => {
    cy.intercept("POST", "/api/auth/join", (req) => {
      req.reply({
        statusCode: 200,
      });
    });

    // when - 회원정보 입력과 인증을 완료하고, 회원가입 버튼을 누른다.
    emailAuthentication(email);
    emailAuthenticationCheck(code);
    cy.get("@passwordInput").type(password);
    cy.get("@passwordCheckInput").type(password);
    nicknameAuthentication(nickname);
    cy.get("[data-cy=signupButton]").click();

    // then - 회원가입에 성공하고 로그인 페이지로 이동한다.
    cy.url().should("eq", "http://localhost:3000/login");
  });

  it("로그인 버튼 클릭 시 로그인 페이지로 이동", () => {
    cy.get("[data-cy=loginButton]").should("be.visible").click();
    cy.url().should("eq", "http://localhost:3000/login");
  });
});
