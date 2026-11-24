<script setup lang="ts">
import type { AlertInformation } from '~/services/alerts/types';
import type { DropdownMenuItem } from '@nuxt/ui'
import { label } from '@unovis/ts/components/axis/style';
import AlertConditionBar from './AlertConditionBar.vue'
import { formatDateTime } from '~/utilis/formaters/dateTimeFormater.ts';
import { apiRouter } from '~/services/serviceWrapper.ts';
import { useQueryClient } from '@tanstack/vue-query';


interface Props {
    alertInformation: AlertInformation | null,
    currency: string,
    accountId: string
}

const props = withDefaults(defineProps<Props>(), {
    alertInformation: null,
    currency: 'EUR'
})




function amountFor(categoryId: string): number {
  const state = props.alertInformation?.alertStates.find(s => s.categoryId === categoryId)
  return state ? state.amount : 0
}

const deleteAlertMutation = apiRouter.alertApi().deleteAlert()
const queryClient = useQueryClient()
const items = computed<DropdownMenuItem[][]>(() => [
  [
    {
      label: 'Edit Alert',
      icon: 'i-lucide-pencil',
      to: '/alerts/' + props.alertInformation?.alertId
    },
    {
      label: 'Delete Alert',
      icon: 'i-lucide-trash',
      color: 'error',
      async onSelect() {
        const alertId = props.alertInformation?.alertId
        if (!alertId) return
        try {
          await deleteAlertMutation.mutateAsync({ alertId })
          await queryClient.invalidateQueries({
            queryKey: ['account', 'get', props.accountId, 'transactions']
          })
        } catch (err) {
          console.log(err)
        }
      }
    }
  ]
])
</script>

<template>
    <div class="alert-component">


        <div v-if="alertInformation !== null"
            class="props.alertInformation?.breached ? 'breached-alert-wrapper' : 'active-alert-wrapper'">

            <div class="alert-header-wrapper">

                <div class="alert-name-status">
                    <span class="alert-name">
                        {{ props.alertInformation?.name }}
                    </span>

                    <div class="right-side">
                        <span class="alert-breached">
                            {{ props.alertInformation?.breached ? 'Breached' : 'Active' }}
                        </span>
                        <span class="seperator-dot">
                            ·
                        </span>

                        <UDropdownMenu :items="items" class="alert-menu-options">
                            <UButton icon="i-lucide-menu" color="neutral" variant="outline" class="alert-menu-button" />
                        </UDropdownMenu>
                    </div>

                </div>

                <div class="alert-date-range">
                    <span class="date-range">
                        {{ formatDateTime(props.alertInformation?.dateFrom as string) + ' - ' + formatDateTime(props.alertInformation?.dateTo as string) }}
                    </span>

                    <UTooltip :text="props.alertInformation?.description ?? ''" :ui="{
                        content: 'bg-gray-900 text-white text-sm px-3 py-2 rounded-lg shadow-lg max-w-xs',
                        arrow: 'bg-gray-900'
                    }">
                        <UIcon name="i-lucide-info" class="size-5 text-muted" />
                    </UTooltip>
                </div>

            </div>

            <div class="conditions-list">
              
              <AlertConditionBar
                v-for="filter in alertInformation.filters"
                :key="filter.conditionId"
                :condition="filter"
                :amount="amountFor(filter.categoryId)"
                :currency="props.currency"
              />
            </div>


        </div>

        <div v-else class="no-alert">
            No alert found ...
        </div>


    </div>
</template>


<style scoped>

.alert-component {
  width: 100%;
  font-family: sans-serif;
}

.active-alert-wrapper,
.breached-alert-wrapper {
  padding: 16px;
  border: 1px solid #d0d0d0;
  border-radius: 10px;
}

.breached-alert-wrapper {
  border-color: #c62828;
}

.alert-header-wrapper {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.alert-name-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.alert-name {
  font-weight: 600;
  font-size: 1rem;
}

.right-side {
  display: flex;
  align-items: center;
  gap: 8px;
}

.alert-breached {
  font-size: 0.8rem;
  font-weight: 600;
}

.alert-breached.is-active {
  color: #2e7d32;
}

.alert-breached.is-breached {
  color: #c62828;
}

.seperator-dot {
  color: #999;
}

.conditions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.alert-menu-options {
  position: relative;
}

.alert-menu-button {
  padding: 4px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
}

.alert-menu-button:hover {
  background: #f3f3f3;
}

.menu-backdrop {
  position: fixed;
  inset: 0;
  z-index: 10;
}

.menu-panel {
  position: absolute;
  right: 0;
  top: calc(100% + 4px);
  z-index: 11;
  min-width: 140px;
  display: flex;
  flex-direction: column;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.12);
  overflow: hidden;
}

.menu-item {
  padding: 8px 12px;
  border: none;
  background: none;
  text-align: left;
  font-size: 0.85rem;
  color: #222;
  text-decoration: none;
  cursor: pointer;
}

.menu-item:hover {
  background: #f3f3f3;
}

.alert-date-range {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
  color: #777;
}




.no-alert {
  color: #777;
  padding: 16px;
}

</style>