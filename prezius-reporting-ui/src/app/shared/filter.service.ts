// convert the dates in a QdFilter string from UTC date/time to a local date
export function fixQdFilterDates(filterString: string): string {
  const decodedString: string = decodeURIComponent(filterString.substring(7)); // we skip "filter=" and decode the string
  const regExp: RegExp = /date\$[^|]*/i; // in the string, the date parameter starts with "date$", and parameters are separated by "|"
  const match: RegExpExecArray = regExp.exec(decodedString);

  // if no match, there is no date in the string, and we can return it as it is
  if (match == null) {
    return filterString;
  }

  const dates: string = match[0].substring(5); // we skip "date$"
  const separatorIndex: number = dates.indexOf('§'); // start and end dates are separated by "§"
  const startDate: string = convertDateString(dates.substring(0, separatorIndex));
  const endDate: string = convertDateString(dates.substring(separatorIndex + 1));
  const startString: string = decodedString.substring(0, match.index);
  const endString: string = decodedString.substring(match.index + match[0].length);

  // we rebuild the original string with the newly formatted dates
  const result: string = 'filter=' + encodeURIComponent(startString + 'date$' + startDate + '§' + endDate + endString);
  return result.replace(/!/g, '%21'); // encodeURIComponent doesn't encode "!", we have to do it manually
}

// convert a UTC date/time to a local date
// input:  "2025-06-19T22:00:00.000Z"
// output: "2025-06-20"
function convertDateString(dateString: string): string {
  // if there is no date in the filter, the string contains a single "!", thus we can just return it
  if (dateString.length == 1) {
    return dateString;
  }

  const date: Date = new Date(dateString);
  const year: string = String(date.getFullYear());
  const month: string = String(date.getMonth() + 1).padStart(2, '0'); // month is zero-indexed, thus we must add 1
  const day: string = String(date.getDate()).padStart(2, '0');

  return year + '-' + month + '-' + day;
}
