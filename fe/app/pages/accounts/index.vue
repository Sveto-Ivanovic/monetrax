<template>
    <div class="accounts">

        <header class="accounts-header">
            <div>
                <h1 class="accounts-title">Accounts</h1>
                <p class="accounts-subtitle">
                    Manage your accounts and track balances in one place.
                </p>
            </div>
            <div class="account-state">
                <span class="accounts-count">
                    {{ activeCount }} active · {{ archivedCount }} archived
                </span>
                <USelect v-model="value" :items="items" />

            </div>

        </header>



        <div class="accounts-wrapper">

            <div class="account-create" @click="isOpen = true">
                <div>
                    <span class="create-plus">+</span>
                    <span class="add-accounnt">Create Account</span>
                </div>
            </div>


            <ULink v-for="item in showAccounts" :to="'accounts/' + item.accountId" class="account-link">
                <div class="account-info">

                    <div class="name-active">
                        <span class="acc-name"> {{ item.name }}</span>
                        <span :class="item.active ? 'acc-active' : 'acc-archived'">{{ item.active ? 'active' :
                            'archived'
                        }}</span>
                    </div>

                    <div class="acc-desc">
                        {{ item.description }}
                    </div>

                    <div class="acc-ammount">
                        {{ formatCurrency(item.currentBalance, item.currency) }}
                    </div>


                    <div class="acc-institution-4digit">
                        <span class="acc-inst">{{ item.institutionName }}</span>
                        <span class="acc-digit"> {{ item.accountNumberMasked }} </span>
                    </div>

                </div>
            </ULink>


            <UModal v-model:open="isOpen" :ui="{
                overlay: 'bg-slate-900/40 backdrop-blur-sm',
                content: 'account-modal-content'
            }">
                <template #content>
                    <div class="account-modal">

                        <header class="modal-header">
                            <h2 class="modal-title">Add new account</h2>
                            <p class="modal-subtitle">Enter the details of the account you want to track.</p>
                        </header>

                        <UForm :schema="schema" :state="state" class="modal-form" @submit="onSubmit">

                            <UFormField label="Account Name" name="name">
                                <UInput v-model="state.name" class="w-full" placeholder="Main Checking Account" />
                            </UFormField>

                            <div class="modal-row">
                                <UFormField label="Institution Name" name="institutionName">
                                    <UInput v-model="state.institutionName" class="w-full" placeholder="NLB Bank" />
                                </UFormField>

                                <UFormField label="Last Four Digits" name="fourLastDigits">
                                    <UInput v-model="state.fourLastDigits" class="w-full" inputmode="numeric"
                                        maxlength="4" placeholder="1234" />
                                </UFormField>
                            </div>

                            <div class="modal-row">
                                <UFormField label="Currency" name="currency">
                                    <USelectMenu v-model="state.currency" :items="itemsCurrency1" class="w-full" />
                                </UFormField>

                                <UFormField label="Initial Balance" name="initalBalance">
                                    <UInput v-model="state.initalBalance" type="number" class="w-full" />
                                </UFormField>
                            </div>

                            <UFormField label="Description" name="description">
                                <UTextarea v-model="state.description" class="w-full" :rows="3"
                                    placeholder="What is this account used for?" />
                            </UFormField>

                            <footer class="modal-actions">
                                <UButton type="button" color="neutral" variant="ghost" @click.prevent="isOpen = false">
                                    Cancel
                                </UButton>
                                <UButton type="submit" class="modal-submit">
                                    Create Account
                                </UButton>
                            </footer>

                        </UForm>
                    </div>
                </template>
            </UModal>



        </div>

    </div>
</template>

<script setup lang="ts">
import * as v from 'valibot'
import type { FormSubmitEvent } from '@nuxt/ui'
import { formatCurrency } from '~/utilis/formaters/balanceFormater';
import { itemsCurrency as itemsCurrency1 } from '~/components/data/currencies'

const isOpen = ref(false);
const items = ref(['Active', 'Archived', 'All'])
const value = ref('Active')
const activeCount = computed(() => accounts.filter(a => a.active).length);
const archivedCount = computed(() => accounts.filter(a => !a.active).length);

const schema = v.object({
    name: v.pipe(v.string(), v.minLength(5, 'Account name must be 5 letters or more')),
    institutionName: v.pipe(v.string(), v.minLength(5, 'Institution name must be 5 letters or more')),
    description: v.pipe(v.string(), v.minLength(6, 'Description must be 6 letters or more')),
    currency: v.pipe(v.string(), v.minLength(3, 'Currency must be 3 letters long')),
    fourLastDigits: v.pipe(v.string(), v.regex(/^\d{4}$/, 'Enter exactly 4 digits')),
    initalBalance: v.pipe(v.number(), v.minValue(0))

})

type Schema = v.InferOutput<typeof schema>

const state = reactive({
    name: '',
    institutionName: '',
    description: '',
    currency: 'EUR',
    fourLastDigits: '',
    initalBalance: 0,
})

async function onSubmit(event: FormSubmitEvent<Schema>) {
    console.log(event.data)
}




let showAccounts = computed<{
    accountId: string;
    name: string;
    description: string;
    currentBalance: number;
    currency: string;
    institutionName: string;
    accountNumberMasked: string;
    active: boolean;
}[]>(() => {
    if (value.value === "All") {
        return accounts
    } else if (value.value === "Active") {
        return accounts.filter(x => x.active)
    } else {
        return accounts.filter(x => !x.active)
    }
})



const accounts = [
    {
        accountId: 'a1b2c3d4-e5f6-7890-abcd-123456789001',
        name: 'Main Checking Account',
        description: 'Everyday spending and transactions Everyday spending and transactions Everyday spending and transactions Everyday spending and transactions Everyday spending and transactions Everyday spending and transactions Everyday spending and transactions Everyday spending and transactions Everyday spending and transactions Everyday spending and transactions',
        currentBalance: 4250.75,
        currency: 'EUR',
        institutionName: 'NLB Bank',
        accountNumberMasked: '4521',
        active: true,
    },
    {
        accountId: 'a1b2c3d4-e5f6-7890-abcd-123456789002',
        name: 'Savings Account',
        description: 'Long-term savings and emergency fund',
        currentBalance: 12500.00,
        currency: 'EUR',
        institutionName: 'CKB Bank',
        accountNumberMasked: '7832',
        active: true,
    },
    {
        accountId: 'a1b2c3d4-e5f6-7890-abcd-123456789003',
        name: 'Business Account',
        description: 'Business income and operational expenses',
        currentBalance: 8750.50,
        currency: 'EUR',
        institutionName: 'Erste Bank',
        accountNumberMasked: '1945',
        active: true,
    },
    {
        accountId: 'a1b2c3d4-e5f6-7890-abcd-123456789004',
        name: 'Travel Savings',
        description: 'Savings for vacations and travel',
        currentBalance: 2100.00,
        currency: 'USD',
        institutionName: 'NLB Bank',
        accountNumberMasked: '6201',
        active: true,
    },
    {
        accountId: 'a1b2c3d4-e5f6-7890-abcd-123456789005',
        name: 'Old Savings Account',
        description: 'Previously used savings account',
        currentBalance: 0.00,
        currency: 'EUR',
        institutionName: 'CKB Bank',
        accountNumberMasked: '3389',
        active: false,
    },
];

definePageMeta({
    middleware: ['require-user-info'],
    layout: 'dashboardlayout'
})
</script>

<style scoped>
.accounts {
    display: flex;
    flex-direction: column;
    gap: 32px;
}

.accounts-header {
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 12px;
}

.accounts-title {
    font-size: 2rem;
    font-weight: 700;
    color: #111827;
    line-height: 1.2;
}

.accounts-subtitle {
    margin-top: 4px;
    font-size: 1rem;
    color: rgb(126, 143, 158);
}

.accounts-count {
    font-size: 1rem;
    font-weight: 500;
    color: rgb(126, 143, 158);
    padding: 4px 12px;
    border: 1px solid rgba(0, 0, 0, 0.06);
    border-radius: 999px;
    background: #fff;
}

.account-create {
    cursor: pointer;
    padding: 24px;
    background-color: rgb(255, 255, 255);

    border: 1px solid rgba(0, 0, 0, 0.06);
    border-radius: 16px;

    box-shadow:
        0 2px 4px rgba(0, 0, 0, 0.02),
        0 8px 24px rgba(0, 0, 0, 0.06);

    display: flex;
    justify-content: center;
    align-items: center;

    max-width: 300px;
    min-height: 300px;
}

.account-create div {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

}

.create-plus {
    font-size: 150px;
    font-weight: 100;
    line-height: 0.8;
    color: rgb(126, 143, 158);
}

.add-accounnt {
    color: rgb(126, 143, 158);
}

.account-create:hover {
    background-color: #fafbff;
    border-color: rgba(79, 70, 229, 0.35);

    box-shadow:
        0 8px 20px rgba(79, 70, 229, 0.06),
        0 20px 40px rgba(0, 0, 0, 0.06);
}

.account-create:hover .create-plus {
    color: #4f46e5;
    transform: scale(1.01);
}

.account-create:hover .add-accounnt {
    color: #4f46e5;
}

.account-create:active {
    transform: translateY(-1px) scale(0.98);
}

.account-create:focus-visible {
    outline: 3px solid rgba(79, 70, 229, 0.3);
    outline-offset: 4px;
}

.accounts-wrapper {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 300px));
    gap: 24px;
    align-items: stretch;
}

.account-create {
    width: 100%;}

.account-link {
    display: block;
    text-decoration: none;
    color: inherit;
    border-radius: 16px;
}

.account-link:focus-visible {
    outline: 3px solid rgba(79, 70, 229, 0.3);
    outline-offset: 4px;
}

.account-info {
    display: flex;
    flex-direction: column;
    gap: 10px;

    height: 100%;
    max-width: 300px;
    min-height: 300px;
    padding: 24px;

    background-color: rgb(255, 255, 255);
    border: 1px solid rgba(0, 0, 0, 0.06);
    border-radius: 16px;

    box-shadow:
        0 2px 4px rgba(0, 0, 0, 0.02),
        0 8px 24px rgba(0, 0, 0, 0.06);

    transition: all 0.2s ease;
}

.account-link:hover .account-info {
    background-color: #fafbff;
    border-color: rgba(79, 70, 229, 0.35);
    transform: translateY(-2px);

    box-shadow:
        0 8px 20px rgba(79, 70, 229, 0.06),
        0 20px 40px rgba(0, 0, 0, 0.06);
}

.account-link:active .account-info {
    transform: translateY(-1px) scale(0.98);
}

.name-active {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 8px;
}

.acc-name {
    font-size: 1.05rem;
    font-weight: 600;
    line-height: 1.3;
    color: #1f2937;
}

.acc-active,
.acc-archived {
    flex-shrink: 0;
    padding: 3px 10px;
    border-radius: 999px;
    font-size: 0.65rem;
    font-weight: 600;
    letter-spacing: 0.05em;
    text-transform: uppercase;
}

.acc-active {
    background: rgba(34, 197, 94, 0.12);
    color: #16a34a;
}

.acc-archived {
    background: rgba(126, 143, 158, 0.15);
    color: rgb(126, 143, 158);
}

.acc-desc {
    font-size: 0.85rem;
    line-height: 1.5;
    color: rgb(126, 143, 158);

    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    line-clamp: 3;
    overflow: hidden;
    overflow-wrap: break-word;
}

.acc-ammount {
    margin-top: auto;
    font-size: 1.75rem;
    font-weight: 700;
    font-variant-numeric: tabular-nums;
    color: #111827;
    transition: color 0.2s ease;
}

.account-link:hover .acc-ammount {
    color: #4f46e5;
}

.acc-institution-4digit {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 12px;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
    font-size: 0.8rem;
    color: rgb(126, 143, 158);
}

.acc-inst {
    font-weight: 500;
}

.acc-digit {
    font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
    letter-spacing: 0.08em;
}

.acc-digit::before {
    content: '•••• ';
    opacity: 0.6;
}

.account-link:has(.acc-archived) .account-info {
    opacity: 0.7;
}

.account-state {
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 8px;
}

.account-modal {
    display: flex;
    flex-direction: column;
    gap: 24px;
    padding: 28px;
}

.modal-title {
    font-size: 1.5rem;
    font-weight: 700;
    line-height: 1.2;
    color: #111827;
}

.modal-subtitle {
    margin-top: 4px;
    font-size: 0.9rem;
    color: rgb(126, 143, 158);
}

.modal-form {
    display: flex;
    flex-direction: column;
    gap: 18px;
}

.modal-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    padding-top: 20px;
    margin-top: 4px;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.modal-submit {
    background-color: #4f46e5;
    color: #fff;
    padding-inline: 18px;
}

.modal-submit:hover {
    background-color: #4338ca;
}

@media (max-width: 520px) {
    .modal-row {
        grid-template-columns: 1fr;
    }
}
</style>
