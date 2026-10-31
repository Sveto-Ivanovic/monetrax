<template>
  <div class="login-page-wrapper">


    <UForm :schema="schema" :state="state" class="form-class" @submit="onSubmit">

      <div class="logo-wrapper" style="cursor: pointer;" @click="redirectHome()">

        <span class="brand-mark" aria-hidden="true">
          <span></span>
          <span></span>
          <span></span>
        </span>

        <span class="logo-txt"> Monetrax </span>
      </div>

      <div class="login-header">
        <p>Welcome back! Please enter your details.</p>
      </div>


      <UFormField label="Email" name="email">
        <UInput v-model="state.email" placeholder="example@gmail.com" />
      </UFormField>

      <UFormField label="Password" name="password">
        <UInput v-model="state.password" placeholder="••••••••" :type="show ? 'text' : 'password'"
          :ui="{ trailing: 'pe-1' }">
          <template #trailing>
            <UButton color="neutral" variant="link" size="sm" :icon="show ? 'i-lucide-eye-off' : 'i-lucide-eye'"
              :aria-label="show ? 'Hide password' : 'Show password'" :aria-pressed="show" aria-controls="password"
              @click="show = !show" />
          </template>
        </UInput>
      </UFormField>

      <div class="flex justify-between" style="padding-top:24px; padding-bottom: 24px;">
        <div class="flex items-center gap-2">
          <UCheckbox style=" " v-model="remeberMe" />
          <span class="text-sm text-gray-600">Remember me</span>
        </div>
        <ULink to="/forgot-password" class="text-sm text-blue-600 hover:underline">Forgot Password?</ULink>
      </div>

      <div class="flex justify-center">
        <UButton type="submit" class="submit-button">
          Submit
        </UButton>
      </div>

      <div class="flex justify-center" style="padding-top: 24px;">
        <span class="text-sm text-gray-600">Don't have an account? </span>
        <ULink to="/register" class="text-sm text-blue-600 hover:underline" style="padding-left: 4px;">Sign Up</ULink>
      </div>
    </UForm>



  </div>
</template>


<script setup lang="ts">
import * as v from 'valibot'
import type { FormSubmitEvent } from '@nuxt/ui'

const show = ref(false)
const remeberMe = ref(false)


const schema = v.object({
  email: v.pipe(v.string(), v.email('Invalid email')),
  password: v.pipe(v.string(),
    v.regex(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/, 'Must contain uppercase, lowercase, number, and special character'),
    v.minLength(8, 'Must be at least 8 characters')
  )
})

type Schema = v.InferOutput<typeof schema>

const state = reactive({
  email: '',
  password: ''
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
  height: 100vh;
  gap: 16px;
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
  font-size: 21px;
  font-family: Verdana, Geneva, Tahoma, sans-serif;
  color: #7176a1;
}

.form-class :deep(input) {
  font-size: 16px;
  font-family: Verdana, Geneva, Tahoma, sans-serif;
  color: #2d3153;
  width: 450px;
  font-weight: 560;
  font-size: 17px;
  padding: 8px;
  border: 1px solid #d1d5db;
}


.logo-wrapper {
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

.logo-txt {
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