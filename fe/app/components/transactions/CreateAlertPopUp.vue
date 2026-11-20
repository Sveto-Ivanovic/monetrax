<script setup lang="ts">
import * as v from 'valibot';
import type { FormSubmitEvent } from '@nuxt/ui';
import { CalendarDate } from '@internationalized/date';

const uuid = () => crypto.randomUUID();
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
const filtersToCreateSchema = v.pipe(
  v.object({
    Id: v.string(),
    categories: v.object(
      {
        categoryId: v.pipe(v.string(), v.nonEmpty('Select a category')),
        name: v.string(),
      },
      'Select a category'
    ),
    ruleType: v.picklist(
      [
        'LESS_OR_EQUAL',
        'GREATER_OR_EQUAL',
        'BETWEEN',
        'EQUAL',
        'LESS',
        'GREATER',
      ],
      'Select a valid kind of rule'
    ),
    limitValueLowOrEqual: v.pipe(
      v.number('Value is required'),
      v.minValue(0, 'Must be greater than 0')
    ),
    limitValueHigh: v.pipe(
      v.number('Value is required'),
      v.minValue(0, 'Must be at least 0')
    ),
  }),

  v.forward(
    v.partialCheck(
      [['ruleType'], ['limitValueLowOrEqual']],
      (input) =>
        !(
          ['GREATER', 'GREATER_OR_EQUAL', 'EQUAL', 'BETWEEN'].includes(
            input.ruleType
          ) &&
          (input.limitValueLowOrEqual === undefined ||
            input.limitValueLowOrEqual <= 0)
        ),
      'Value is required and must be greater than 0'
    ),
    ['limitValueLowOrEqual']
  ),

  v.forward(
    v.partialCheck(
      [['ruleType'], ['limitValueHigh']],
      (input) =>
        !(
          ['LESS', 'LESS_OR_EQUAL', 'BETWEEN'].includes(input.ruleType) &&
          (input.limitValueHigh === undefined || input.limitValueHigh <= 0)
        ),
      'Value is required and must be greater than 0'
    ),
    ['limitValueHigh']
  ),

  v.forward(
    v.partialCheck(
      [['ruleType'], ['limitValueLowOrEqual'], ['limitValueHigh']],
      (input) =>
        !(
          input.ruleType === 'BETWEEN' &&
          input.limitValueLowOrEqual !== undefined &&
          input.limitValueHigh !== undefined &&
          input.limitValueHigh <= input.limitValueLowOrEqual
        ),
      'Must be greater than the lower value'
    ),
    ['limitValueHigh']
  )
);

const schema = v.object({
  name: v.pipe(v.string(), v.minLength(4, 'Must be at least 4 characters')),
  description: v.pipe(v.string()),

  dateFrom: v.pipe(
    v.instance(CalendarDate, 'Beginning date of alert period is necessary')
  ),
  dateTo: v.pipe(
    v.instance(CalendarDate, 'End date of alert period is necessary')
  ),

  // custom elements
  filtersToCreate: v.pipe(v.array(filtersToCreateSchema)),

  // recurence params
  recurrenceNum: v.pipe(v.number(), v.minValue(1, 'Value minimum 1')),
  numberOfOccurrences: v.pipe(v.number(), v.minValue(1, 'Min 1 recurrence')),
  ruleType: v.picklist(
    ['EVERY_WEEK', 'EVERY_MONTH', 'EVERY_YEAR', 'EVERY_SPAN'],
    'Select a valid rule type'
  ),
});

type Schema = v.InferOutput<typeof schema>;

// A filter row starts without a category selected, so the select can show its placeholder
type FilterRow = Omit<Schema['filtersToCreate'][number], 'categories'> & {
  categories?: Schema['filtersToCreate'][number]['categories'];
};

const state = reactive<
  Omit<Partial<Schema>, 'filtersToCreate'> & { filtersToCreate: FilterRow[] }
>({
  name: '',
  description: '',

  dateFrom: markRaw(new CalendarDate(2026, 11, 20)),
  dateTo: markRaw(new CalendarDate(2027, 11, 20)),

  filtersToCreate: [
    {
      Id: 'a1b2c3d4-0000-4000-8000-000000000001',
      categories: undefined,
      ruleType: 'BETWEEN',
      limitValueLowOrEqual: 50,
      limitValueHigh: 200,
    },
  ],

  numberOfOccurrences: 5,
  ruleType: 'EVERY_MONTH',
  recurrenceNum: 1,
});

function addFilter() {
  state.filtersToCreate.push({
    Id: uuid(),
    categories: undefined,
    ruleType: 'LESS',
    limitValueLowOrEqual: 20,
    limitValueHigh: 0,
  });
}

function removeFilter(item: FilterRow) {
  state.filtersToCreate = state.filtersToCreate.filter((i) => i.Id !== item.Id);
}

const checkboxValue = ref(false);
const inputDate = useTemplateRef('inputDate');
const inputDate1 = useTemplateRef('inputDate1');

async function onSubmit(event: FormSubmitEvent<Schema>) {
  console.log(event.data);
}
</script>

<template>
  <div>
    <UModal :ui="{ content: 'sm:max-w-3xl' }">
      <UButton label="Open" color="neutral" variant="subtle" />

      <template #content="{ close }">
        <div class="form-wrapper">
          <div class="form-creation-header">
            <h2 class="form-title">Create Alert</h2>
            <UButton
              icon="i-lucide-x"
              color="neutral"
              variant="ghost"
              size="sm"
              aria-label="Close"
              @click="close()"
            />
          </div>

          <UForm
            :schema="schema"
            :state="state"
            class="space-y-4"
            @submit="onSubmit"
          >
            <UFormField label="Alert Name" name="name">
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
              <UFormField label="Date From" name="dateFrom">
                <UInputDate ref="inputDate" v-model="state.dateFrom">
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
                        <UCalendar v-model="state.dateFrom" class="p-2" />
                      </template>
                    </UPopover>
                  </template>
                </UInputDate>
              </UFormField>

              <UFormField label="Date To" name="dateTo">
                <UInputDate ref="inputDate1" v-model="state.dateTo">
                  <template #trailing>
                    <UPopover :reference="inputDate1?.inputsRef[3]?.$el">
                      <UButton
                        color="neutral"
                        variant="link"
                        size="sm"
                        icon="i-lucide-calendar"
                        aria-label="Select a date"
                        class="px-0"
                      />

                      <template #content>
                        <UCalendar v-model="state.dateTo" class="p-2" />
                      </template>
                    </UPopover>
                  </template>
                </UInputDate>
              </UFormField>
            </div>

            <div class="additional-info-wrapper">
              <div class="add-inf-header">
                <span> Filters </span>

                <UButton color="neutral" @click="addFilter()" variant="outline">
                  + Add row</UButton
                >
              </div>

              <div class="add-inf-content">
                <div
                  class="modal-row-5"
                  v-for="(item, index) in state.filtersToCreate"
                  :key="item.Id"
                >
                  <UFormField
                    label="Select Category"
                    :name="`filtersToCreate.${index}.categories`"
                  >
                    <USelectMenu
                      v-model="item.categories"
                      :items="props.categories"
                      label-key="name"
                      class="w-full"
                      placeholder="Choose a category"
                    />
                  </UFormField>

                  <UFormField
                    label="Filter Type"
                    :name="`filtersToCreate.${index}.ruleType`"
                  >
                    <USelect
                      v-model="item.ruleType"
                      :items="[
                        'LESS_OR_EQUAL',
                        'GREATER_OR_EQUAL',
                        'BETWEEN',
                        'EQUAL',
                        'LESS',
                        'GREATER',
                      ]"
                      class="w-full"
                    />
                  </UFormField>

                  <UFormField
                    label="Lower Bound"
                    :name="`filtersToCreate.${index}.limitValueLowOrEqual`"
                  >
                    <UInput
                      v-model="item.limitValueLowOrEqual"
                      type="number"
                      class="w-full"
                    />
                  </UFormField>

                  <UFormField
                    label="Upper Bound"
                    :name="`filtersToCreate.${index}.limitValueHigh`"
                  >
                    <UInput
                      v-model="item.limitValueHigh"
                      type="number"
                      class="w-full"
                    />
                  </UFormField>

                  <div class="row-actions" v-if="index >= 1">
                    <UButton
                      icon="i-lucide-x"
                      color="error"
                      variant="outline"
                      size="sm"
                      aria-label="Remove row"
                      @click="removeFilter(item)"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div class="check-box-custom-date">
              <span> Set alert as recurring </span>
              <UCheckbox v-model="checkboxValue" />
            </div>

            <div class="modal-row-h-3" :class="checkboxValue ? 'mshow' : ''">
              <UFormField label="Repeat every" name="recurrenceNum">
                <UInput
                  v-model="state.recurrenceNum"
                  class="w-full"
                  type="number"
                />
              </UFormField>

              <UFormField label="Recurrence Type" name="ruleType">
                <USelect
                  v-model="state.ruleType"
                  :items="[
                    'EVERY_WEEK',
                    'EVERY_MONTH',
                    'EVERY_YEAR',
                    'EVERY_SPAN',
                  ]"
                  class="w-full"
                />
              </UFormField>

              <UFormField
                label="Max Recurrence Num."
                name="numberOfOccurrences"
              >
                <UInput
                  v-model="state.numberOfOccurrences"
                  class="w-full"
                  type="number"
                />
              </UFormField>
            </div>

            <UButton type="submit"> Submit </UButton>
          </UForm>
        </div>
      </template>
    </UModal>
  </div>
</template>

<style scoped>
.form-wrapper {
  padding: 16px;
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
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

.modal-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.additional-info-wrapper {
  border: 1px solid lightgray;
  border-radius: 12px;
  overflow: hidden;
}

.add-inf-header {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  width: 100%;
  align-items: center;
  padding: 6px 6px 6px 12px;
  border-bottom: 1px solid lightgray;
  background-color: #efeff5;
}

.add-inf-content {
  display: flex;
  flex-direction: column;
}

.modal-row-5 {
  display: grid;
  grid-template-columns: 1.4fr 1.4fr 1.2fr 1.2fr 32px;
  align-items: start;
  gap: 12px;
  padding: 12px;
}

.modal-row-5:not(:last-child) {
  border-bottom: 1px solid lightgray;
}

.row-actions {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  margin-top: 26px;
  flex-shrink: 0;
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

.modal-row-h-3 {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  max-height: 0px;
  overflow: hidden;
  gap: 16px;
  transition: max-height 0.5s ease;
}

.modal-row-h-3.mshow {
  max-height: 300px;
}

@media (max-width: 720px) {
  .modal-row-h-3 {
    grid-template-columns: 1fr 1fr;
  }

  .modal-row-5 {
    grid-template-columns: 1fr 1fr;
  }

  .row-actions {
    grid-column: 1 / -1;
    justify-content: flex-end;
    margin-top: 0;
  }
}

@media (max-width: 520px) {
  .modal-row {
    grid-template-columns: 1fr;
  }

  .modal-row-h-3 {
    grid-template-columns: 1fr;
  }

  .modal-row-5 {
    grid-template-columns: 1fr;
  }
}
</style>
