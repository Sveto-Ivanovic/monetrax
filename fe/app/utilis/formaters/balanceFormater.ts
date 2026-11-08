export function formatCurrency(amount: number, currency: string) {
    return new Intl.NumberFormat('de-DE', {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(amount);
}