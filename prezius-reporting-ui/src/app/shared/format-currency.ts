export function formatCurrency(amount: number): string {
  if (amount == null) {
    return '-';
  }
  return 'CHF ' + amount.toFixed(2);
}
