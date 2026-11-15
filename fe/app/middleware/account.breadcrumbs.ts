export default defineNuxtRouteMiddleware((to) => {
  const id = to.params.accountId as string | undefined
  if (!id) return
  const labels = useBreadcrumbs()
  labels.value[id] = { label: 'pepe', icon: 'i-lucide-user' }
})