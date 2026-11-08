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
  if (isEmpty(fetchedTemplateTest.staticDataTotalBalance)) {
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
    const a_month = new Date(`${a}-01T00:00:00`)
    const b_month = new Date(`${b}-01T00:00:00`)
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

      const changeString = change.toFixed(2).toString();

      if (change > 0) {
        changeComparedToLastMonth = "+ " + changeString + "%"
        sign = "positive"
      }
      else if (change < 0) {
        changeComparedToLastMonth = "- " + changeString + "%"
        sign = "negative"
      }
    }

    yData = tempArray.reverse().map(x => x.totalBalance)
    xData = tempArray.reverse().map(x => new Date(`${x.month}-01T00:00:00`))
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
  if (isEmpty(fetchedTemplateTest.staticDataTotalIncome)) {
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
  if (isEmpty(fetchedTemplateTest.staticDataTotalExpenses)) {
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
</style>