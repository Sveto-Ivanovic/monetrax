export default defineNuxtRouteMiddleware((to) => {
  const userStore = useUserStore()
  const publicRoutes = ['/login', '/register', '/', '/about']



  if (!userStore.isAuthenticated && !publicRoutes.includes(to.path)) {
    return navigateTo('/login' )
  }
})