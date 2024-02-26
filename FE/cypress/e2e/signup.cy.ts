import { cy, it } from "local-cypress";

describe("회원가입 화면", async () => {
  beforeEach(() => {
    cy.visit("/signup");
  });

  it("회원가입 약관 비동의시 Next 버튼 비활성화", () => {
    cy.get("[data-cy=nextButton]").click();

    cy.get("[data-cy=conditionsAgreed]").check();
    cy.get("[data-cy=nextButton]").click();

    cy.get("[data-cy=conditionsAgreed]").uncheck();
    cy.get("[data-cy=personalInfoAgreed]").check();
    cy.get("[data-cy=nextButton]").click();
  });

  it("회원가입 약관 모두 동의시 Next 버튼 활성화", () => {
    cy.get('[data-cy="allAgreedCheckbox"]').check();
    cy.get("[data-cy=nextButton]").click();
  });
});
