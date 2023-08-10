import { type JestConfigWithTsJest } from "ts-jest";

const jestConfig: JestConfigWithTsJest = {
  preset: "ts-jest",
  testEnvironment: "jsdom",
  testMatch: ["**/__tests__/**/*.test.(ts|tsx)"],
  transform: {
    "^.+\\.(ts|tsx)$": [
      "ts-jest",
      {
        babel: true,
        tsconfig: "tsconfig.jest.json",
      },
    ],
  },
  modulePaths: ["<rootDir>"],
  moduleNameMapper: {
    "\\.(css|scss)$": "identity-obj-proxy",
    "^@api/(.*)$": "<rootDir>/src/api/$1",
    "^@hooks/(.*)$": "<rootDir>/src/hooks/$1",
    "^@assets/(.*)$": "<rootDir>/src/assets/$1",
    "^@components/(.*)$": "<rootDir>/src/components/$1",
    "^@features/(.*)$": "<rootDir>/src/features/$1",
    "^@pages/(.*)$": "<rootDir>/src/pages/$1",
    "^@store/(.*)$": "<rootDir>/src/store/$1",
    "^@styles/(.*)$": "<rootDir>/src/styles/$1",
  },
  setupFilesAfterEnv: ["<rootDir>/setupTest.ts"],
  moduleFileExtensions: ["ts", "tsx", "js", "jsx"],
};

export default jestConfig;
