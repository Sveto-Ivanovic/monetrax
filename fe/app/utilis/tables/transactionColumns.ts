import { h } from 'vue'
import { UButton, UDropdownMenu } from '#components'
import type { DropdownMenuItem, TableColumn, TableRow } from '@nuxt/ui'
import { formatCurrency } from '~/utilis/formaters/balanceFormater'
import { categoryTypeStyle, stringToColors } from '~/utilis/formaters/colorGetter'

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



export function getTransactionColumns(

    getRowItems: (row: TableRow<ColTypes>) => DropdownMenuItem[],
    accountCurrency: string,
): TableColumn<ColTypes>[] {



    return [
        {
            accessorKey: 'name',
            enableSorting: true,
            header: ({ column }) => {
                const isSorted = column.getIsSorted()

                return h(UButton, {
                    color: 'neutral',
                    variant: 'ghost',
                    label: 'Name',
                    icon: isSorted
                        ? isSorted === 'asc'
                            ? 'i-lucide-arrow-up-narrow-wide'
                            : 'i-lucide-arrow-down-wide-narrow'
                        : 'i-lucide-arrow-up-down',
                    class: '-mx-2.5',
                    onClick: () => column.toggleSorting(column.getIsSorted() === 'asc')
                })
            }
        },
        {
            accessorKey: 'description', enableSorting: true,
            header: 'Description',
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
            meta: {
                class: {
                    th: 'text-right',
                    td: 'text-right font-medium'
                }
            },
            footer: ({ table }) => {
                const total = table
                    .getFilteredRowModel()
                    .rows.reduce(
                        (acc: number, row: TableRow<ColTypes>) => acc + row.original.rawAmount,
                        0
                    )
                const formatted = formatCurrency(total, accountCurrency)
                return `Total: ${formatted}`
            },
            header: ({ column }) => {
                const isSorted = column.getIsSorted()

                return h(UButton, {
                    color: 'neutral',
                    variant: 'ghost',
                    label: 'Amount',
                    icon: isSorted
                        ? isSorted === 'asc'
                            ? 'i-lucide-arrow-up-narrow-wide'
                            : 'i-lucide-arrow-down-wide-narrow'
                        : 'i-lucide-arrow-up-down',
                    class: '-mx-2.5',
                    onClick: () => column.toggleSorting(column.getIsSorted() === 'asc')
                })
            },
        },

        {
            accessorKey: "categoryType",
            header: ({ column }) => {
                const isSorted = column.getIsSorted()

                return h(UButton, {
                    color: 'neutral',
                    variant: 'ghost',
                    label: 'Category Type',
                    icon: isSorted
                        ? isSorted === 'asc'
                            ? 'i-lucide-arrow-up-narrow-wide'
                            : 'i-lucide-arrow-down-wide-narrow'
                        : 'i-lucide-arrow-up-down',
                    class: '-mx-2.5',
                    onClick: () => column.toggleSorting(column.getIsSorted() === 'asc')
                })
            },
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
            accessorFn: (row) => row.categories.join('* ')+'*',

            header: 'Categories',
            enableSorting: true,
            filterFn: (row, _columnId, filterValue: string) =>
                row.original.categories.some(c => c.toLowerCase().includes(filterValue.toLowerCase())),

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
            header: ({ column }) => {
                const isSorted = column.getIsSorted()

                return h(UButton, {
                    color: 'neutral',
                    variant: 'ghost',
                    label: 'Date',
                    icon: isSorted
                        ? isSorted === 'asc'
                            ? 'i-lucide-arrow-up-narrow-wide'
                            : 'i-lucide-arrow-down-wide-narrow'
                        : 'i-lucide-arrow-up-down',
                    class: '-mx-2.5',
                    onClick: () => column.toggleSorting(column.getIsSorted() === 'asc')
                })
            },
            enableSorting: true,
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