import { useUserStore } from '../stores/user_store';


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

    async onRequest({ request, options, error }) {

      if (isRefreshing.value) {

        return new Promise((resolve, reject) => {

          subscribeTokenRefresh((token: string | null) => {
            if (token == null) {
              console.log("Failed to refresh!")
              reject("Failed to refresh!")
              return
            }
            options.headers.set('Authorization', `Bearer ${token}`)
            resolve("Successfuly refreshed")
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
    async onResponseError({ response }) {
      if (response.status === 401) {

        if (isRefreshing.value == false) {
          isRefreshing.value = true


          try {
            const res = await $fetch(config.public.baseUrl + "/auth/token/refresh", {
              method: 'POST',
              body: {},
              credentials: 'include'
            })

            const token = res.authToken ?? null

            if (token == null) {
              isRefreshing.value = false
              refreshSubscribers(null)
              userStore.logOutUser()
              await nuxtApp.runWithContext(() => navigateTo('/login'))
              return
            }

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

      }
    },
  })

  return {
    provide: {
      customFetch: $customFetch,
    },
  }





})