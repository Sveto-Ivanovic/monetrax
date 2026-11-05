export default defineNuxtRouteMiddleware(async (to) => {
  const userStore = useUserStore()
  try {
    await getUserInfo();
  }
  catch {
    return navigateTo('/login')
  }


})