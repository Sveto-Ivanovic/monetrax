const HAS_ZONE = /(Z|[+-]\d{2}:?\d{2})$/i

export function utcToZoned(utc: string | Date, timeZone: string): string {
    const date = typeof utc === 'string'
        ? new Date(HAS_ZONE.test(utc) ? utc : utc + 'Z')
        : utc

    const parts = new Intl.DateTimeFormat('en-CA', {
        timeZone,
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hourCycle: 'h23',
    }).formatToParts(date)

    const get = (type: string) => parts.find(p => p.type === type)!.value

    return `${get('year')}-${get('month')}-${get('day')}T${get('hour')}:${get('minute')}:${get('second')}`
}

function getOffsetMs(date: Date, timeZone: string): number {
    const wallClock = utcToZoned(date, timeZone)
    const wallClockAsUtc = new Date(wallClock + 'Z').getTime()
    const truncated = Math.floor(date.getTime() / 1000) * 1000
    return wallClockAsUtc - truncated
}

export function zonedToUtc(local: string, timeZone: string): Date {
    const asIfUtc = new Date(local + 'Z').getTime()

    const firstGuess = asIfUtc - getOffsetMs(new Date(asIfUtc), timeZone)
    const secondGuess = asIfUtc - getOffsetMs(new Date(firstGuess), timeZone)

    return new Date(secondGuess)
}