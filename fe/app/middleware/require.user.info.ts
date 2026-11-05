import { getUserInfo } from "~/services/usermanagement/middleware.queries";
import { useUserStore } from "~/store/user";

export default defineNuxtRouteMiddleware(async (to) => {
  const userStore = useUserStore()
  try {
    await getUserInfo();
  }
  catch {
    return navigateTo('/login')
  }


})