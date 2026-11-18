<script setup lang="ts">
import * as v from 'valibot';
import type { FormSubmitEvent } from '@nuxt/ui';
import { CalendarDate, Time, now } from '@internationalized/date';

const inputDate = useTemplateRef('inputDate');

const itemsCurrency = [
  // Europe
  'EUR',
  'GBP',
  'CHF',
  'ALL',
  'BAM',
  'RSD',
  'MKD',
  'NOK',
  'SEK',
  'DKK',
  'ISK',
  'PLN',
  'CZK',
  'HUF',
  'RON',
  'MDL',
  'UAH',
  'BYN',
  'RUB',
  'TRY',
  'GEL',
  'AMD',
  'AZN',
  'GIP',
  'XPF',

  // Americas
  'USD',
  'CAD',
  'MXN',
  'BRL',
  'ARS',
  'CLP',
  'COP',
  'PEN',
  'UYU',
  'PYG',
  'BOB',
  'VES',
  'GYD',
  'SRD',
  'JMD',
  'TTD',
  'BBD',
  'BSD',
  'BZD',
  'BMD',
  'AWG',
  'ANG',
  'XCD',
  'DOP',
  'CRC',
  'GTQ',
  'HNL',
  'NIO',
  'PAB',
  'SVC',
  'HTG',
  'CUP',
  'KYD',
  'FKP',

  // Asia
  'CNY',
  'JPY',
  'KRW',
  'KPW',
  'INR',
  'PKR',
  'BDT',
  'LKR',
  'NPR',
  'BTN',
  'MVR',
  'AFN',
  'KZT',
  'UZS',
  'KGS',
  'TJS',
  'TMT',
  'MNT',
  'THB',
  'VND',
  'IDR',
  'MYR',
  'SGD',
  'PHP',
  'BND',
  'KHR',
  'LAK',
  'MMK',
  'TWD',
  'HKD',
  'MOP',
  'AED',
  'SAR',
  'QAR',
  'KWD',
  'BHD',
  'OMR',
  'YER',
  'ILS',
  'JOD',
  'IQD',
  'IRR',
  'LBP',
  'SYP',

  // Africa and Oceania
  'DZD',
  'MAD',
  'TND',
  'EGP',
  'LYD',
  'SDG',
  'ETB',
  'ERN',
  'KES',
  'UGX',
  'TZS',
  'RWF',
  'BIF',
  'SOS',
  'DJF',
  'NGN',
  'GHS',
  'GMD',
  'GNF',
  'SLE',
  'LRD',
  'XOF',
  'XAF',
  'CDF',
  'AOA',
  'ZMW',
  'ZWG',
  'BWP',
  'NAD',
  'LSL',
  'SZL',
  'ZAR',
  'MUR',
  'SCR',
  'MGA',
  'MWK',
  'MZN',
  'PGK',
  'FJD',
  'WST',
];

const selectedTimezone = 'UTC';
const yesterday = now(selectedTimezone).subtract({ days: 1 });
const props = {
  categories: [
    {
      categoryId: '1',
      name: 'Category 1',
    },
    {
      categoryId: '2',
      name: 'Category 2',
    },
    {
      categoryId: '3',
      name: 'Category 3',
    },
  ],
};

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

const state = reactive<Schema>({
  name: '',
  description: '',
  currency: 'EUR',
  amount: 0.01,
  customCreationDate: new CalendarDate(
    yesterday.year,
    yesterday.month,
    yesterday.day
  ),
  customCreationTime: new Time(12, 0),
  categories: [],

  additionalInfo: [],
  lineInformation: [],

  recurrenceNum: 1,
  maxNumOfOccurrencesAllowed: 1,
  ruleType: 'DAY',
});

function addAdditionalInfo() {
  const uuid = () => crypto.randomUUID();
  state.additionalInfo.push({
    Id: uuid,
    kind: 'ADDITION',
    label: '',
    amount: 0.01,
  });
}

function addProduct() {
  const uuid = () => crypto.randomUUID();
  state.lineInformation.push({
    Id: uuid,
    productName: '',
    amount: 0.01,
  });
}

function removeAdditionalInfo(item: Schema['additionalInfo'][number]) {
  state.additionalInfo = state.additionalInfo.filter((i) => i.Id !== item.Id);
}
function duplicateAdditionalInfo(item: Schema['additionalInfo'][number]) {
  state.additionalInfo.push({ ...item, Id: crypto.randomUUID() });
}

function removeProduct(item: Schema['lineInformation'][number]) {
  state.lineInformation = state.lineInformation.filter((i) => i.Id !== item.Id);
}
function duplicateProduct(item: Schema['lineInformation'][number]) {
  state.lineInformation.push({ ...item, Id: crypto.randomUUID() });
}

const checkboxValue = ref(false);

async function onSubmit(event: FormSubmitEvent<Schema>) {
  console.log(event.data);
}
</script>

<template>
  <UModal>
    <UButton label="Open" color="neutral" variant="subtle" />

    <template #content>
      <div class="form-wrapper">
        <UForm
          :schema="schema"
          :state="state"
          class="space-y-4"
          @submit="onSubmit"
        >
          <UFormField label="Transaction Name" name="name">
            <UInput
              v-model="state.name"
              class="w-full"
              placeholder="Weekly Groceries"
            />
          </UFormField>

          <UFormField label="Description" name="description">
            <UTextarea
              v-model="state.description"
              class="w-full"
              :rows="3"
              placeholder="What is this transaction used for?"
            />
          </UFormField>

          <div class="modal-row">
            <UFormField label="Amount" name="amount">
              <UInput v-model="state.amount" type="number" class="w-full" />
            </UFormField>
            <UFormField label="Currency" name="currency">
              <USelectMenu
                v-model="state.currency"
                :items="itemsCurrency"
                class="w-full"
              />
            </UFormField>
          </div>

          <div class="modal-row" v-if="checkboxValue">
            <UFormField label="Date of Transaction" name="customCreationDate">
              <UInputDate ref="inputDate" v-model="state.customCreationDate">
                <template #trailing>
                  <UPopover :reference="inputDate?.inputsRef[3]?.$el">
                    <UButton
                      color="neutral"
                      variant="link"
                      size="sm"
                      icon="i-lucide-calendar"
                      aria-label="Select a date"
                      class="px-0"
                    />

                    <template #content>
                      <UCalendar
                        v-model="state.customCreationDate"
                        class="p-2"
                      />
                    </template>
                  </UPopover>
                </template>
              </UInputDate>
            </UFormField>

            <UFormField label="Time of Transaction" name="customCreationTime">
              <UInputTime v-model="state.customCreationTime" />
            </UFormField>
          </div>

          <div class="check-box-custom-date">
            <span> Set transaction date </span>
            <UCheckbox v-model="checkboxValue" />
          </div>

          <div class="additional-info-wrapper">
            <div class="add-inf-header">
              <span> Bought Products </span>

              <UButton color="neutral" @click="addProduct()" variant="outline">
                + Add row</UButton
              >
            </div>

            <div class="add-inf-content">
              <div
                v-if="state.lineInformation.length > 0"
                class="modal-row-2"
                v-for="(item, index) in state.lineInformation"
                :key="item.Id"
              >
                <UFormField
                  label="Label"
                  :name="`lineInformation.${index}.productName`"
                >
                  <UInput
                    v-model="item.productName"
                    class="w-full"
                    placeholder="Insert Product Name"
                  />
                </UFormField>

                <UFormField
                  label="Amount"
                  :name="`lineInformation.${index}.amount`"
                >
                  <UInput v-model="item.amount" type="number" class="w-full" />
                </UFormField>

                <div class="row-actions">
                  <UButton
                    icon="i-lucide-copy"
                    color="neutral"
                    variant="outline"
                    size="sm"
                    aria-label="Duplicate row"
                    @click="duplicateProduct(item)"
                  />
                  <UButton
                    icon="i-lucide-x"
                    color="error"
                    variant="outline"
                    size="sm"
                    aria-label="Remove row"
                    @click="removeProduct(item)"
                  />
                </div>
              </div>

              <div v-else class="no-data">
                No items yet. Add what you bought, one row per product.
              </div>
            </div>

            <div class="add-foot">
              <span>Net adjustment</span>

              <div class="net-adj-copy"></div>
            </div>
          </div>

          <div class="additional-info-wrapper">
            <div class="add-inf-header">
              <span> Additions and deductions </span>

              <UButton
                color="neutral"
                @click="addAdditionalInfo()"
                variant="outline"
              >
                + Add row</UButton
              >
            </div>

            <div class="add-inf-content">
              <div
                v-if="state.additionalInfo.length > 0"
                class="modal-row-3"
                v-for="(item, index) in state.additionalInfo"
                :key="item.Id"
              >
                <UFormField
                  label="Label"
                  :name="`additionalInfo.${index}.label`"
                >
                  <UInput
                    v-model="item.label"
                    class="w-full"
                    placeholder="Insert Name"
                  />
                </UFormField>

                <UFormField
                  label="Amount"
                  :name="`additionalInfo.${index}.amount`"
                >
                  <UInput v-model="item.amount" type="number" class="w-full" />
                </UFormField>

                <UFormField label="Type" :name="`additionalInfo.${index}.kind`">
                  <USelect
                    v-model="item.kind"
                    :items="['DEDUCTION', 'ADDITION']"
                    class="w-full"
                  />
                </UFormField>

                <div class="row-actions">
                  <UButton
                    icon="i-lucide-copy"
                    color="neutral"
                    variant="outline"
                    size="sm"
                    aria-label="Duplicate row"
                    @click="duplicateAdditionalInfo(item)"
                  />
                  <UButton
                    icon="i-lucide-x"
                    color="error"
                    variant="outline"
                    size="sm"
                    aria-label="Remove row"
                    @click="removeAdditionalInfo(item)"
                  />
                </div>
              </div>

              <div v-else class="no-data">
                No adjustments. Add a tip, discount, fee or tax if the total
                changes.
              </div>
            </div>
          </div>

          <UButton type="submit"> Submit </UButton>
        </UForm>
      </div>
    </template>
  </UModal>
</template>

<style scoped>
.form-wrapper {
  overflow-y: scroll;
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

.modal-row-2 {
  display: grid;
  grid-template-columns: 3fr 3fr 1fr;
  align-items: center;
  gap: 8px;
  padding: 8px;
}

.form-wrapper {
  padding: 8px;
}

.check-box-custom-date {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 4px;
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

@media (max-width: 720px) {
  .modal-row-3 {
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
}
</style>
