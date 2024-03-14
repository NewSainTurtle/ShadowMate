import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    experimentalStudio: true,
    baseUrl: "http://localhost:3000",
    setupNodeEvents(on, config) {},
    supportFile: "cypress/support/commands.ts",
  },
});
