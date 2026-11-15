
<template>
    <div class="transactions">















    <div class="transactions-wrapper">

        <div class="as-left">
          <span class="s-1">Recent transactions</span>
          <span class="s-2">A little activity from across your accounts</span>
        </div>

        <div class="latest-transactions">


          <div v-if="fetchedTemplateTest.latestFiveTransactions?.length > 0"
            v-for="item in fetchedTemplateTest.latestFiveTransactions.slice(0, 5)" class="transaction-list-member">

            <div class="transaction-main-info">

              <div
                :class="item.categoryKind === 'EXPENSE' || item.categoryKind === 'TRANSFER_TO' || item.categoryKind === 'ADJUSTMENT_MINUS' ? 'down-symbol' : 'up-symbol'">
                {{ item.categoryKind === 'EXPENSE' || item.categoryKind === 'TRANSFER_TO' || item.categoryKind ===
                  'ADJUSTMENT_MINUS' ? '▼' : '▲' }}
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

              <UPopover mode="hover" enable-touch :ui="{
                content: 'bg-transparent ring-0 border-transparent shadow-none backdrop-blur-md'
              }
                ">
                <span class="category" :style="{
                  '--category-light': stringToColors(item.categories[0] ?? 'unknown').light,
                  '--category-dark': stringToColors(item.categories[0] ?? 'unknown').dark,
                }">
                  {{ item.categories[0] ?? 'unknown' }}
                </span>

                <template v-if="item.categories.length > 1" #content>
                  <HoverCategories :categories="item.categories"></HoverCategories>
                </template>
              </UPopover>



              <div class="cat-date">
                {{ formatDateTime(utcToZoned(item.dateTimeZone, userStore.selectedTimeZone)) }}
              </div>
            </div>

            <div
              :class="item.categoryKind === 'EXPENSE' || item.categoryKind === 'TRANSFER_TO' || item.categoryKind === 'ADJUSTMENT_MINUS' ? 'transaction-amount-negative' : 'transaction-amount-positive'">
              {{ item.categoryKind === 'EXPENSE' || item.categoryKind === 'TRANSFER_TO' || item.categoryKind ===
                'ADJUSTMENT_MINUS' ? '-' + formatCurrency(item.amount, item.currency) : '+' + formatCurrency(item.amount,
                  item.currency) }}
            </div>

          </div>
          <div v-else class="no-data"> No transactions ... </div>
        </div>

        </div>








    </div>




</template>






<script setup lang="ts">
import { useUserStore } from '~/store/user';
import { formatCurrency } from '~/utilis/formaters/balanceFormater';
import { stringToColors } from '~/utilis/formaters/colorGetter';
import { formatDateTime } from '~/utilis/formaters/dateTimeFormater';
import { utcToZoned } from '~/utilis/timezoneUtilis/convertTimezone';

const userStore = useUserStore()

const fetchedTemplateTest ={
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
  }]
}

</script>


<style scoped>

.transactions-wrapper {
  display: flex;
  flex: 2;
  flex-direction: column;
  gap: 16px;
  min-width: 350px;


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

.transaction-amount-positive {
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

.as-left {
  display: flex;
  flex-direction: column;
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
</style>