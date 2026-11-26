"use strict";
globalThis.ngJest = {
  skipNgcc: true,
};
module.exports = {
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/tests/setup-jest.ts'],
  coverageDirectory: '<rootDir>/coverage/sonarQube',
  collectCoverageFrom: ['<rootDir>/src/**/*.ts'],
  testResultsProcessor: 'jest-sonar-reporter',
  globalSetup: '<rootDir>/tests/global-setup-jest.js',
  roots: ['src'],
  prettierPath: null, // prettier 3 does not yet work with jest and inline snapshots
  collectCoverage: true,
  transformIgnorePatterns: ['node_modules/(?!.*\\.mjs$|@quadrel-enterprise-ui|@ngrx)'],
  coveragePathIgnorePatterns: [
    '/node_modules/',
    '/tests/',
    '/src/main\\.ts$',
    '/src/polyfills\\.ts$',
    '/src/app/app\\.module\\.ts$',
    '/src/app/app-routing\\.module\\.ts$',
    '/src/app/shared/routes/',
    '/src/environments/'
  ]
};
