export default defineNuxtRouteMiddleware((to) => {
  const userStore = useUserStore()
  const publicRoutes = ['/profile', '/account-transactions']


  if (!userStore.isAuthenticated && !publicRoutes.includes(to.path)) {
    // call the endpoint for fetching user here, we need user information about either profile or if we have functional api key so we know if we can make a transaction
  }
})