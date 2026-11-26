import { formatCurrency } from './format-currency';

describe('formatCurrency', (): void => {
  it('when called, return the correct string', done => {
    expect(formatCurrency(0)).toEqual('CHF 0.00');
    expect(formatCurrency(12.55)).toEqual('CHF 12.55');
    expect(formatCurrency(-5.2)).toEqual('CHF -5.20');
    expect(formatCurrency(null)).toEqual('-');
    done();
  });
});
