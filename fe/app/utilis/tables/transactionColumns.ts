import { h } from 'vue'
import { UButton, UDropdownMenu, UInput, UInputDate, UPopover, USelectMenu } from '#components'
import type { DropdownMenuItem, TableColumn, TableRow } from '@nuxt/ui'
import { formatCurrency } from '~/utilis/formaters/balanceFormater'
import { categoryTypeStyle, stringToColors } from '~/utilis/formaters/colorGetter'
import type { Column } from '@tanstack/vue-table'
import {
    today, startOfMonth, endOfMonth, startOfWeek, endOfWeek,
    startOfYear, endOfYear, type CalendarDate
} from '@internationalized/date'

export type ColTypes = {
    transactionId: string
    name: string
    description: string | null
    amount: string
    categories: string[]
    date: string
    categoryType: string
    currency: string
    rawAmount: number
    rawDate: string
}

function sortButton(column: Column<ColTypes>, label: string) {
    const s = column.getIsSorted()
    return h(UButton, {
        color: 'neutral',
        variant: 'ghost',
        label,
        icon: s
            ? s === 'asc' ? 'i-lucide-arrow-up-narrow-wide' : 'i-lucide-arrow-down-wide-narrow'
            : 'i-lucide-arrow-up-down',
        class: '-mx-2.5',
        onClick: () => column.toggleSorting(s === 'asc')
    })
}

function filterSearchNameDesc(column: Column<ColTypes>) {
    const active = !!column.getFilterValue()

    return h(UPopover, null, {
        default: () =>
            h(UButton, {
                icon: 'i-lucide-ellipsis-vertical',
                size: 'xs',
                color: active ? 'primary' : 'neutral',
                variant: active ? 'soft' : 'ghost',
                'aria-label': 'Column filter',
            }),

        content: () =>
            h('div', { class: 'p-2 w-56 flex flex-col gap-2' }, [
                h(UInput as Component, {
                    modelValue: (column.getFilterValue() as string) ?? '',
                    'onUpdate:modelValue': (v: string) => column.setFilterValue(v || undefined),
                    placeholder: 'Filter...',
                }),
                h(UButton, {
                    size: 'xs',
                    color: 'neutral',
                    variant: 'outline',
                    label: 'Clear',
                    class: 'self-end',
                    onClick: () => column.setFilterValue(undefined),
                }),
            ]),
    })
}



type AmountFilter = { min?: number; max?: number }

function filterSearcAmount(column: Column<ColTypes>) {
    const current = () => (column.getFilterValue() as AmountFilter | undefined) ?? {}
    const active = current().min != null || current().max != null

    const update = (key: 'min' | 'max', raw: string | number | null) => {
        const num = raw === '' || raw == null ? undefined : Number(raw)
        const next: AmountFilter = { ...current(), [key]: Number.isNaN(num) ? undefined : num }

        column.setFilterValue(next.min == null && next.max == null ? undefined : next)
    }
    return h(UPopover, null, {
        default: () =>
            h(UButton, {
                icon: 'i-lucide-ellipsis-vertical',
                size: 'xs',
                color: active ? 'primary' : 'neutral',
                variant: active ? 'soft' : 'ghost',
                'aria-label': 'Amount filter',
            }),

        content: () =>
            h('div', { class: 'p-2 w-56 flex flex-col gap-2' }, [
                h(UInput as Component, {
                    modelValue: current().min ?? '',
                    'onUpdate:modelValue': (v: string | number) => update('min', v),
                    placeholder: 'Min amount...',
                    type: 'number',
                }),
                h(UInput as Component, {
                    modelValue: current().max ?? '',
                    'onUpdate:modelValue': (v: string | number) => update('max', v),
                    placeholder: 'Max amount...',
                    type: 'number',
                }),
                h(UButton, {
                    size: 'xs',
                    color: 'neutral',
                    variant: 'outline',
                    label: 'Clear',
                    class: 'self-end',
                    onClick: () => column.setFilterValue(undefined),
                }),
            ]),
    })
}



function filterCategories(column: Column<ColTypes>, options: string[]) {
    const current = () => (column.getFilterValue() as string[] | undefined) ?? []
    const active = current().length > 0

    return h(UPopover, null, {
        default: () =>
            h(UButton, {
                icon: 'i-lucide-ellipsis-vertical',
                size: 'xs',
                color: active ? 'primary' : 'neutral',
                variant: active ? 'soft' : 'ghost',
                'aria-label': 'Categories filter',
            }),

        content: () =>
            h('div', { class: 'p-2 w-64 flex flex-col gap-2' }, [
                h(USelectMenu as Component, {
                    modelValue: current(),
                    multiple: true,
                    items: options,
                    placeholder: 'Pick categories...',
                    class: 'w-full',
                    'onUpdate:modelValue': (v: string[]) =>
                        column.setFilterValue(v.length ? v : undefined),
                }),
                h(UButton, {
                    size: 'xs',
                    color: 'neutral',
                    variant: 'outline',
                    label: 'Clear',
                    class: 'self-end',
                    onClick: () => column.setFilterValue(undefined),
                }),
            ]),
    })
}


type DateFilter = {
    start?: CalendarDate;
    end?: CalendarDate;
}


function datePresets(tz: string): { label: string; range: DateFilter }[] {
    const t = today(tz)
    const lastMonth = t.subtract({ months: 1 })
    const lastYear = t.subtract({ years: 1 })

    return [
        { label: 'Today', range: { start: t, end: t } },
        { label: 'Yesterday', range: { start: t.subtract({ days: 1 }), end: t.subtract({ days: 1 }) } },
        { label: 'Last 7 days', range: { start: t.subtract({ days: 6 }), end: t } },
        { label: 'Last 30 days', range: { start: t.subtract({ days: 29 }), end: t } },
        { label: 'This week', range: { start: startOfWeek(t, 'en-GB'), end: endOfWeek(t, 'en-GB') } },
        { label: 'This month', range: { start: startOfMonth(t), end: endOfMonth(t) } },
        { label: 'Last month', range: { start: startOfMonth(lastMonth), end: endOfMonth(lastMonth) } },
        { label: 'Last 3 months', range: { start: t.subtract({ months: 3 }), end: t } },
        { label: 'This year', range: { start: startOfYear(t), end: endOfYear(t) } },
        { label: 'Last year', range: { start: startOfYear(lastYear), end: endOfYear(lastYear) } },
    ]
}


function filterSearchDate(column: Column<ColTypes>, timeZone: string) {
    const current = () => (column.getFilterValue() as DateFilter | undefined) ?? {}
    const active = current().start != null || current().end != null

    const update = (v: DateFilter | null | undefined) =>
        column.setFilterValue(v?.start || v?.end ? v : undefined)

    const isSame = (r: DateFilter) =>
        r.start?.toString() === current().start?.toString() &&
        r.end?.toString() === current().end?.toString()

    return h(UPopover, null, {
        default: () =>
            h(UButton, {
                icon: 'i-lucide-ellipsis-vertical',
                size: 'xs',
                color: active ? 'primary' : 'neutral',
                variant: active ? 'soft' : 'ghost',
                'aria-label': 'Date filter',
            }),

        content: () =>
            h('div', { class: 'p-2 w-72 flex flex-col gap-2' }, [
                h('div', { class: 'grid grid-cols-2 gap-1' },
                    datePresets(timeZone).map(p =>
                        h(UButton, {
                            key: p.label,
                            size: 'xs',
                            color: isSame(p.range) ? 'primary' : 'neutral',
                            variant: isSame(p.range) ? 'soft' : 'outline',
                            label: p.label,
                            onClick: () => update(p.range),
                        })
                    )
                ),
                h(UInputDate as Component, {
                    range: true,
                    modelValue: current(),
                    'onUpdate:modelValue': (v: DateFilter | null | undefined) => update(v),
                }),
                h(UButton, {
                    size: 'xs',
                    color: 'neutral',
                    variant: 'outline',
                    label: 'Clear',
                    class: 'self-end',
                    onClick: () => column.setFilterValue(undefined),
                }),
            ]),
    })
}





export function getTransactionColumns(

    getRowItems: (row: TableRow<ColTypes>) => DropdownMenuItem[],
    accountCurrency: string, timezone?: string, categories?: string[]
): TableColumn<ColTypes>[] {



    return [
        {
            accessorKey: 'name',
            enableSorting: true,
            header: ({ column }) => h('div', { class: 'flex items-center gap-1' }, [
                sortButton(column, 'Name'),
                filterSearchNameDesc(column),
            ]),
        },
        {
            accessorKey: 'description', enableSorting: true,
            header: ({ column }) => h('div', { class: 'flex items-center gap-1' }, [
                sortButton(column, 'Description'),
                filterSearchNameDesc(column),
            ]),
            cell: ({ row }) => {
                const description = row.getValue<string | null>('description')

                if (!description) {
                    return h('span', { class: 'text-muted' }, '—')
                }

                return h(
                    'div',
                    {
                        title: description,
                        class: 'line-clamp-3 max-w-70 whitespace-normal wrap-break-word',
                    },
                    description
                )
            },
        },

        {
            accessorKey: 'amount',
            enableSorting: true,
            sortingFn: (a, b) => a.original.rawAmount - b.original.rawAmount,
            filterFn: (row, _columnId, value: { min?: number; max?: number }) => {
                const val = row.original.rawAmount;
                if (value.min == null && value.max == null)
                    return true
                else if (value.min != null && value.max != null)
                    return val <= value.max && val >= value.min
                else if (value.min != null)
                    return val >= value.min
                else if (value.max != null)
                    return val <= value.max
                else
                    return true

            },
            meta: {
                class: {
                    th: 'text-right',
                    td: 'text-right font-medium'
                }
            },
            footer: ({ table }) => {
                const INCOME_TYPES = new Set(['INCOME', 'TRANSFER_FROM', 'ADJUSTMENT_PLUS'])

                let income = 0
                let expense = 0

                for (const row of table.getFilteredRowModel().rows) {
                    const { rawAmount, categoryType } = row.original
                    if (INCOME_TYPES.has(categoryType)) income += rawAmount
                    else expense += rawAmount
                }

                const net = income - expense

                const parts = [
                    { label: 'Net', value: net, color: '#00347e' },
                    { label: 'Income', value: income, color: '#227003' },
                    { label: 'Expense', value: expense, color: '#8a1c14' },
                ].filter(p => p.value !== 0)

                return h('div', { class: 'flex flex-col items-end gap-0.5 font-semibold' },
                    parts.map(p =>
                        h('span', { key: p.label, style: { color: p.color } },
                            `${p.label}: ${formatCurrency(p.value, accountCurrency)}`
                        )
                    )
                )
            },
            header: ({ column }) => h('div', { class: 'flex items-center gap-1' }, [
                sortButton(column, 'Amount'),
                filterSearcAmount(column),
            ]),
        },

        {
            accessorKey: "categoryType",
            header: ({ column }) => h('div', { class: 'flex items-center gap-1' }, [
                sortButton(column, 'Category Type'),
                filterCategories(column, ['INCOME',
                    'EXPENSE',
                    'TRANSFER_FROM',
                    'TRANSFER_TO',
                    'ADJUSTMENT_PLUS',
                    'ADJUSTMENT_MINUS']),
            ]),
            enableSorting: true,
            cell: ({ row }) => {
                const type = row.getValue<string>('categoryType')
                const entry = categoryTypeStyle[type]

                return h(
                    'span',
                    {
                        class: 'font-semibold whitespace-nowrap',
                        style: { color: entry?.color ?? 'inherit' },
                    },
                    entry?.label ?? type
                )
            },

        },

        {
            id: 'categories',
            accessorFn: (row) => row.categories.join('* ') + '*',
            header: ({ column }) => h('div', { class: 'flex items-center gap-1' }, [
                h('div', null, 'Categories'),
                filterCategories(column, categories ?? ['']),
            ]),
            enableSorting: true,
            filterFn: (row, _id, selected: string[]) =>
                !selected?.length || row.original.categories.some(c => selected.includes(c)),

            cell: ({ row }) => {
                const categories = row.original.categories ?? []

                if (!categories.length) {
                    return h('span', { class: 'text-muted' }, '—')
                }

                return h('div',
                    { class: 'flex flex-wrap gap-1' },
                    categories.map((name) => {
                        const { light, dark } = stringToColors(name)

                        return h('span',
                            {
                                key: name, class: 'global-category', style: {
                                    '--category-light': light,
                                    '--category-dark': dark,
                                }
                            },
                            name
                        )
                    }))
            }
        },
        {
            accessorKey: 'date',
            sortingFn: (a, b) => a.original.rawDate.localeCompare(b.original.rawDate),
            header: ({ column }) => h('div', { class: 'flex items-center gap-1' }, [
                sortButton(column, 'Date'),
                filterSearchDate(column, timezone ?? 'UTC'),
            ]),
            enableSorting: true,
            filterFn: (row, _columnId, value: { start?: CalendarDate; end?: CalendarDate }) => {
                const d = row.original.rawDate.slice(0, 10);
                const start = value.start?.toString()
                const end = value.end?.toString()

                if (start == null && end == null)
                    return true;
                else if (start != null && end != null)
                    return d >= start && d <= end;
                else if (start != null)
                    return d >= start;
                else if (end != null)
                    return d <= end;
                else
                    return true;
            },
        },
        {
            id: 'actions',
            enableSorting: true,
            meta: {
                class: {
                    td: 'text-right'
                }
            },
            cell: ({ row }) => {
                return h(
                    UDropdownMenu,
                    {
                        content: {
                            align: 'end'
                        },
                        items: getRowItems(row),
                        'aria-label': 'Actions dropdown'
                    },
                    () =>
                        h(UButton, {
                            icon: 'i-lucide-ellipsis-vertical',
                            color: 'neutral',
                            variant: 'ghost',
                            'aria-label': 'Actions dropdown'
                        })
                )

            }
        }
    ]



}