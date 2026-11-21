<script setup lang="ts">
import * as v from 'valibot';
import type { FormSubmitEvent } from '@nuxt/ui';
import { CalendarDate, Time, now } from '@internationalized/date';
import { itemsCurrency as itemsCurrency1 } from '~/components/data/currencies'
import { useUserStore } from '~/store/user';
import { apiRouter } from '~/services/serviceWrapper';
import type { TransactionCreate } from '~/services/transactions/types';
import { zonedToUtc } from '~/utilis/timezoneUtilis/convertTimezone';
import { getErrorMessage } from '~/utilis/getErrormessage';
import { useQueryClient } from '@tanstack/vue-query';
const inputDate = useTemplateRef('inputDate');
const uuid = () => crypto.randomUUID();


interface Category {
  categoryId: string;
  name: string;
}

const props = withDefaults(
  defineProps<{
    categories?: Category[];
    disabled: boolean;
    accountId: string;
  }>(),
  {
    disabled: false
  }
);

const selectedTimezone = useUserStore().selectedTimeZone;
const yesterday = now(selectedTimezone).subtract({ days: 1 });

const productLineSchema = v.object({
  Id: v.string(),
  productName: v.pipe(
    v.string(),
    v.minLength(4, 'Must be at least 4 characters'),
    v.maxLength(100, 'Must be at most 100 characters')
  ),
  amount: v.pipe(
    v.number('Amount is required'),
    v.minValue(0.01, 'Must be at least 0.01')
  ),
});

const additionalInfoSchema = v.object({
  Id: v.string(),
  kind: v.picklist(['DEDUCTION', 'ADDITION'], 'Select a valid kind of label'),
  label: v.pipe(
    v.string(),
    v.minLength(4, 'Must be at least 4 characters'),
    v.maxLength(100, 'Must be at most 100 characters')
  ),
  amount: v.pipe(
    v.number('Amount is required'),
    v.minValue(0.01, 'Must be at least 0.01')
  ),
});

const schema = v.object({
  name: v.pipe(v.string(), v.minLength(4, 'Must be at least 4 characters')),
  description: v.pipe(v.string()),
  currency: v.pipe(
    v.string(),
    v.minLength(3, 'Currency must be 3 letters long'),
    v.maxLength(3, 'Currency must be 3 letters long')
  ),
  amount: v.pipe(v.number(), v.minValue(0.01)),
  customCreationDate: v.pipe(
    v.instance(CalendarDate, 'Date of transaction is necessary')
  ),
  customCreationTime: v.pipe(
    v.instance(Time, 'time of transaction is necessary')
  ),
  categories: v.pipe(
    v.array(
      v.object({
        categoryId: v.string(),
        name: v.string(),
      })
    ),
    v.minLength(1, 'Select at least one category')
  ),

  // custom elements
  additionalInfo: v.pipe(v.array(additionalInfoSchema)),
  lineInformation: v.pipe(v.array(productLineSchema)),

  // recurence params
  recurrenceNum: v.pipe(v.number(), v.minValue(1, 'Min 1 recurrence')),
  maxNumOfOccurrencesAllowed: v.pipe(
    v.number(),
    v.minValue(1, 'Min 1 recurrence')
  ),
  ruleType: v.picklist(
    ['DAY', 'WEEK', 'MONTH', 'YEAR'],
    'Select a valid rule type'
  ),
});

// put label key in multi select to be name

type Schema = v.InferOutput<typeof schema>;
const state = reactive({
  name: '',
  description: '',
  currency: 'EUR',
  amount: 0.01,
  customCreationDate: markRaw(new CalendarDate(yesterday.year, yesterday.month, yesterday.day)),
  customCreationTime: markRaw(new Time(12, 0)),
  categories: [],
  additionalInfo: [],
  lineInformation: [],
  recurrenceNum: 1,
  maxNumOfOccurrencesAllowed: 1,
  ruleType: 'DAY',
}) as Schema

function addAdditionalInfo() {
  state.additionalInfo.push({
    Id: uuid(),
    kind: 'ADDITION',
    label: '',
    amount: 0.01,
  });
}

function addProduct() {
  state.lineInformation.push({
    Id: uuid(),
    productName: '',
    amount: 0.01,
  });
}

function removeAdditionalInfo(item: Schema['additionalInfo'][number]) {
  state.additionalInfo = state.additionalInfo.filter((i) => i.Id !== item.Id);
}
function duplicateAdditionalInfo(item: Schema['additionalInfo'][number]) {
  state.additionalInfo.push({ ...item, Id: uuid() });
}

function removeProduct(item: Schema['lineInformation'][number]) {
  state.lineInformation = state.lineInformation.filter((i) => i.Id !== item.Id);
}
function duplicateProduct(item: Schema['lineInformation'][number]) {
  state.lineInformation.push({ ...item, Id: uuid() });
}

const computeNetProduct = computed(() => {
  const val = state.lineInformation.reduce((x, item) => x + item.amount, 0);

  return val;
});

const computeAdditionalInfo = computed(() => {
  const val = state.additionalInfo.reduce((x, item) => x + item.amount, 0);

  return val;
});

const copiedInfo = ref(false);
let timerInfo: ReturnType<typeof setTimeout> | undefined;

const copyInfo = async () => {
  try {
    await navigator.clipboard.writeText(String(computeAdditionalInfo.value));
    copiedInfo.value = true;
    clearTimeout(timerInfo);
    timerInfo = setTimeout(() => (copiedInfo.value = false), 1200);
  } catch (e) {
    console.error('Copy failed', e);
  }
};

onBeforeUnmount(() => clearTimeout(timerInfo));

const copiedProduct = ref(false);
let timerProduct: ReturnType<typeof setTimeout> | undefined;

const copyProduct = async () => {
  try {
    await navigator.clipboard.writeText(String(computeNetProduct.value));
    copiedProduct.value = true;
    clearTimeout(timerProduct);
    timerProduct = setTimeout(() => (copiedProduct.value = false), 1200);
  } catch (e) {
    console.error('Copy failed', e);
  }
};

onBeforeUnmount(() => clearTimeout(timerProduct));

const checkboxValue = ref(false);
const checkboxValueRecurring = ref(false);

const transactionApi = apiRouter.transactionApi()
const createTransactionMutation = transactionApi.createTransaction()
const loadingBut = ref(false)
const createTransactionErr = ref('')
const open = ref(false)
const queryCLient = useQueryClient()

async function onSubmit(event: FormSubmitEvent<Schema>) {
  createTransactionErr.value = ''
  loadingBut.value = true
  let transactionCreate: TransactionCreate = {
    name: event.data.name,
    amount: event.data.amount,
    currency: event.data.currency,
    categories: event.data.categories,
    additionalInfo: event.data.additionalInfo,
    lineInformation: event.data.lineInformation,
  }

  if (checkboxValue.value) {
    const local = `${event.data.customCreationDate.toString()}T${event.data.customCreationTime.toString()}`
    transactionCreate['customCreationDate'] = zonedToUtc(local, selectedTimezone).toISOString()
  }

  if (checkboxValueRecurring.value) {
    transactionCreate['transactionRecurrenceRule'] = {
      ruleType: event.data.ruleType,
      recurrenceNum: event.data.recurrenceNum,
      maxNumOfOccurrencesAllowed: event.data.maxNumOfOccurrencesAllowed
    }
  }

  try {
    const res = await createTransactionMutation.mutateAsync({ req: transactionCreate, account_id: props.accountId })

    open.value = false
    const yesterday = now(selectedTimezone).subtract({ days: 1 })
  await queryCLient.invalidateQueries({
    queryKey: ['account', 'get', props.accountId, 'transactions'],
  })

    Object.assign(state, {
      name: '',
      description: '',
      currency: 'EUR',
      amount: 0.01,
      customCreationDate: markRaw(
        new CalendarDate(yesterday.year, yesterday.month, yesterday.day)
      ),
      customCreationTime: markRaw(new Time(12, 0)),
      categories: [],
      additionalInfo: [],
      lineInformation: [],
      recurrenceNum: 1,
      maxNumOfOccurrencesAllowed: 1,
      ruleType: 'DAY',
    })

    checkboxValue.value = false
    checkboxValueRecurring.value = false
    createTransactionErr.value = ''

  } catch (e) {
    createTransactionErr.value = getErrorMessage(e)
  }
  finally {
    loadingBut.value = false
  }

}
</script>

<template>
  <UModal v-model:open="open" :ui="{ content: 'sm:max-w-3xl' }">
    <UButton label="Open" :class="!props.disabled ? 'acc-transaction-active' : 'acc-transaction-disabled'">+ Create
      Transaction</UButton>

    <template #content="{ close }">
      <div class="form-wrapper">
          <div v-if="createTransactionErr" class="form-error" role="alert">
    <UIcon name="i-lucide-circle-alert" class="form-error-icon" />
    <span class="form-error-text">{{ createTransactionErr }}</span>
  </div>


        <div class="form-creation-header">
          <h2 class="form-title">Create Transaction</h2>

          <UButton icon="i-lucide-x" color="neutral" variant="ghost" size="sm" aria-label="Close" @click="close" />
        </div>

        <UForm :schema="schema" :state="state" class="space-y-4" @submit="onSubmit">
          <UFormField label="Transaction Name" name="name">
            <UInput v-model="state.name" class="w-full" placeholder="Weekly Groceries" />
          </UFormField>

          <UFormField label="Description" name="description">
            <UTextarea v-model="state.description" class="w-full" :rows="3"
              placeholder="What is this transaction used for?" />
          </UFormField>

          <div class="modal-row">
            <UFormField label="Amount" name="amount">
              <UInput v-model="state.amount" type="number" class="w-full" />
            </UFormField>
            <UFormField label="Currency" name="currency">
              <USelectMenu v-model="state.currency" :items="itemsCurrency1" class="w-full" />
            </UFormField>
          </div>

          <UFormField label="Categories" name="categories">
            <USelectMenu v-model="state.categories" :items="props.categories" label-key="name" multiple class="w-full"
              placeholder="Select categories" />
          </UFormField>

          <div class="check-box-custom-date">
            <span> Set transaction date </span>
            <UCheckbox v-model="checkboxValue" />
          </div>

          <div class="modal-row-h" :class="checkboxValue ? 'mshow' : ''">
            <UFormField label="Date of Transaction" name="customCreationDate">
              <UInputDate ref="inputDate" v-model="state.customCreationDate">
                <template #trailing>
                  <UPopover :reference="inputDate?.inputsRef[3]?.$el">
                    <UButton color="neutral" variant="link" size="sm" icon="i-lucide-calendar"
                      aria-label="Select a date" class="px-0" />

                    <template #content>
                      <UCalendar v-model="state.customCreationDate" class="p-2" />
                    </template>
                  </UPopover>
                </template>
              </UInputDate>
            </UFormField>

            <UFormField label="Time of Transaction" name="customCreationTime">
              <UInputTime v-model="state.customCreationTime" />
            </UFormField>
          </div>

          <div class="additional-info-wrapper">
            <div class="add-inf-header">
              <span> Bought Products </span>

              <UButton color="neutral" @click="addProduct()" variant="outline">
                + Add row</UButton>
            </div>

            <div class="add-inf-content">
              <div v-if="state.lineInformation.length > 0" class="modal-row-2"
                v-for="(item, index) in state.lineInformation" :key="item.Id">
                <UFormField label="Label" :name="`lineInformation.${index}.productName`">
                  <UInput v-model="item.productName" class="w-full" placeholder="Insert Product Name" />
                </UFormField>

                <UFormField label="Amount" :name="`lineInformation.${index}.amount`">
                  <UInput v-model="item.amount" type="number" class="w-full" />
                </UFormField>

                <div class="row-actions">
                  <UButton icon="i-lucide-copy" color="neutral" variant="outline" size="sm" aria-label="Duplicate row"
                    @click="duplicateProduct(item)" />
                  <UButton icon="i-lucide-x" color="error" variant="outline" size="sm" aria-label="Remove row"
                    @click="removeProduct(item)" />
                </div>
              </div>

              <div v-else class="no-data">
                No items yet. Add what you bought, one row per product.
              </div>
            </div>

            <div class="add-foot">
              <span class="net-h">Net adjustment</span>

              <UTooltip :open="copiedProduct" text="Copied!">
                <div class="net-adj-copy" @click="copyProduct()">
                  {{ computeNetProduct + ' ' + state.currency }}
                </div>
              </UTooltip>
            </div>
          </div>

          <div class="additional-info-wrapper">
            <div class="add-inf-header">
              <span> Additions and deductions </span>

              <UButton color="neutral" @click="addAdditionalInfo()" variant="outline">
                + Add row</UButton>
            </div>

            <div class="add-inf-content">
              <div v-if="state.additionalInfo.length > 0" class="modal-row-3"
                v-for="(item, index) in state.additionalInfo" :key="item.Id">
                <UFormField label="Label" :name="`additionalInfo.${index}.label`">
                  <UInput v-model="item.label" class="w-full" placeholder="Insert Name" />
                </UFormField>

                <UFormField label="Amount" :name="`additionalInfo.${index}.amount`">
                  <UInput v-model="item.amount" type="number" class="w-full" />
                </UFormField>

                <UFormField label="Type" :name="`additionalInfo.${index}.kind`">
                  <USelect v-model="item.kind" :items="['DEDUCTION', 'ADDITION']" class="w-full" />
                </UFormField>

                <div class="row-actions">
                  <UButton icon="i-lucide-copy" color="neutral" variant="outline" size="sm" aria-label="Duplicate row"
                    @click="duplicateAdditionalInfo(item)" />
                  <UButton icon="i-lucide-x" color="error" variant="outline" size="sm" aria-label="Remove row"
                    @click="removeAdditionalInfo(item)" />
                </div>
              </div>

              <div v-else class="no-data">
                No adjustments. Add a tip, discount, fee or tax if the total
                changes.
              </div>
            </div>

            <div class="add-foot">
              <span class="net-h">Net adjustment</span>
              <UTooltip :open="copiedInfo" text="Copied!">
                <div class="net-adj-copy" @click="copyInfo()">
                  {{ computeAdditionalInfo + ' ' + state.currency }}
                </div>
              </UTooltip>
            </div>
          </div>

          <div class="check-box-custom-date">
            <span> Set transaction as recurring </span>
            <UCheckbox v-model="checkboxValueRecurring" />
          </div>

          <div class="modal-row-h-3" :class="checkboxValueRecurring ? 'mshow' : ''">
            <UFormField label="Repeat every" name="recurrenceNum">
              <UInput v-model="state.recurrenceNum" class="w-full" type="number" />
            </UFormField>

            <UFormField label="Recurrence Type" name="ruleType">
              <USelect v-model="state.ruleType" :items="['DAY', 'WEEK', 'MONTH', 'YEAR']" class="w-full" />
            </UFormField>

            <UFormField label="Max Recurrence Num." name="maxNumOfOccurrencesAllowed">
              <UInput v-model="state.maxNumOfOccurrencesAllowed" class="w-full" type="number" />
            </UFormField>
          </div>

          <UButton type="submit" :loading="loadingBut" :disabled="loadingBut"> Submit </UButton>
        </UForm>
      </div>
    </template>
  </UModal>
</template>

<style scoped>
.form-wrapper {
  padding: 24px;
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
}

.modal-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.modal-row-3 {
  display: grid;
  grid-template-columns: 2fr 2fr 2fr 1fr;
  align-items: center;
  gap: 8px;
  padding: 8px;
}

.modal-row-3:not(:last-child) {
  border-bottom: 1px solid lightgray;
}

.modal-row-2 {
  display: grid;
  grid-template-columns: 3fr 3fr 1fr;
  align-items: center;
  gap: 8px;
  padding: 8px;
}

.modal-row-2:not(:last-child) {
  border-bottom: 1px solid lightgray;
}

.check-box-custom-date {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-top: 16px;
  gap: 8px;
}

.check-box-custom-date span {
  margin-top: -2px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.additional-info-wrapper {
  border: 1px solid lightgray;
  border-radius: 12px;
}

.add-inf-header {
  margin-top: -2px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  width: 100%;
  align-items: center;
  padding: 6px;
  border-bottom: 1px solid lightgray;
  background-color: #efeff5;
  border-radius: 12px 12px 0 0;
}

.row-actions {
  display: flex;
  flex-direction: row;
  align-items: end;
  gap: 3px;
  height: 100%;
  flex-shrink: 0;
  margin-bottom: 3px;
}

.no-data {
  padding: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  text-align: center;
  color: gray;
}

.add-foot {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  width: 100%;
  align-items: center;
  padding: 6px;
  border-top: 1px solid lightgray;
  background-color: #efeff5;
  border-radius: 0px 0px 12px 12px;
}

.net-h {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.net-adj-copy {
  font-size: 12px;
  font-weight: 500;
  color: gray;
  cursor: pointer;
  user-select: none;
  display: inline-block;
  transition: color 0.15s ease, transform 0.1s ease, opacity 0.15s ease;
}

.net-adj-copy:hover {
  color: #333;
  text-decoration: underline dotted;
  text-underline-offset: 3px;
}

.net-adj-copy:active {
  color: #000;
  transform: scale(0.96);
  opacity: 0.8;
}

.modal-row-h {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-height: 0px;
  overflow: hidden;
  gap: 16px;
  transition: max-height 0.5s ease;
}

.modal-row-h.mshow {
  max-height: 300px;
}

.modal-row-h-3 {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  max-height: 0px;
  overflow: hidden;
  gap: 16px;
  transition: max-height 0.5s ease;
}

.form-creation-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 12px;
  margin-bottom: 16px;
  border-bottom: 1px solid lightgray;
}

.form-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.modal-row-h-3.mshow {
  max-height: 300px;
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
.form-error {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  gap: 8px;
  padding: 10px 12px;
  margin-bottom: 16px;
  background-color: #fef2f2;
  border: 1px solid #fecaca;
  border-left: 4px solid #dc2626;
  border-radius: 8px;
}

.form-error-icon {
  flex-shrink: 0;
  margin-top: 2px;
  font-size: 16px;
  color: #dc2626;
}

.form-error-text {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.4;
  color: #991b1b;
  word-break: break-word;
}

@media (max-width: 720px) {
  .modal-row-3 {
    grid-template-columns: 1fr 1fr;
  }

  .modal-row-h-3 {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 520px) {
  .modal-row {
    grid-template-columns: 1fr;
  }

  .modal-row-3 {
    grid-template-columns: 1fr;
  }

  .modal-row-2 {
    grid-template-columns: 1fr;
  }

  .modal-row-h {
    grid-template-columns: 1fr;
  }

  .modal-row-h-3 {
    grid-template-columns: 1fr;
  }
}
</style>
