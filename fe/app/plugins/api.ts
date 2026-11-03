import { useUserStore } from '../store/user'
import type { CommonErrorResponse, LogInResponse } from "~/services/usermanagement/types";

export default defineNuxtPlugin((nuxtApp) => {
  const config = useRuntimeConfig()
  const userStore = useUserStore()
  const baseURL = config.public.baseUrl as string

  let refreshPromise: Promise<string | null> | null = null;

  function refreshToken(): Promise<string | null> {
  if (!refreshPromise) {
    refreshPromise = $fetch<LogInResponse | CommonErrorResponse>('/auth/token/refresh', {
      baseURL,
      method: 'POST',
      body: {},
      credentials: 'include',
    })
      .then((res) => {
        if ('authToken' in res) {
          userStore.initUser(res)
          return res.authToken
        }
        return null
      })
      .catch((error: any) => {
        const statusCode = error.statusCode || error.response?.status
        console.error(`Refresh failed [${statusCode}]:`, error.data || error.message)
        return null
      })
      .finally(() => {
        refreshPromise = null
      })
  }
  return refreshPromise
}


  async function redirectToLogin() {
    userStore.logOutUser()
    await nuxtApp.runWithContext(() => navigateTo('/login'))
  }


  const $customFetch = $fetch.create({
    baseURL: config.public.baseUrl as string,
    retryStatusCodes: [401],
    retry: 1,

    async onRequest({ options }) {

      if (refreshPromise) {
        const token = await refreshPromise
        if (token == null) {
          console.log("Failed to refresh!")
          await redirectToLogin()
          throw new Error('Failed to refresh!')
        }
        options.headers.set('Authorization', `Bearer ${token}`)
        return
      }

      if (userStore.isAuthenticated) {
        options.headers.set('Authorization', `Bearer ${userStore.accessToken}`)
        return
      } else {
        await redirectToLogin()
        throw new Error('You need access token to call the endpoints.')
      }

    },
    onResponse({ response }) {

    },
    async onResponseError({ response, options, request }) {
      if (response.status !== 401) return

      if (!options.retry) return



      const oldToken = options.headers.get('Authorization')
      if (oldToken && oldToken === `Bearer ${userStore.accessToken}`) {
        const newToken = await refreshToken()

        if (newToken == null) {
          console.log("Failed to refresh!")
          await redirectToLogin()
          throw new Error('Failed to refresh!')
        }

        return
      }

    }
  })

  return { provide: { customFetch: $customFetch } }
})