<template>
    <div class="login-page-wrapper">


        <UForm :schema="schema" :state="state" class="form-class" @submit="onSubmit">

            <div class="register-wrapper" style="cursor: pointer;" @click="redirectHome()">

                <span class="brand-mark" aria-hidden="true">
                    <span></span>
                    <span></span>
                    <span></span>
                </span>

                <span class="register-txt"> Monetrax </span>
            </div>

            <div class="login-header">
                <p>Sign Up to get started!</p>
            </div>


            <UFormField label="Username" name="username">
                <UInput v-model="state.username" placeholder="Enter your username" />
            </UFormField>

            <UFormField label="Name" name="name">
                <UInput v-model="state.name" placeholder="Enter your name" />
            </UFormField>

            <UFormField label="Surname" name="surname">
                <UInput v-model="state.surname" placeholder="Enter your surname" />
            </UFormField>

            <UFormField label="Date of birth" name="dateOfBirth">

            <UInputDate ref="inputDate" v-model="state.dateOfBirth">
                <template #trailing>
                    <UPopover :reference="inputDate?.inputsRef[3]?.$el">
                        <UButton color="neutral" variant="link" size="sm" icon="i-lucide-calendar"
                            aria-label="Select a date" class="px-0" />

                        <template #content>
                            <UCalendar v-model="state.dateOfBirth" class="p-2" />
                        </template>
                    </UPopover>
                </template>
            </UInputDate>
            </UFormField>   

            <UFormField label="Email" name="email">
                <UInput v-model="state.email" placeholder="example@gmail.com" />
            </UFormField>

            <UFormField label="Password" name="password">
                <UInput v-model="state.password" placeholder="••••••••" :type="show ? 'text' : 'password'"
                    :ui="{ trailing: 'pe-1' }">
                    <template #trailing>
                        <UButton color="neutral" variant="link" size="sm"
                            :icon="show ? 'i-lucide-eye-off' : 'i-lucide-eye'"
                            :aria-label="show ? 'Hide password' : 'Show password'" :aria-pressed="show"
                            aria-controls="password" @click="show = !show" />
                    </template>
                </UInput>
            </UFormField>


            <UFormField label="Confirm Password" name="confirmPassword">
                <UInput v-model="state.confirmPassword" placeholder="••••••••" :type="showConfirm ? 'text' : 'password'"
                    :ui="{ trailing: 'pe-1' }">
                    <template #trailing>
                        <UButton color="neutral" variant="link" size="sm"
                            :icon="showConfirm ? 'i-lucide-eye-off' : 'i-lucide-eye'"
                            :aria-label="showConfirm ? 'Hide password' : 'Show password'" :aria-pressed="showConfirm"
                            aria-controls="password" @click="showConfirm = !showConfirm" />
                    </template>
                </UInput>
            </UFormField>

            <div class="flex justify-start" style="padding-top:24px; padding-bottom: 24px;">
                <div class="flex items-center gap-2">
                    <UCheckbox style=" " v-model="acceptTerms" />
                    <span class="text-sm text-gray-600">I accept the </span>
                    <ULink to="/terms" class="text-sm text-blue-600 hover:underline">Terms and Conditions</ULink>
                    <span class="text-sm text-gray-600">and </span>
                    <ULink to="/privacy" class="text-sm text-blue-600 hover:underline">Privacy Policy</ULink>

                </div>
            </div>

            <div class="flex justify-center">
                <UButton type="submit" class="submit-button">
                    Submit
                </UButton>
            </div>

            <div class="flex justify-center" style="padding-top: 6px;">
                <span class="text-sm text-gray-600">Already have an account? </span>
                <ULink to="/login" class="text-sm text-blue-600 hover:underline" style="padding-left: 4px;">Sign In
                </ULink>
            </div>
        </UForm>



    </div>
</template>


<script setup lang="ts">
import * as v from 'valibot'
import type { FormSubmitEvent } from '@nuxt/ui'
import { CalendarDate, today } from '@internationalized/date'


const show = ref(false)
const showConfirm = ref(false)
const acceptTerms = ref(false)


const inputDate = useTemplateRef('inputDate')

const modelValue = shallowRef(new CalendarDate(2022, 1, 10))

const schema = v.pipe(

    v.object({
        username: v.pipe(v.string(), v.minLength(3, 'Username must be at least 3 characters long')),
        name: v.pipe(v.string(), v.minLength(3, 'Name must be at least 3 characters long')),
        surname: v.pipe(v.string(), v.minLength(3, 'Surname must be at least 3 characters long')),
        dateOfBirth: v.pipe(
            v.instance(CalendarDate, 'Date of birth is required'),
            v.check(
                (d) => d.compare(today("UTC")) <= 0,
                'Date of birth cannot be in the future'
            )
        ),        email: v.pipe(v.string(), v.email('Invalid email')),
        password: v.pipe(v.string(),
            v.regex(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/, 'Must contain uppercase, lowercase, number, and special character'),
            v.minLength(8, 'Must be at least 8 characters')
        ),
        confirmPassword: v.pipe(v.string(),
            v.regex(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/, 'Must contain uppercase, lowercase, number, and special character'),
            v.minLength(8, 'Must be at least 8 characters')
        ),

    }),
    v.forward(
        v.partialCheck(
            [['password'], ['confirmPassword']],
            (input) => input.password === input.confirmPassword,
            'The two passwords do not match.'
        ),
        ['confirmPassword']
    )
)

type Schema = v.InferOutput<typeof schema>

const state = shallowReactive({
    username: '',
    name: '',
    surname: '',
    dateOfBirth: new CalendarDate(2022, 1, 10),
    email: '',
    password: '',
    confirmPassword: ''
})

const is_loading = ref(false)

function redirectHome() {
    navigateTo('/')
}

async function onSubmit(event: FormSubmitEvent<Schema>) {
    is_loading.value = true
    console.log(event.data)
    /* small delay to simulate a network request */
    await new Promise(resolve => setTimeout(resolve, 1000))
    is_loading.value = false
}


</script>


<style scoped>
.login-page-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    gap: 16px;
    padding-top: 32px;
    padding-bottom: 32px;
}

.form-class {
    display: flex;
    flex-direction: column;
    gap: 16px;
    width: 550px;
    padding: 24px;
    background-color: white;
    border-radius: 14px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.form-class :deep(label) {
    font-weight: 600;
    font-size: 16px;
    font-family: Verdana, Geneva, Tahoma, sans-serif;
    color: #7176a1;
}

.form-class :deep(input) {
    font-size: 16px;
    font-family: Verdana, Geneva, Tahoma, sans-serif;
    color: #2d3153;
    width: 450px;
    font-weight: 560;
    font-size: 14px;
    padding: 6px;
    border: 1px solid #d1d5db;
}


.register-wrapper {
    gap: 16px;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
}


.brand-mark {
    width: 50px;
    height: 50px;
    display: flex;
    align-items: end;
    justify-content: center;
    gap: 6px;
    padding: 8px;
    background: #050a47;
    border-radius: 8px;
}

.brand-mark span {
    display: block;
    width: 8px;
    border-radius: 3px;
    background: #a8ead2;
}

.brand-mark span:nth-child(1) {
    height: 16px;
}

.brand-mark span:nth-child(2) {
    height: 26px;
}

.brand-mark span:nth-child(3) {
    height: 20px;
}

.register-txt {
    font-family: 'Montserrat', sans-serif;
    font-weight: 700;
    background: linear-gradient(90deg, rgba(2, 0, 36, 1) 0%, rgba(9, 9, 121, 1) 35%, rgba(0, 212, 255, 1) 100%);
    -webkit-background-clip: text;
    background-clip: text;
    font-size: 26px;
    -webkit-text-fill-color: transparent;
    color: transparent;
    font-size: 48px;
    letter-spacing: -0.8px;
}

.login-header {
    text-align: center;
}

.login-header h1 {
    font-size: 32px;
    font-weight: 700;
    margin-bottom: 8px;
    color: rgb(6, 6, 117);
    padding-top: 24px;
    padding-bottom: 8px;
}

.login-header p {
    font-size: 18px;
    color: #6b7280;
    padding-bottom: 24px;
}

.submit-button {
    background-color: #050a47;
    color: white;
    font-size: 18px;
    font-weight: 600;
    padding: 12px 24px;
    border-radius: 24px;
    transition: background-color 0.3s, box-shadow 0.3s, transform 0.3s;
    cursor: pointer;
    width: 80%;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
}
</style>