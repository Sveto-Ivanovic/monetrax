import { getUserInfo } from "~/services/usermanagement/middleware.queries";

export default defineNuxtRouteMiddleware(async (to) => {
  try {
    await getUserInfo();
  }
  catch {
    return navigateTo('/login')
  }


})