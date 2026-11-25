const HAS_ZONE = /(Z|[+-]\d{2}:?\d{2})$/i

export function formatDateTime(value: string): string {
  const date = new Date(HAS_ZONE.test(value) ? value : value + 'Z');

  return new Intl.DateTimeFormat('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    timeZone: 'UTC',
  }).format(date);
}