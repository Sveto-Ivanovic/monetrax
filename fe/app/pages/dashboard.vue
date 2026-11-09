<template>
  <div class="dashboard">

    <div class="main-cards">

      <div class="main-card total-balance-card ">

        <div class="card-header1">
          <span> Total balance </span>
          <div>⌂</div>
        </div>

        <div class="card-ammount">
          <span class="raw-ammount1"> {{ getBalance.totalBalanceThisMonth }} </span>
          <div class="change-class" v-if="getBalance.sign">
            <span :class="getBalance.sign === 'positive' ? 'positive-change' : 'negative-change'"> {{
              getBalance.changeComparedToLastMonth }}</span>
            <span class="additiona-info-change"> vs. previous month</span>
          </div>
        </div>

        <div class="card-footer">
          <div class="graph-wrapper">
            <VisXYContainer :data="chartData" :height="25">
              <VisLine :x="(d: ChartPoint) => d.x" :y="(d: ChartPoint) => d.y" :lineWidth="3" />
            </VisXYContainer>
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
            <VisXYContainer :data="incomeChartData" :height="25">
              <VisLine :x="(d: ChartPoint) => d.x" :y="(d: ChartPoint) => d.y" :lineWidth="3" :color="'green'"/>
            </VisXYContainer>
          </div>
        </div>

      </div>

      <div class="main-card expense-card">

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
            <VisXYContainer :data="expenseChartData" :height="25">
              <VisLine :x="(d: ChartPoint) => d.x" :y="(d: ChartPoint) => d.y" :lineWidth="3" :color="'red'" />
            </VisXYContainer>
          </div>
        </div>

      </div>


    <div class="flow-and-account-state-wrapper">

        <div class="income-expense-flow">

            <div class="ief-header">

              <span class="s-1">Cash flow</span>
              <span class="s-2">Income and spending across all accounts</span>
            </div>
            
            <div class="ief-container">
              <BarChart :height="350" :y-axis="['expense', 'income']" v-bind="options">
                <template #tooltip="{ values }">
                  <div class="ief-hover">
                    <div class="ief-hover-title">{{ values?.date }}</div>

                    <div class="ief-hover-row">
                      <span  class="ief-hover-dot"  :style="{ '--c': RevenueCategoriesMultiple.income.color }"/>
                      <span class="ief-hover-label">Income</span>
                      <span class="ief-hover-value ief-hover-value-income">
                        {{ formatCurrency(values?.income ?? 0, fetchedTemplateTest.staticDataTotalBalance[0].currency ?? 'EUR') }}
                      </span>
                    </div>

                    <div class="ief-hover-row">
                      <span class="ief-hover-dot"  :style="{ '--c': RevenueCategoriesMultiple.expense.color }" />
                      <span class="ief-hover-label">Expense</span>
                      <span class="ief-hover-value ief-hover-value-expense">
                        {{ formatCurrency(values?.expense ?? 0,  fetchedTemplateTest.staticDataTotalBalance[0].currency ?? 'EUR') }}
                      </span>
                    </div>
                  </div>
                </template>
              </BarChart>
            </div>

        </div>

        <div class="account-state">

            <div class="as-header">
              
              <div class="as-left">
                  <span class="s-1">Your accounts</span>
                  <span class="s-2">Balances at a glance</span>
              </div>

               <NuxtLink to="/accounts" class="view-all-accs">View all →</NuxtLink>
            </div>

            <div class="as-content">

                <template v-if="displayTop3Accs.length">

                  <div
                    v-for="(item, i) in displayTop3Accs"
                    :key="item.bankNumLast4"
                    class="account-container"
                  >

                    <div class="acc-icon" :class="`acc-icon-${i % 3}`">
                      {{ item.accountName.charAt(0) }}
                    </div>

                    <div class="acc-info">
                      <div class="acc-name">{{ item.accountName }}</div>
                      <div class="acc-sub">{{ item.institution }} · {{ item.bankNumLast4 }}</div>
                    </div>

                    <div class="acc-amount">
                      <div class="acc-balance">
                        {{ formatMoney(item.totalBalance, item.currency) }}
                      </div>
                      <div class="acc-type">{{ item.accountType ?? item.currency }}</div>
                    </div>

                  </div>

                </template>

                <div v-else class="no-account-data">No accounts yet</div>
          </div>

              
            <div class="as-footer" style="width: 100%;">
              <NuxtLink to="/accounts/createAcc" class="view-all-accs">+ Add an account</NuxtLink>
            </div>

        </div>

    </div>


    </div>





  </div>
</template>

<script setup lang="ts">
import { isEmpty } from '@nuxt/ui/runtime/utils/index.js';
import type { FetchedTemplateTest } from '~/services/analytics/types';
import { useUserStore } from '~/store/user';
import { utcToZoned } from '~/utilis/timezoneUtilis/convertTimezone';
import { formatCurrency } from '~/utilis/formaters/balanceFormater'
import { VisXYContainer, VisLine } from '@unovis/vue'

const userStore = useUserStore()
console.log(userStore.userInfo)

const fetchedTemplateTest: FetchedTemplateTest = {
  staticDataTotalBalance: [{
    totalBalance: 10000.58,
    currency: 'EUR',
    month: '2026-11'
  }, {
    totalBalance: 8000,
    currency: 'EUR',
    month: '2026-10'
  }, {
    totalBalance: 9500,
    currency: 'EUR',
    month: '2026-09'
  }],

  staticDataTotalExpenses: [{
    totalExpenses: 2560,
    currency: 'EUR',
    month: '2026-11'
  }, {
    totalExpenses: 1900,
    currency: 'EUR',
    month: '2026-10'
  }, {
    totalExpenses: 1800,
    currency: 'EUR',
    month: '2026-09'
  }],
  staticDataTotalIncome: [{
    totalIncome: 3000,
    currency: 'EUR',
    month: '2026-11'
  }, {
    totalIncome: 2500,
    currency: 'EUR',
    month: '2026-10'
  }, {
    totalIncome: 2800,
    currency: 'EUR',
    month: '2026-09'
  }],
  totalBalanceByAccount: [{
    accountName: 'Checking Account',
    bankNumLast4: '1234',
    totalBalance: 5000,
    currency: 'EUR',
    institution: 'Bank A'
  }, {
    accountName: 'Savings Account',
    bankNumLast4: '5678',
    totalBalance: 3000,
    currency: 'EUR',
    institution: 'Bank B'
  }, {
    accountName: 'Investment Account',
    bankNumLast4: '9012',
    totalBalance: 2000,
    currency: 'EUR',
    institution: 'Bank C'
  }],
  latestFiveTransactions: [{
    transactionName: 'Grocery Store',
    dateTimeZone: '2026-11-15T10:30:00Z',
    amount: 35.75,
    currency: 'EUR',
    categories: ['Groceries', 'Food'],
    categoryKind: 'EXPENSE',
  },
  {
    transactionName: 'Salary Payment',
    dateTimeZone: '2026-11-01T09:00:00Z',
    amount: 2500.00,
    currency: 'EUR',
    categories: ['Income', 'Salary'],
    categoryKind: 'INCOME',
  },
  {
    transactionName: 'Electricity Bill',
    dateTimeZone: '2026-10-28T14:15:00Z',
    amount: 75.20,
    currency: 'EUR',
    categories: ['Utilities', 'Electricity'],
    categoryKind: 'EXPENSE',
  },
  {
    transactionName: 'Restaurant Dinner',
    dateTimeZone: '2026-10-20T19:45:00Z',
    amount: 60.50,
    currency: 'EUR',
    categories: ['Dining', 'Food'],
    categoryKind: 'EXPENSE',
  },
  {
    transactionName: 'Online Shopping',
    dateTimeZone: '2026-10-15T16:30:00Z',
    amount: 120.99,
    currency: 'EUR',
    categories: ['Shopping', 'E-commerce'],
    categoryKind: 'EXPENSE',
  }]
}

type ChartPoint = { x: number; y: number }

const chartData = computed<ChartPoint[]>(() =>
  getBalance.value.xData.map((date, i) => ({
    x: date.getTime(),
    y: getBalance.value.yData[i]!,
  }))
)

const incomeChartData = computed<ChartPoint[]>(() =>
  getIncome.value.xData.map((date, i) => ({
    x: date.getTime(),
    y: getIncome.value.yData[i]!,
  }))
)

const expenseChartData = computed<ChartPoint[]>(() =>
  getExpenses.value.xData.map((date, i) => ({
    x: date.getTime(),
    y: getExpenses.value.yData[i]!,
  }))
)


const getBalance = computed<{
  totalBalanceThisMonth: string;
  changeComparedToLastMonth: string;
  sign: string;
  yData: number[];
  xData: Date[];
}>(() => {
  if (!fetchedTemplateTest.staticDataTotalBalance?.length) {
    return {
      totalBalanceThisMonth: "No balance found.",
      changeComparedToLastMonth: "",
      sign: "",
      xData: [],
      yData: []
    }
  }

  let tempArray = fetchedTemplateTest.staticDataTotalBalance

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

    if (currentMonth && previousMonth && previousMonth.totalBalance != 0) {
      const change =
        ((currentMonth.totalBalance - previousMonth.totalBalance) /
          previousMonth.totalBalance) * 100;

      if (change > 0) {
        changeComparedToLastMonth = "+ " + changeString + "%"
        sign = "positive"
      }
      else if (change < 0) {
        changeComparedToLastMonth = "- " + changeString + "%"
        sign = "negative"
      }
    }

    const changeString = Math.abs(change).toFixed(2)
    const ascending = [...tempArray].reverse()

    yData = ascending.map(x => x.totalBalance)
    xData = ascending.map(x => new Date(`${x.month}-01T00:00:00`))
  }


  return {
    totalBalanceThisMonth: tempArray[0]?.currency && tempArray[0]?.totalBalance ? formatCurrency(tempArray[0]?.totalBalance, tempArray[0]?.currency).toString() : "No balance found.",
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
  if (!fetchedTemplateTest.staticDataTotalIncome?.length) {
    return {
      totalIncomeThisMonth: "No income found.",
      changeComparedToLastMonth: "",
      sign: "",
      xData: [],
      yData: []
    }
  }

  const tempArray = [...fetchedTemplateTest.staticDataTotalIncome]

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

    if (currentMonth && previousMonth && previousMonth.totalIncome != 0) {
      const change =
        ((currentMonth.totalIncome - previousMonth.totalIncome) /
          previousMonth.totalIncome) * 100;

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
    yData = ascending.map(x => x.totalIncome)
    xData = ascending.map(x => new Date(`${x.month}-01T00:00:00`))
  }

  return {
    totalIncomeThisMonth: tempArray[0]?.currency && tempArray[0]?.totalIncome ? formatCurrency(tempArray[0]?.totalIncome, tempArray[0]?.currency).toString() : "No income found.",
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
  if (!fetchedTemplateTest.staticDataTotalExpenses?.length) {
    return {
      totalExpensesThisMonth: "No expenses found.",
      changeComparedToLastMonth: "",
      sign: "",
      xData: [],
      yData: []
    }
  }

  const tempArray = [...fetchedTemplateTest.staticDataTotalExpenses]

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

    if (currentMonth && previousMonth && previousMonth.totalExpenses != 0) {
      const change =
        ((currentMonth.totalExpenses - previousMonth.totalExpenses) /
          previousMonth.totalExpenses) * 100;

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
    yData = ascending.map(x => x.totalExpenses)
    xData = ascending.map(x => new Date(`${x.month}-01T00:00:00`))
  }

  return {
    totalExpensesThisMonth: tempArray[0]?.currency && tempArray[0]?.totalExpenses ? formatCurrency(tempArray[0]?.totalExpenses, tempArray[0]?.currency).toString() : "No expenses found.",
    changeComparedToLastMonth: changeComparedToLastMonth,
    sign: sign,
    xData: xData,
    yData: yData
  }
})



type DataProps = {
  income?: number;
  expense?: number;
  date?: string;
};

const RevenueCategoriesMultiple = {
  income: { name: "Income", color: "#40f728" },
  expense: { name: "Expense", color: "#c52222" },
};

const RevenueData = computed<DataProps[]>(() => {
  return getIncome.value.xData
    .map((date, i) => ({
      date: date.toISOString().slice(0, 7),
      income: getIncome.value.yData[i]!,
      expense: getExpenses.value.yData[i]!,
    }))
    .slice(-7)
})

const options = computed(() => ({
  data: RevenueData.value,
  categories: RevenueCategoriesMultiple,
  xNumTicks:  Math.min(7, RevenueData.value.length),
  xAxis: "date" as keyof DataProps,
  groupPadding: 70,
  barPadding: 0.25,   
  radius: 5,
  xFormatter: (tick: number, i?: number) =>
    RevenueData.value[i ?? tick]?.date ?? "",
  yFormatter: (tick: number) =>
    formatCurrency(tick,  fetchedTemplateTest.staticDataTotalBalance[0].currency ?? 'EUR'),
  }))

type Account = {
  accountName: string
  bankNumLast4: string
  totalBalance: number
  currency: string
  institution: string
  accountType?: string   // optional: 'Checking' | 'Savings' | 'Cash'
}

const displayTop3Accs = computed<Account[]>(() =>
  [...staticDataTotalBalance.totalBalanceByAccount]
    .sort((a, b) => b.totalBalance - a.totalBalance)
    .slice(0, 3)
)

definePageMeta({
  middleware: ['require-user-info'],
  layout: 'dashboardlayout'
})

</script>

<style scoped>
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

.card-header1, .card-header2, .card-header3{
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

.income-expense-flow{
  flex: 2;
  display: flex;
  align-items: center;
  flex-direction: column;
  min-height:400px;
    min-width: 450px;

  padding: 24px;
  background-color: rgb(255, 255, 255);

  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 16px;

  box-shadow:
    0 2px 4px rgba(0, 0, 0, 0.02),
    0 8px 24px rgba(0, 0, 0, 0.06);
}



.ief-hover {
  padding: 10px 14px;
  min-width: 160px;
  font-family: inherit;
}

.ief-hover-title {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  opacity: 0.6;
  margin-bottom: 8px;
}

.ief-hover-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ief-hover-row + .ief-hover-row {
  margin-top: 4px;
}

.ief-hover-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--c);
}

.ief-hover-label {
  flex: 1;
  font-size: 13px;
  opacity: 0.7;
}

.ief-hover-value {
  font-size: 14px;
  font-weight: 600;
}

.ief-hover-value-income {
  color: #16a34a;
}

.ief-hover-value-expense {
  color: #c52222;
}

.ief-header{
  display: flex;
  flex-direction: column;
}

.s-1 {
    margin: 0 0 5px;
    color: #30473e;
    font-size: 13px;
    font-weight: 700;
    letter-spacing: -.2px;
}

.s-2 {
    margin: 0;
    color: #8c9992;
    font-size: 9px;
}

.account-state{
  flex: 1;
  display: flex;
  align-items: center;
  flex-direction: column;

  min-height: 400px;
  min-width: 450px;

  padding: 24px;
  background-color: rgb(255, 255, 255);

  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 16px;

  box-shadow:
    0 2px 4px rgba(0, 0, 0, 0.02),
    0 8px 24px rgba(0, 0, 0, 0.06);
}

.view-all-accs{
      color: #0c0075;
    font-size: 9px;
    font-weight: 700;
    white-space: nowrap;
}

.view-all-accs:hover {
  color: #1e0bca;
}

.as-left{
    display: flex;
  flex-direction: column;
}

.as-header{
  display: flex;
  flex-direction: row;
  align-items:center;
  justify-content: space-between;
}

.account-container {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 0;
}

.account-container + .account-container {
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.acc-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-size: 18px;
  font-weight: 600;
  flex-shrink: 0;
}

.acc-icon-0 { background: #e6f4ee; color: #2f8f6b; }
.acc-icon-1 { background: #eeeaf6; color: #7a6aa8; }
.acc-icon-2 { background: #fbefe0; color: #c9822b; }

.acc-info {
  flex: 1;
  min-width: 0;
}

.acc-name {
  font-size: 14px;
  font-weight: 600;
}

.acc-sub {
  font-size: 12px;
  opacity: 0.5;
  margin-top: 2px;
}

.acc-amount {
  text-align: right;
}

.acc-balance {
  font-size: 14px;
  font-weight: 600;
}

.acc-type {
  font-size: 12px;
  opacity: 0.5;
  margin-top: 2px;
}

.no-account-data {
  padding: 24px 0;
  text-align: center;
  opacity: 0.5;
}
</style>