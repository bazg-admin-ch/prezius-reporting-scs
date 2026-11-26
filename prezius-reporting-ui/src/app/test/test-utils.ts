import * as path from 'path';

export function readJsonFileFromBack(fileName: string): any {
  const mockResponsePath = path.join(
    __dirname,
    '../../../../prezius-reporting-service/src/test/resources/rest-bodies/' + fileName
  );
  return JSON.parse(JSON.stringify(require(mockResponsePath)));
  // is needed to return a copy of the json, to prevent unwanted modifications of the file itself
}

export function readJsonFileFromFront(fileName: string): any {
  const mockResponsePath = path.join(__dirname, '../../../tests/data/' + fileName);
  return JSON.parse(JSON.stringify(require(mockResponsePath)));
  // is needed to return a copy of the json, to prevent unwanted modifications of the file itself
}
