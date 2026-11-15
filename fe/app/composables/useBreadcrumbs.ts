export const useBreadcrumbs = () => {

    return useState<Record<string, { label: string; icon: string }>>('breadcrumbs', () => {
        return {
            dashboard: { label: 'Dashboard', icon: 'i-lucide-layout-dashboard' },
            accounts: { label: 'Accounts', icon: 'i-lucide-wallet' },
            categories: { label: 'Categories', icon: 'i-lucide-tags' },
            alerts: { label: 'Alerts', icon: 'i-lucide-bell' },
            profile: { label: 'Profile', icon: 'i-lucide-user' },
        }
    })

}