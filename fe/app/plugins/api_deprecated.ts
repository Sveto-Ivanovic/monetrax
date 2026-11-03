import type { CommonErrorResponse, LogInResponse } from "~/services/usermanagement/types";
import { useUserStore } from "../store/user";


export default defineNuxtPlugin((nuxtApp) => {

  const config = useRuntimeConfig()

  const userStore = useUserStore()
  const isRefreshing = ref(false)

  let refreshSubs: Array<(token: string | null) => void> = []

  function subscribeTokenRefresh(cb: (token: string | null) => void) {
    refreshSubs.push(cb)
  }

  function refreshSubscribers(token: string | null) {
    refreshSubs.forEach((cb) => cb(token))
    refreshSubs = []
  }


  const $customFetch = $fetch.create({
    baseURL: config.public.baseUrl as string,
    retryStatusCodes: [401],
    retry: 1,

    async onRequest({ options }) {

      if (isRefreshing.value) {

        return new Promise((resolve, reject) => {

          subscribeTokenRefresh((token: string | null) => {
            if (token == null) {
              console.log("Failed to refresh!")
              reject("Failed to refresh!")
              return
            }
            options.headers.set('Authorization', `Bearer ${token}`)
            resolve()
          })

        })
      }

      if (userStore.isAuthenticated) {
        options.headers.set('Authorization', `Bearer ${userStore.accessToken}`)

      } else {
        await nuxtApp.runWithContext(() => navigateTo('/login'))
        throw new Error('You need access token to call the endpoints.')
      }



    },
    onResponse({ response }) {

    },
    async onResponseError({ response, options }) {
      if (response.status === 401) {

        if(options.retry && options.retry>=1){
          console.log("Already retried once, but still got 401. Logging out user.")
          useUserStore().logOutUser()
          await nuxtApp.runWithContext(() => navigateTo('/login'))
          
          return
        }

        if (isRefreshing.value == false) {
          isRefreshing.value = true


          try {
            const res: LogInResponse | CommonErrorResponse = await $fetch(config.public.baseUrl + "/auth/token/refresh", {
              method: 'POST',
              body: {},
              credentials: 'include'
            })

            if ('errors' in res && res.errors.length >= 0) {
              isRefreshing.value = false
              refreshSubscribers(null)
              userStore.logOutUser()
              await nuxtApp.runWithContext(() => navigateTo('/login'))
              return
            }

            let token = null;
            if ('authToken' in res) {
              token = res.authToken;
            }


            if (token == null) {
              isRefreshing.value = false
              refreshSubscribers(null)
              userStore.logOutUser()
              await nuxtApp.runWithContext(() => navigateTo('/login'))
              return
            }

            if('authToken' in res)
              userStore.initUser(res)

            isRefreshing.value = false
            refreshSubscribers(token)


          } catch (error: any) {

            const statusCode = error.statusCode || error.response?.status
            const errorData = error.data
            console.error(`Refresh failed [${statusCode}]:`, errorData || error.message)
            isRefreshing.value = false
            refreshSubscribers(null)
            userStore.logOutUser()
            await nuxtApp.runWithContext(() => navigateTo('/login'))

          }


        }

        // This case is when the refresh is already in progress
        // but somehow this request still got past the request interceptor and got a 401 response.
        
        else{





        }





      }
    },
  })

  return {
    provide: {
      customFetch: $customFetch,
    },
  }





})