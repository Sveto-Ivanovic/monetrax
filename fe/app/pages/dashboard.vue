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
              <VisLine :x="(d: ChartPoint) => d.x" :y="(d: ChartPoint) => d.y" :lineWidth="3" :color="'green'" />
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



    </div>


    <div class="flow-and-account-state-wrapper">

      <div class="income-expense-flow">

        <div class="ief-header">

          <span class="s-1">Cash flow</span>
          <span class="s-2">Income and spending across all accounts</span>
        </div>

        <div class="ief-container" ref="chartEl">
          <ClientOnly>
            <VChart :option="chartOption" autoresize style="height: 350px; width: 100%" />
          </ClientOnly>
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

            <NuxtLink v-for="(item, i) in displayTop3Accs" :key="item.bankNumLast4" class="account-container"
              :to="`/accounts/${item.id}`">

              <div class="acc-icon" :class="`acc-icon-${i % 3}`">
                {{ item.accountName.charAt(0) }}
              </div>

              <div class="acc-info">
                <div class="acc-name">{{ item.accountName }}</div>
                <div class="acc-sub">{{ item.institution }} · {{ item.bankNumLast4 }}</div>
              </div>

              <div class="acc-amount">
                <div class="acc-balance">
                  {{ formatCurrency(item.totalBalance, item.currency) }}
                </div>
                <div class="acc-type">{{ item.currency }}</div>
              </div>

            </NuxtLink>

          </template>

          <div v-else class="no-account-data">No accounts yet</div>
        </div>


        <div class="as-footer" style="width: 100%; padding-top: 32px;">
          <NuxtLink to="/accounts/createAcc" class="view-all-accs">+ Add an account</NuxtLink>
        </div>

      </div>

    </div>

    <div class="transactions-piechart-category-wrapper">


      <div class="transactions-wrapper">

        <div class="as-left">
                <span class="s-1">Recent transactions</span>
                <span class="s-2">A little activity from across your accounts</span>
        </div>

        <div class="latest-transactions">


          <div v-if="fetchedTemplateTest.latestFiveTransactions?.length > 0"
            v-for="item in fetchedTemplateTest.latestFiveTransactions" class="transaction-list-member">

            <div class="transaction-main-info">

              <div
                :class="item.categoryKind === 'EXPENSE' || item.categoryKind === 'TRANSFER_TO' || item.categoryKind === 'ADJUSTMENT_MINUS' ? 'down-symbol' : 'up-symbol'">
                {{ item.categoryKind === 'EXPENSE' || item.categoryKind ===  'TRANSFER_TO' || item.categoryKind ===  'ADJUSTMENT_MINUS' ? '▼' : '▲' }}
              </div>

              <div class="ta-names">
                <span class="t1">
                  {{ item.transactionName }}
                </span>
                <span class="t2">
                  {{ 'Account: ' + item.accountName }}
                </span>
              </div>

            </div>

            <div class="categories-date">

              <UPopover mode="hover" enable-touch :ui="
              {
                content: 'bg-transparent ring-0 border-transparent shadow-none backdrop-blur-md'
              }
              ">
                <span  class="category" :style="
                {
                  '--category-light': stringToColors(item.categories[0] ?? 'unknown').light,
                  '--category-dark': stringToColors(item.categories[0] ?? 'unknown').dark,
                }">
                  {{ item.categories[0] ?? 'unknown' }}
                </span>

                <template v-if="item.categories.length>1" #content>
                  <HoverCategories :categories="item.categories"></HoverCategories>
                </template>
              </UPopover>
              


              <div class="cat-date">
                {{ formatDateTime(item.dateTimeZone) }}
              </div>
            </div>

            <div :class=" item.categoryKind === 'EXPENSE' || item.categoryKind === 'TRANSFER_TO' || item.categoryKind === 'ADJUSTMENT_MINUS' ? 'transaction-amount-negative' : 'transaction-amount-positive'">
                {{ item.categoryKind === 'EXPENSE' || item.categoryKind === 'TRANSFER_TO' || item.categoryKind === 'ADJUSTMENT_MINUS' ? '-' + formatCurrency(item.amount, item.currency) : '+' + formatCurrency(item.amount, item.currency) }}
            </div>

          </div>
          <div v-else class="no-data"> No transactions ... </div>
      </div>

      <div class="expense-chart"> 
        <ClientOnly>
            <VChart :option="optionPieChart1" autoresize style="height: 350px; width: 100%" />
        </ClientOnly>
      
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
import { useElementSize } from '@vueuse/core'
import { stringToColors } from '~/utilis/formaters/colorGetter';
import { formatDateTime } from '~/utilis/formaters/dateTimeFormater';

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
  }
    , {
    totalExpenses: 1800,
    currency: 'EUR',
    month: '2026-08'
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
  }, {
    totalIncome: 2800,
    currency: 'EUR',
    month: '2026-08'
  }],
  totalBalanceByAccount: [{
    accountName: 'Checking Account',
    bankNumLast4: '1234',
    totalBalance: 5000,
    currency: 'EUR',
    institution: 'Bank A',
    id: "550e8400-e29b-41d4-a716-446655440000"
  }, {
    accountName: 'Savings Account',
    bankNumLast4: '5678',
    totalBalance: 3000,
    currency: 'EUR',
    institution: 'Bank B',
    id: "7f3a9c21-8b64-4d02-ae51-93c7f0b82614"
  }, {
    accountName: 'Investment Account',
    bankNumLast4: '9012',
    totalBalance: 2000,
    currency: 'EUR',
    institution: 'Bank C',
    id: "c2d8e5a7-1f43-49b6-8c90-5a2e71d4f603"
  }],
  latestFiveTransactions: [{
    transactionName: 'Grocery Store',
    dateTimeZone: '2026-11-15T10:30:00Z',
    amount: 35.75,
    currency: 'EUR',
    categories: ['Groceries', 'Food'],
    categoryKind: 'EXPENSE',
    accountName: 'Checking Account'
  },
  {
    transactionName: 'Salary Payment',
    dateTimeZone: '2026-11-01T09:00:00Z',
    amount: 2500.00,
    currency: 'EUR',
    categories: ['Income', 'Salary'],
    categoryKind: 'INCOME',
    accountName: 'Savings Account',
  },
  {
    transactionName: 'Electricity Bill',
    dateTimeZone: '2026-10-28T14:15:00Z',
    amount: 75.20,
    currency: 'EUR',
    categories: ['Utilities', 'Electricity'],
    categoryKind: 'EXPENSE',
    accountName: 'Savings Account',
  },
  {
    transactionName: 'Restaurant Dinner',
    dateTimeZone: '2026-10-20T19:45:00Z',
    amount: 60.50,
    currency: 'EUR',
    categories: ['Dining', 'Food'],
    categoryKind: 'EXPENSE',
    accountName: 'Savings Account',
  },
  {
    transactionName: 'Online Shopping',
    dateTimeZone: '2026-10-15T16:30:00Z',
    amount: 120.99,
    currency: 'EUR',
    categories: ['Shopping', 'E-commerce'],
    categoryKind: 'EXPENSE',
    accountName: 'Savings Account',
  }],
  categoryAmountsIncome: [
    {
      categoryName: 'Salary',
      categoryKind: 'INCOME',
      amount: 2500
    },
    {
      categoryName: 'Freelancing',
      categoryKind: 'INCOME',
      amount: 750
    },
    {
      categoryName: 'Investments',
      categoryKind: 'INCOME',
      amount: 320
    },
    {
      categoryName: 'Gifts',
      categoryKind: 'INCOME',
      amount: 150
    }
  ],

  categoryAmountsExpense: [
    {
      categoryName: 'Food',
      categoryKind: 'EXPENSE',
      amount: 450
    },
    {
      categoryName: 'Transportation',
      categoryKind: 'EXPENSE',
      amount: 120
    },
    {
      categoryName: 'Rent',
      categoryKind: 'EXPENSE',
      amount: 800
    },
    {
      categoryName: 'Entertainment',
      categoryKind: 'EXPENSE',
      amount: 180
    }
  ]
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

/* the multi barchart part */


type DataProps = {
  income?: number;
  expense?: number;
  date?: string;
};

const revenueCategoriesMultiple = {
  income: { name: "Income", color: "#40f728" },
  expense: { name: "Expense", color: "#c52222" },
};

const revenueData = computed<DataProps[]>(() => {

  return getIncome.value.xData
    .map((date, i) => ({
      date: date.toISOString().slice(0, 7),
      income: getIncome.value.yData[i]!,
      expense: getExpenses.value.yData[i]!,
    }))
    .slice(-7)
})

const currency = computed(() => fetchedTemplateTest.staticDataTotalBalance[0]?.currency ?? 'EUR')

const chartOption = computed(() => ({
  grid: { left: 10, right: 10, top: 20, bottom: 40, containLabel: true },
  legend: { bottom: 0, icon: 'roundRect', itemWidth: 10, itemHeight: 10 },
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' },
    backgroundColor: 'rgb(222, 240, 245)',
    borderWidth: 0,

    formatter: (params: any[]) => `
      <div style="min-width:160px">
        <div style="font-size:12px; font-weight:600; opacity:.6; margin-bottom:8px">${params[0].axisValue}</div>
        ${params.map(p => `
          <div style="display:flex; align-items:center; gap:8px; margin-top:4px">
            ${p.marker}
            <span style="flex:1; font-size:13px; opacity:.7 ">${p.seriesName}</span>
            <span style="font-size:14px; font-weight:600; color:${p.color}">${formatCurrency(p.value, currency.value)}</span>
          </div>`).join('')}
      </div>`,

  },
  xAxis: {
    type: 'category',
    data: revenueData.value.map(d => d.date),
    axisTick: { show: false },
  },
  yAxis: {
    type: 'value',
    axisLabel: { formatter: (v: number) => formatCurrency(v, currency.value) },
  },
  series: [
    {
      name: 'Income', type: 'bar', barWidth: 20, barGap: '0%',
      data: revenueData.value.map(d => d.income),
      itemStyle: { color: revenueCategoriesMultiple.income.color, borderRadius: [5, 5, 0, 0] },
    },
    {
      name: 'Expense', type: 'bar', barWidth: 20,
      data: revenueData.value.map(d => d.expense),
      itemStyle: { color: revenueCategoriesMultiple.expense.color, borderRadius: [5, 5, 0, 0] },
    },
  ],
}))

/* accounts */

type Account = {
  accountName: string
  bankNumLast4: string
  totalBalance: number
  currency: string
  institution: string
  id: string
}

const displayTop3Accs = computed<Account[]>(() => {
  if (!fetchedTemplateTest.totalBalanceByAccount.length) {
    return []

  }

  const x = [...fetchedTemplateTest.totalBalanceByAccount]
    .sort((a, b) => b.totalBalance - a.totalBalance)
    .slice(0, 3)

  return x
})

# the pie charts
let optionPieChart1 =  computed(() => (
  
let sum = 0;
const expenseData = fetchedTemplateTest.categoryAmountsExpense

expenseData.forEach((x)=> {
  sum = sum + x.amount
})

const subtext = 'How your ' + formatCurrency(sum, expenseData[0].currency) + ' was spent this month this month'
const data = expenseData.map((x)=> {
  value: x.amount, name: x.categoryName
})
  
return {
title: {
    text: 'Spending by category',
    subtext: subtext,
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: 'Access From',
      type: 'pie',
      radius: '50%',
      data: data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ],
   tooltip: {
    trigger: 'item',
    backgroundColor: 'rgb(222, 240, 245)',
    borderWidth: 0,

      valueFormatter: (value) => formatCurrency(value, 'EUR')

  },
}))

let optionPieChart2 =  computed(() => (
let sum = 0;
const expenseData = fetchedTemplateTest.categoryAmountsIncome

expenseData.forEach((x)=> {
  sum = sum + x.amount
})

const subtext = 'How your ' + formatCurrency(sum, expenseData[0].currency) + ' was spent this month this month'
const data = expenseData.map((x)=> {
  value: x.amount, name: x.categoryName
})
  
return {
title: {
    text: 'Spending by category',
    subtext: subtext,
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: 'Access From',
      type: 'pie',
      radius: '50%',
      data: data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ],
     tooltip: {
    trigger: 'item',
    backgroundColor: 'rgb(222, 240, 245)',
    borderWidth: 0,

      valueFormatter: (value) => formatCurrency(value, 'EUR')

  },
}))



definePageMeta({
  middleware: ['require-user-info'],
  layout: 'dashboardlayout'
})

</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 48px;
  padding-bottom: 24px;
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

.flow-and-account-state-wrapper {
  display: flex;
  flex-direction: row;
  gap: 32px;
  flex-wrap: wrap;
  justify-content: center;

}

.income-expense-flow {
  flex: 2;
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



.ief-hover {
  padding: 10px 14px;
  min-width: 160px;
  font-family: inherit;
  background-color: rgb(222, 240, 245);
  border-radius: 16px;
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

.ief-hover-row+.ief-hover-row {
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

.ief-header {
  display: flex;
  flex-direction: column;
  width: 100%;
  padding-left: 12px;
}

.s-1 {
  margin: 0 0 5px;
  color: #30473e;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: -.2px;
}

.s-2 {
  margin: 0;
  color: #8c9992;
  font-size: 13px;
}

.account-state {
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

.view-all-accs {
  color: #0c0075;
  font-size: 16px;
  font-weight: 700;
  white-space: nowrap;
}

.view-all-accs:hover {
  color: #1e0bca;
}

.as-left {
  display: flex;
  flex-direction: column;
}

.as-header {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.account-container {
  display: flex;
  align-items: center;
  gap: 14px;
  border-radius: 8px;
  padding: 16px 8px;
}

.account-container+.account-container {
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.account-container:hover {
  box-shadow: 0 0 0 1px blue;
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

.acc-icon-0 {
  background: #e6f4ee;
  color: #2f8f6b;
}

.acc-icon-1 {
  background: #eeeaf6;
  color: #7a6aa8;
}

.acc-icon-2 {
  background: #fbefe0;
  color: #c9822b;
}

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

.as-content {
  width: 100%;
  padding-top: 32px;
}

.ief-container {
  width: 100%;
}


.category {
  padding: 0.123rem 0.63rem;
  border: 1px solid var(--category-dark);
  border-radius: 9999px;
  background-color: var(--category-light);
  color: var(--category-dark);
  font-size: 0.75rem;
  font-weight: 500;
  line-height: 1.25rem;
}

.transactions-piechart-category-wrapper{
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}


.transactions-wrapper {
  display: flex;
  flex:2;
  flex-direction: column;
  gap: 16px;
  width: 100%;


  padding: 24px;
  background-color: rgb(255, 255, 255);

  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 16px;

  box-shadow:
    0 2px 4px rgba(0, 0, 0, 0.02),
    0 8px 24px rgba(0, 0, 0, 0.06);
}

.latest-transactions {
  display: flex;
  flex-direction: column;
  width: 100%;
  min-width: 0;
}

.transaction-list-member {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem 0.5rem;
  border-bottom: 1px solid var(--ui-border);
}

.transaction-list-member:last-child {
  border-bottom: none;
}

.transaction-main-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  min-width: 0;
}

.down-symbol,
.up-symbol {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  font-size: 0.75rem;
  line-height: 1;
}

.down-symbol {
  background-color: color-mix(in srgb, #ef4444 15%, transparent);
  color: #ef4444;
}

.up-symbol {
  background-color: color-mix(in srgb, #22c55e 15%, transparent);
  color: #22c55e;
}

.ta-names {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.t1 {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--ui-text-highlighted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.t2 {
  font-size: 0.75rem;
  color: var(--ui-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.categories-date {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.25rem;
}

.cat-date {
  font-size: 0.7rem;
  color: var(--ui-text-muted);
  white-space: nowrap;
}

.transaction-amount-negative {
  min-width: 5rem;
  text-align: right;
  font-size: 0.95rem;
  font-weight: 600;
  color: red;
  white-space: nowrap;
}

.transaction-amount-positive{
    min-width: 5rem;
  text-align: right;
  font-size: 0.95rem;
  font-weight: 600;
  color: rgb(34, 150, 5);
  white-space: nowrap;
}

.no-data {
  padding: 1.5rem;
  text-align: center;
  color: var(--ui-text-muted);
  font-size: 0.875rem;
}

@media (max-width: 640px) {
  .transaction-list-member {
    grid-template-columns: minmax(0, 1fr) auto;
    grid-template-areas:
      "main amount"
      "meta meta";
    gap: 0.5rem 0.75rem;
    padding: 0.75rem 0.25rem;
  }

  .transaction-main-info {
    grid-area: main;
  }

  .transaction-amount {
    grid-area: amount;
    min-width: 0;
    font-size: 0.9rem;
  }

  .categories-date {
    grid-area: meta;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding-left: 2.75rem;
  }

  .down-symbol,
  .up-symbol {
    width: 1.75rem;
    height: 1.75rem;
  }
}

</style>