export function stringToColors(str: string) {
  let hash = 2166136261
  for (let i = 0; i < str.length; i++) {
    hash ^= str.charCodeAt(i)
    hash = Math.imul(hash, 16777619)
  }

  hash ^= hash >>> 16
  hash = Math.imul(hash, 2246822507)
  hash ^= hash >>> 13
  hash = Math.imul(hash, 3266489909)
  hash ^= hash >>> 16

  const h = hash >>> 0

  const hue = h % 360
  const saturation = 50 + ((h >>> 9) % 40)
  const darkLightness = 22 + ((h >>> 17) % 16)
  const lightLightness = 86 + ((h >>> 21) % 8)

  return {
    hue,
    light: `hsl(${hue}, ${saturation}%, ${lightLightness}%)`,
    dark: `hsl(${hue}, ${saturation}%, ${darkLightness}%)`,
  }
}

export const categoryTypeStyle: Record<string, { label: string; color: string }> = {
  INCOME:           { label: 'Income',           color: '#15803d' }, // dark green
  TRANSFER_FROM:    { label: 'Transfer from',    color: '#22c55e' }, // medium green
  ADJUSTMENT_PLUS:  { label: 'Adjustment plus',  color: '#65a30d' }, // lime green
  EXPENSE:          { label: 'Expense',          color: '#b91c1c' }, // dark red
  TRANSFER_TO:      { label: 'Transfer to',      color: '#ef4444' }, // bright red
  ADJUSTMENT_MINUS: { label: 'Adjustment minus', color: '#f97316' }, // red-orange
}