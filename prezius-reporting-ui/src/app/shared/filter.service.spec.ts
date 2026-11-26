import { fixQdFilterDates } from './filter.service';

describe('fixQdFilterDates', (): void => {
  it('when called with param before and after, return the correct string', done => {
    const filterString: string =
      'filter=officeName%24ZEMK%7Cdate%242025-04-30T10%3A00%3A00.000Z%C2%A72025-05-21T10%3A00%3A00.000Z%7CTest$Test';

    const result = fixQdFilterDates(filterString);

    expect(result).toEqual('filter=officeName%24ZEMK%7Cdate%242025-04-30%C2%A72025-05-21%7CTest%24Test');
    done();
  });

  it('when called with param before, return the correct string', done => {
    const filterString: string =
      'filter=officeName%24ZEMK%7Cdate%242025-04-30T10%3A00%3A00.000Z%C2%A72025-05-21T10%3A00%3A00.000Z';

    const result = fixQdFilterDates(filterString);

    expect(result).toEqual('filter=officeName%24ZEMK%7Cdate%242025-04-30%C2%A72025-05-21');
    done();
  });

  it('when called with param after, return the correct string', done => {
    const filterString: string =
      'filter=date%242025-04-30T10%3A00%3A00.000Z%C2%A72025-05-21T10%3A00%3A00.000Z%7CTest$Test';

    const result = fixQdFilterDates(filterString);

    expect(result).toEqual('filter=date%242025-04-30%C2%A72025-05-21%7CTest%24Test');
    done();
  });

  it('when called without other param, return the correct string', done => {
    const filterString: string = 'filter=date%242025-04-30T10%3A00%3A00.000Z%C2%A72025-05-21T10%3A00%3A00.000Z';

    const result = fixQdFilterDates(filterString);

    expect(result).toEqual('filter=date%242025-04-30%C2%A72025-05-21');
    done();
  });

  it('when called with no start date, return the correct string', done => {
    const filterString: string = 'filter=date%24%21%C2%A72025-06-16T10%3A00%3A00.000Z';

    const result = fixQdFilterDates(filterString);

    expect(result).toEqual('filter=date%24%21%C2%A72025-06-16');
    done();
  });

  it('when called with no end date, return the correct string', done => {
    const filterString: string = 'filter=date%242025-06-16T10%3A00%3A00.000Z%C2%A7%21';

    const result = fixQdFilterDates(filterString);

    expect(result).toEqual('filter=date%242025-06-16%C2%A7%21');
    done();
  });

  it('when called with no date, return the correct string', done => {
    const filterString: string = 'filter=officeName%24ZEMK';

    const result = fixQdFilterDates(filterString);

    expect(result).toEqual('filter=officeName%24ZEMK');
    done();
  });
});
