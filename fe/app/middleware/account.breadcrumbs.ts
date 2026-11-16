import { useUserStore } from "~/store/user"


export default defineNuxtRouteMiddleware((to) => {
  const id = to.params.accountId as string | undefined

   const userStore = useUserStore()

  if (!id) return

  let accountName = "No Account Name Found"
  if(userStore.userInfo){
     userStore.userInfo.accountInformationList.forEach(element => {
    if(element.accountId && element.name && element.accountId === id ){
      accountName = element.name
    }
  });
  }



  const labels = useBreadcrumbs()
  labels.value[id] = { label: accountName, icon: 'i-lucide-credit-card-check' }


})