<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '~/store/user'
import { accountTransactionsExamples } from '~/testData/accountTransactions'
import { formatCurrency } from '~/utilis/formaters/balanceFormater'
import { utcToZoned } from '~/utilis/timezoneUtilis/convertTimezone'
import { VisXYContainer, VisLine } from '@unovis/vue'
import { formatDateTime } from '~/utilis/formaters/dateTimeFormater'
import type { DropdownMenuItem, TableColumn, ContextMenuItem, } from '@nuxt/ui'
import type { TableRow } from '@nuxt/ui'
import { getTransactionColumns, type ColTypes } from '~/utilis/tables/transactionColumns'
import { getPaginationRowModel } from '@tanstack/vue-table'

const route = useRoute()


// switch with the endpoint
const accountTransactions = accountTransactionsExamples



function openTransactionModal() {

}


function openAlertModal() {

}


function handleArchive(accountId: string, active: boolean) {

}


const userStore = useUserStore()


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  functions related to the three cards START
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
interface ValState {
    value: number;
    currency: string;
    month: string;
}

type ChartPoint = { x: number; y: number }




const chartData = computed<ChartPoint[]>(() => {
    const x = getNet.value.xData.map((date, i) => ({
        x: date.getTime(),
        y: getNet.value.yData[i]!,
    })).slice(-5)

    return x

}

)

const incomeChartData = computed<ChartPoint[]>(() =>
    getIncome.value.xData.map((date, i) => ({
        x: date.getTime(),
        y: getIncome.value.yData[i]!,
    })).slice(-5)
)

const expenseChartData = computed<ChartPoint[]>(() =>
    getExpenses.value.xData.map((date, i) => ({
        x: date.getTime(),
        y: getExpenses.value.yData[i]!,
    })).slice(-5)
)



const getCashFlowMonthlyData = computed<{
    net: ValState[],
    income: ValState[],
    expenses: ValState[]
}>(() => {


    if (!accountTransactionsExamples.transactions?.length) {
        return {
            net: [],
            income: [],
            expenses: []
        }
    }

    let tempArray = accountTransactionsExamples.transactions

    // here im getting array of strings representing the months i need
    const currentDate = new Date();

    const date = new Intl.DateTimeFormat("en-CA", {
        timeZone: userStore.getSelectedTimeZone(),
        year: "numeric",
        month: "2-digit",
        day: "2-digit"
    }).format(currentDate);

    const [year, month] = date.split("-").map(Number);
    const months = [];
    let begingReduceState: {
        net: Record<string, ValState>,
        income: Record<string, ValState>,
        expenses: Record<string, ValState>
    } = {
        income: {},
        expenses: {},
        net: {}
    }

    for (let i = 0; i < 5; i++) {
        const d = new Date(year!, month! - 1 - i, 1);
        const monthString = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}`
        months.push(monthString);

        begingReduceState['income'][monthString] = {
            currency: accountTransactions.account.currency,
            value: 0.01,
            month: monthString
        } as ValState;

        begingReduceState['expenses'][monthString] = {
            currency: accountTransactions.account.currency,
            value: 0.01,
            month: monthString
        } as ValState;


        begingReduceState['net'][monthString] = {
            currency: accountTransactions.account.currency,
            value: 0.01,
            month: monthString
        } as ValState;
    }




    const yearlyData = tempArray.reduce((acc, item, i) => {
        const yearMonth = utcToZoned(item.createdAt, userStore.getSelectedTimeZone())
            .slice(0, 7)

        const CatTypeIsExpense = ['EXPENSE', 'TRANSFER_TO', 'ADJUSTMENT_MINUS'].includes(item.categoryType)

        if (acc['income'][yearMonth] && acc['expenses'][yearMonth] && acc['net'][yearMonth]) {

            acc['income'][yearMonth]['value'] = !CatTypeIsExpense ? acc['income'][yearMonth]['value'] + item.amount : acc['income'][yearMonth]['value']
            acc['expenses'][yearMonth]['value'] = CatTypeIsExpense ? acc['expenses'][yearMonth]['value'] + item.amount : acc['expenses'][yearMonth]['value']
            acc['net'][yearMonth]['value'] = CatTypeIsExpense ? acc['net'][yearMonth]['value'] - item.amount : acc['net'][yearMonth]['value'] + item.amount
        }

        return acc;
    }, begingReduceState);




    return {
        net: Object.values(yearlyData.net),
        income: Object.values(yearlyData.income),
        expenses: Object.values(yearlyData.expenses)
    }

})



const getNet = computed<{
    totalNetThisMonth: string;
    changeComparedToLastMonth: string;
    sign: string;
    yData: number[];
    xData: Date[];
}>(() => {
    if (!getCashFlowMonthlyData.value.net?.length) {
        return {
            totalNetThisMonth: "No net data found.",
            changeComparedToLastMonth: "",
            sign: "",
            xData: [],
            yData: []
        }
    }

    let tempArray = getCashFlowMonthlyData.value.net

    tempArray.sort((a, b) => {
        const a_month = new Date(`${a.month}-01T00:00:00`)
        const b_month = new Date(`${b.month}-01T00:00:00`)
        return b_month.getTime() - a_month.getTime();
    })


    let changeComparedToLastMonth = ""
    let sign = ""
    let yData: number[] = []
    let xData: Date[] = []
    if (tempArray.length >= 2) {

        const currentMonth = tempArray[0];
        const previousMonth = tempArray[1];

        if (currentMonth && previousMonth && previousMonth.value != 0) {
            const change = ((currentMonth.value - previousMonth.value) / Math.abs(previousMonth.value)) * 100

            const changeString = Math.abs(change).toFixed(2)

            if (change > 0) {
                changeComparedToLastMonth = "+ " + changeString + "%"
                sign = "positive"
            }
            else if (change < 0) {
                changeComparedToLastMonth = "- " + changeString + "%"
                sign = "negative"
            }
        }

        const ascending = [...tempArray].reverse()

        yData = ascending.map(x => x.value)
        xData = ascending.map(x => new Date(`${x.month}-01T00:00:00`))
    }


    return {
        totalNetThisMonth: tempArray[0]?.currency && tempArray[0]?.value ? formatCurrency(tempArray[0]?.value, tempArray[0]?.currency).toString() : "No net data found.",
        changeComparedToLastMonth: changeComparedToLastMonth,
        sign: sign,
        xData: xData,
        yData: yData
    }
})



const getIncome = computed<{
    totalIncomeThisMonth: string;
    changeComparedToLastMonth: string;
    sign: string;
    yData: number[];
    xData: Date[];
}>(() => {
    if (!getCashFlowMonthlyData.value.income?.length) {
        return {
            totalIncomeThisMonth: "No income data found.",
            changeComparedToLastMonth: "",
            sign: "",
            xData: [],
            yData: []
        }
    }

    let tempArray = getCashFlowMonthlyData.value.income

    tempArray.sort((a, b) => {
        const a_month = new Date(`${a.month}-01T00:00:00`)
        const b_month = new Date(`${b.month}-01T00:00:00`)
        return b_month.getTime() - a_month.getTime();
    })


    let changeComparedToLastMonth = ""
    let sign = ""
    let yData: number[] = []
    let xData: Date[] = []
    if (tempArray.length >= 2) {

        const currentMonth = tempArray[0];
        const previousMonth = tempArray[1];

        if (currentMonth && previousMonth && previousMonth.value != 0) {
            const change =
                ((currentMonth.value - previousMonth.value) /
                    previousMonth.value) * 100;

            const changeString = Math.abs(change).toFixed(2)

            if (change > 0) {
                changeComparedToLastMonth = "+ " + changeString + "%"
                sign = "positive"
            }
            else if (change < 0) {
                changeComparedToLastMonth = "- " + changeString + "%"
                sign = "negative"
            }
        }

        const ascending = [...tempArray].reverse()

        yData = ascending.map(x => x.value)
        xData = ascending.map(x => new Date(`${x.month}-01T00:00:00`))
    }


    return {
        totalIncomeThisMonth: tempArray[0]?.currency && tempArray[0]?.value ? formatCurrency(tempArray[0]?.value, tempArray[0]?.currency).toString() : "No income data found.",
        changeComparedToLastMonth: changeComparedToLastMonth,
        sign: sign,
        xData: xData,
        yData: yData
    }
})


const getExpenses = computed<{
    totalExpensesThisMonth: string;
    changeComparedToLastMonth: string;
    sign: string;
    yData: number[];
    xData: Date[];
}>(() => {
    if (!getCashFlowMonthlyData.value.expenses?.length) {
        return {
            totalExpensesThisMonth: "No expense data found.",
            changeComparedToLastMonth: "",
            sign: "",
            xData: [],
            yData: []
        }
    }

    let tempArray = getCashFlowMonthlyData.value.expenses

    tempArray.sort((a, b) => {
        const a_month = new Date(`${a.month}-01T00:00:00`)
        const b_month = new Date(`${b.month}-01T00:00:00`)
        return b_month.getTime() - a_month.getTime();
    })


    let changeComparedToLastMonth = ""
    let sign = ""
    let yData: number[] = []
    let xData: Date[] = []
    if (tempArray.length >= 2) {

        const currentMonth = tempArray[0];
        const previousMonth = tempArray[1];

        if (currentMonth && previousMonth && previousMonth.value != 0) {
            const change =
                ((currentMonth.value - previousMonth.value) /
                    previousMonth.value) * 100;

            const changeString = Math.abs(change).toFixed(2)

            if (change > 0) {
                changeComparedToLastMonth = "+ " + changeString + "%"
                sign = "positive"
            }
            else if (change < 0) {
                changeComparedToLastMonth = "- " + changeString + "%"
                sign = "negative"
            }
        }

        const ascending = [...tempArray].reverse()

        yData = ascending.map(x => x.value)
        xData = ascending.map(x => new Date(`${x.month}-01T00:00:00`))
    }


    return {
        totalExpensesThisMonth: tempArray[0]?.currency && tempArray[0]?.value ? formatCurrency(tempArray[0]?.value, tempArray[0]?.currency).toString() : "No expense data found.",
        changeComparedToLastMonth: changeComparedToLastMonth,
        sign: sign,
        xData: xData,
        yData: yData
    }
})

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  functions related to the three cards END
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  Props for transaction Table START
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

const globalFilter = ref('')


let tableTransactions = computed<ColTypes[]>(() => {
    return accountTransactions.transactions.map((x) => {
        return {
            name: x.name,
            description: x.description,
            amount: formatCurrency(x.amount, x.currency),
            categories: x.categories,
            date: formatDateTime(utcToZoned(x.createdAt, userStore.getSelectedTimeZone())),
            categoryType: x.categoryType,
            rawAmount: x.amount,
            currency: x.currency,
            transactionId: x.transactionId,
            rawDate: utcToZoned(x.createdAt, userStore.getSelectedTimeZone())
        }
    })
})

// here we get tranasction options
const columns: TableColumn<ColTypes>[] = getTransactionColumns(getRowItems, accountTransactions.account.currency)

function getRowItems(row: TableRow<ColTypes>): DropdownMenuItem[] {
    return [
        { type: 'label', label: 'Actions' },
        {
            label: 'Delete Transaction',
            onSelect() {
                console.log('Deleted transaction', row.original.name)
            },
        },
        {
            label: 'More Details',
            onSelect() {
                console.log('More Details', row.original.name)
            },
        },
    ]
}
const items = ref<ContextMenuItem[]>([])
function onContextmenu(_e: Event, row: TableRow<ColTypes>) {
    items.value = getRowItems(row)
}
const table = useTemplateRef('table')

const pagination = ref({
    pageIndex: 0,
    pageSize: 10
})




definePageMeta({
    middleware: ['require-user-info', 'account-breadcrumbs'],
    layout: 'dashboardlayout'
})

</script>

<template>
    <div class="account">


        <!-- Header -->
        <div class="account-info">
            <div class="acc-inf-header">
                <div class="acc-inf-header-left">
                    <div class="name-and-active">
                        <span class="acc-name"> {{ accountTransactions.account.name }} </span>
                        <span :class="accountTransactions.account.active ? 'acc-active' : 'acc-archived'"> {{
                            accountTransactions.account.active ? 'active' : 'archived' }} </span>
                    </div>

                    <div class="acc-sub-info">
                        <span>{{ accountTransactions.account.institutionName }}</span>
                        <span>{{ '**** **** **** ' + accountTransactions.account.accountNumberMasked }}</span>
                        <span>{{ accountTransactions.account.currency }}</span>
                    </div>
                </div>

                <div class="acc-inf-header-right">
                    <span class="balance-desc"> Current Balance </span>
                    <span class="balance-format"> {{ formatCurrency(accountTransactions.account.currentBalance,
                        accountTransactions.account.currency) }} </span>
                </div>
            </div>

            <div class="dividing-line"></div>

            <div class="account-info-buttons">
                <div class="acc-inf-creation-buttons">
                    <UButton
                        :class="accountTransactions.account.active ? 'acc-transaction-active' : 'acc-transaction-disabled'"
                        @click="openTransactionModal()">+ Create Transaction</UButton>
                    <UButton :class="accountTransactions.account.active ? 'acc-alert-active' : 'acc-alert-disabled'"
                        @click="openAlertModal()">+ Add Alert</UButton>
                </div>
                <div class="archive-analytics">
                    <UButton
                        @click="handleArchive(accountTransactions.account.accountId, accountTransactions.account.active)"
                        :class="accountTransactions.account.active ? 'acc-inf-arch' : 'acc-inf-unarch'">{{
                            accountTransactions.account.active ? 'Archive' : 'Unarchive' }}</UButton>

                    <ULink class="acc-analytics" :to="'/accounts/analytics/' + accountTransactions.account.accountId">
                        Display Account Analytics
                    </ULink>
                </div>
            </div>
        </div>



        <!-- Three state divs -->
        <div class="main-cards">

            <div class="main-card">
                <div class="card-header1">
                    <span> Net </span>
                    <div>⌂</div>
                </div>

                <div class="card-ammount">
                    <span class="raw-ammount1"> {{ getNet.totalNetThisMonth }} </span>
                    <div class="change-class" v-if="getNet.sign">
                        <span :class="getNet.sign === 'positive' ? 'positive-change' : 'negative-change'"> {{
                            getNet.changeComparedToLastMonth }}</span>
                        <span class="additiona-info-change"> vs. previous month</span>
                    </div>
                </div>

                <div class="card-footer">
                    <div class="graph-wrapper">
                        <ClientOnly>
                            <VisXYContainer :data="chartData" :height="25">
                                <VisLine :x="(d: ChartPoint) => d.x" :y="(d: ChartPoint) => d.y" :lineWidth="3" />
                            </VisXYContainer>
                        </ClientOnly>
                    </div>
                </div>


            </div>

            <div class="main-card">
                <div class="card-header2">
                    <span> Income </span>
                    <div>↑</div>
                </div>

                <div class="card-ammount">
                    <span class="raw-ammount2"> {{ getIncome.totalIncomeThisMonth }} </span>
                    <div class="change-class" v-if="getIncome.sign">
                        <span :class="getIncome.sign === 'positive' ? 'positive-change' : 'negative-change'">
                            {{ getIncome.changeComparedToLastMonth }}</span>
                        <span class="additional-info-change"> vs. previous month</span>
                    </div>
                </div>

                <div class="card-footer">
                    <div class="graph-wrapper">
                        <ClientOnly>
                            <VisXYContainer :data="incomeChartData" :height="25">
                                <VisLine :x="(d: ChartPoint) => d.x" :y="(d: ChartPoint) => d.y" :lineWidth="3"
                                    :color="'green'" />
                            </VisXYContainer>
                        </ClientOnly>
                    </div>
                </div>

            </div>

            <div class="main-card">

                <div class="card-header3">
                    <span> Expenses </span>
                    <div>↓</div>
                </div>

                <div class="card-ammount">
                    <span class="raw-ammount3"> {{ getExpenses.totalExpensesThisMonth }} </span>
                    <div class="change-class" v-if="getExpenses.sign">
                        <span :class="getExpenses.sign === 'negative' ? 'positive-change' : 'negative-change'">
                            {{ getExpenses.changeComparedToLastMonth }}</span>
                        <span class="additional-info-change"> vs. previous month</span>
                    </div>
                </div>

                <div class="card-footer">
                    <div class="graph-wrapper">
                        <ClientOnly>
                            <VisXYContainer :data="expenseChartData" :height="25">
                                <VisLine :x="(d: ChartPoint) => d.x" :y="(d: ChartPoint) => d.y" :lineWidth="3"
                                    :color="'red'" />
                            </VisXYContainer>
                        </ClientOnly>
                    </div>
                </div>
            </div>

        </div>



        <!-- Table component with alert on the side -->
         <div class="trans-alert-comp">
        <div class="trans-table-alerts">

            <div>
                <div class="flex px-4 py-3.5 border-b border-accented gap-1">
                    <UInput v-model="globalFilter" class="max-w-sm" placeholder="Filter..." />

                    <UIcon name="i-lucide-info" class="size-5 text-muted cursor-help"
                        title="If you want to target just specific category enter its name and add * at the end of it" />
                </div>

                <UContextMenu :items="items">
                    <UTable sticky :data="tableTransactions" :columns="columns" class="flex-1" ref="table"
                        :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }"
                        v-model:pagination="pagination" :ui="{ root: 'min-h-[770px]' }"
                        v-model:global-filter="globalFilter" @contextmenu="onContextmenu">
                        <template #expanded="{ row }">
                            <pre>{{ row.original }}</pre>
                        </template>
                    </UTable>
                </UContextMenu>

                <div class="flex justify-end border-t border-default pt-4 px-4">
                    <UPagination :page="(table?.tableApi?.getState().pagination.pageIndex || 0) + 1"
                        :items-per-page="table?.tableApi?.getState().pagination.pageSize"
                        :total="table?.tableApi?.getFilteredRowModel().rows.length"
                        @update:page="(p) => table?.tableApi?.setPageIndex(p - 1)" />
                </div>
            </div>


        </div>

               <div class="alert-wrapper">


                    hey

            </div>

</div>




    </div>
</template>

<style scoped>
.account {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 24px;
    padding-bottom: 24px;
}


.account-info {
    width: 100%;
    padding: 24px;
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.acc-inf-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 24px;
    flex-wrap: wrap;
}

.acc-inf-header-left {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.name-and-active {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
}

.acc-name {
    font-size: 20px;
    font-weight: 700;
    color: #1e293b;
    letter-spacing: -0.4px;
}

.acc-active,
.acc-archived {
    padding: 5px 11px;
    border-radius: 20px;
    font-size: 11px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.acc-active {
    color: #15803d;
    background: #dcfce7;
}

.acc-archived {
    color: #64748b;
    background: #f1f5f9;
}

.acc-sub-info {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
    color: #64748b;
    font-size: 13px;
}

.acc-sub-info span:not(:last-child)::after {
    content: "•";
    margin-left: 10px;
    color: #cbd5e1;
}

.acc-inf-header-right {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 6px;
}

.balance-desc {
    font-size: 12px;
    font-weight: 500;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: 0.7px;
}

.balance-format {
    font-size: 26px;
    font-weight: 750;
    color: #0f172a;
    letter-spacing: -0.7px;
}

.dividing-line {
    width: 100%;
    height: 1px;
    background: #e2e8f0;
    margin: 24px 0 18px;
}

.account-info-buttons {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
}

.acc-inf-creation-buttons {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
}

.acc-transaction-active {
    background: #2563eb;
    color: #ffffff;
    border: 1px solid #2563eb;
    border-radius: 7px;
    font-weight: 600;
    transition: all 0.2s ease;
    cursor: pointer;
}

.acc-transaction-active:hover {
    background: #1d4ed8;
    border-color: #1d4ed8;
}

.acc-transaction-disabled {
    background: #cbd5e1;
    color: #64748b;
    border: 1px solid #cbd5e1;
    border-radius: 7px;
    cursor: not-allowed;
    opacity: 0.7;
}

.acc-alert-active {
    background: #ffffff;
    color: #475569;
    border: 1px solid #cbd5e1;
    border-radius: 7px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
}

.acc-alert-active:hover {
    background: #f1f5f9;
    border-color: #94a3b8;
}

.acc-alert-disabled {
    background: #f1f5f9;
    color: #94a3b8;
    border: 1px solid #e2e8f0;
    border-radius: 7px;
    cursor: not-allowed;
    opacity: 0.7;
}

/* Archive / Unarchive */
.acc-inf-arch,
.acc-inf-unarch {
    border-radius: 7px;
    padding: 8px 16px;
    font-weight: 600;
    transition: all 0.2s ease;
}

.acc-inf-arch {
    background: #fff1f2;
    color: #e11d48;
    border: 1px solid #fecdd3;
    cursor: pointer;
}

.acc-inf-arch:hover {
    background: #ffe4e6;
}

.acc-inf-unarch {
    background: #ecfdf5;
    color: #059669;
    border: 1px solid #a7f3d0;
}

.acc-inf-unarch:hover {
    background: #d1fae5;
}

.archive-analytics {
    display: flex;
    align-items: center;
    gap: 12px;
}


.acc-analytics {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 8px 14px;
    border: 1px solid #bfdbfe;
    border-radius: 7px;
    background: #eff6ff;
    color: #2563eb;
    font-size: 13px;
    font-weight: 600;
    text-decoration: none;
    transition: all 0.2s ease;
}

.acc-analytics:hover {
    background: #dbeafe;
    border-color: #93c5fd;
    color: #1d4ed8;
    box-shadow: 0 2px 6px rgba(37, 99, 235, 0.12);
}

.acc-analytics:active {
    background: #bfdbfe;
    transform: translateY(1px);
}

.acc-inf-arch {
    transition: all 0.2s ease;
}

.acc-inf-arch:hover {
    background: #ffe4e6;
    border-color: #fda4af;
    color: #be123c;
    box-shadow: 0 2px 6px rgba(225, 29, 72, 0.12);
    transform: translateY(-1px);
}

.acc-inf-unarch {
    transition: all 0.2s ease;
}

.acc-inf-unarch:hover {
    background: #d1fae5;
    border-color: #6ee7b7;
    color: #047857;
    box-shadow: 0 2px 6px rgba(5, 150, 105, 0.12);
    transform: translateY(-1px);
}

.acc-analytics:focus-visible,
.acc-inf-arch:focus-visible,
.acc-inf-unarch:focus-visible {
    outline: 3px solid rgba(59, 130, 246, 0.3);
    outline-offset: 2px;
}


.main-cards {
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    gap: 34px;
    flex-wrap: wrap;
}

.main-card {
    flex: 1;
    padding: 24px;
    background-color: rgb(255, 255, 255);
    border-radius: 16px;
    min-width: 350px;
    min-height: 200px;

    border: 1px solid rgba(0, 0, 0, 0.06);
    border-radius: 16px;

    box-shadow:
        0 2px 4px rgba(0, 0, 0, 0.02),
        0 8px 24px rgba(0, 0, 0, 0.06);
}




.card-header1,
.card-header2,
.card-header3 {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-direction: row;
}

.card-header1 span {
    color: #9ca3af;
    font-size: 14px;
    font-weight: 500;
}

.card-header1 div {
    color: #3b82f6;
    background: #eff6ff;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    font-size: 20px;
}

.card-header2 span {
    color: #9ca3af;
    font-size: 14px;
    font-weight: 500;
}

.card-header2 div {
    color: #86f63b;
    background: #f4ffef;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    font-size: 20px;
}


.card-header3 span {
    color: #9ca3af;
    font-size: 14px;
    font-weight: 500;
}

.card-header3 div {
    color: #f6513b;
    background: #fff1ef;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    font-size: 20px;
}

.card-ammount {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-top: 20px;
}

.raw-ammount1 {
    font-size: 30px;
    font-weight: 700;
    color: #00347e;
    letter-spacing: -1px;
}

.raw-ammount2 {
    font-size: 30px;
    font-weight: 700;
    color: #227003;
    letter-spacing: -1px;
}


.raw-ammount3 {
    font-size: 30px;
    font-weight: 700;
    color: #5f0701;
    letter-spacing: -1px;
}

.change-class {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
}

.positive-change {
    color: #16a34a;
    font-weight: 600;
    background: #dcfce7;
    padding: 4px 8px;
    border-radius: 6px;
}

.negative-change {
    color: #dc2626;
    font-weight: 600;
    background: #fee2e2;
    padding: 4px 8px;
    border-radius: 6px;
}

.additional-info-change {
    color: #9ca3af;
    font-size: 12px;
}

.card-footer {
    width: 100%;
    display: flex;
    justify-content: end;
}

.graph-wrapper {
    width: 150px;
    padding-top: 24px;
}

.trans-alert-comp{
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 16px;

}

.trans-table-alerts {
    flex: 3;
    padding: 24px;
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    min-width: 500px;

}

.alert-wrapper{
       flex: 1;
    padding: 24px;
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04); 
    min-width: 350px;
}

@media (max-width: 640px) {
    .account-info {
        padding: 18px;
    }

    .acc-inf-header {
        align-items: flex-start;
        flex-direction: column;
        gap: 20px;
    }

    .acc-inf-header-right {
        align-items: flex-start;
        width: 100%;
    }

    .balance-format {
        font-size: 23px;
    }

    .account-info-buttons {
        align-items: stretch;
        flex-direction: column;
    }

    .acc-inf-creation-buttons {
        width: 100%;
        flex-direction: column;
        align-items: stretch;
    }

    .acc-inf-creation-buttons :deep(button) {
        width: 100%;
    }

    .account-info-buttons> :deep(button) {
        width: 100%;
    }

    .archive-analytics {
        width: 100%;
        flex-direction: column;
        align-items: stretch;
    }

    .archive-analytics>* {
        width: 100%;
        text-align: center;
        justify-content: center;
    }
}
</style>
