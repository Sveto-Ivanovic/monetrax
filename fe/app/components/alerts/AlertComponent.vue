<script setup lang="ts">
import type { AlertInformation } from '~/services/alerts/types';
import type { DropdownMenuItem } from '@nuxt/ui'
import { label } from '@unovis/ts/components/axis/style';


interface Props {
    alertInformation: AlertInformation | null
}

const props = withDefaults(defineProps<Props>(), {
    alertInformation: null
})


const items = ref<DropdownMenuItem[][]>([
    [
        {
            label: "Delete Alert",
            type: "label",
            onSelect: () => {
                console.log("Deleting alert with id:" + props.alertInformation?.alertId)



            }
        },
        {
            label: "Edit Alert",
            type: "label",
            to: '/alerts/' + props.alertInformation?.alertId
        },
    ],



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
                        {{ props.alertInformation?.dateFrom + ' - ' + props.alertInformation?.dateTo }}
                    </span>

                    <UTooltip :text="props.alertInformation?.description ?? ''" :ui="{
                        content: 'bg-gray-900 text-white text-sm px-3 py-2 rounded-lg shadow-lg max-w-xs',
                        arrow: 'bg-gray-900'
                    }">
                        <UIcon name="i-lucide-info" class="size-5 text-muted" />
                    </UTooltip>
                </div>

            </div>


            <div>



                
            </div>





        </div>

        <div v-else class="no-alert">
            No alert found ...
        </div>


    </div>
</template>

<style scoped></style>