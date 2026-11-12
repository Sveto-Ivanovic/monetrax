<script setup lang="ts">
import type { DropdownMenuItem, NavigationMenuItem } from '@nuxt/ui'
import { useUserStore } from '~/store/user'
import { useWindowSize } from '@vueuse/core'
import type { BreadcrumbItem } from '@nuxt/ui'

const { width } = useWindowSize()
const userStore = useUserStore()
const sidebarTheme = [
    'bg-[#0b2a43] border-white/10',
    '[--ui-text-highlighted:#ffffff]',
    '[--ui-text:var(--color-slate-100)]',
    '[--ui-text-toned:var(--color-slate-200)]',
    '[--ui-text-muted:var(--color-slate-300)]',
    '[--ui-text-dimmed:var(--color-slate-400)]',
    '[--ui-bg-elevated:rgb(255_255_255/0.14)]',
    '[--ui-border:rgb(255_255_255/0.1)]',
].join(' ')

const open = ref(true)
function logoutFunction() {
    console.log('Logout function called')
}
function getItems(state: 'collapsed' | 'expanded') {
    return [
        [
            { label: 'Dashboard', icon: 'i-lucide-layout-dashboard', to: '/dashboard' },
            { label: 'Accounts', icon: 'i-lucide-wallet', to: '/accounts' },
            { label: 'Categories', icon: 'i-lucide-tags', to: '/categories' },
            { label: 'Alerts', icon: 'i-lucide-bell', to: '/alerts' },
        ]
    ] satisfies NavigationMenuItem[][]
}

const userInformation = computed(() => {

    return {
        name: userStore.userInfo?.userInfo.name || 'Guest',
        email: userStore.userInfo?.userInfo.userEmail || 'Guest'
    }

})

const userItems = computed<DropdownMenuItem[][]>(() => [
    [
        {
            label: 'Profile',
            icon: 'i-lucide-user',
            to: '/profile'
        },

    ],
    [
        {
            label: 'Log out',
            icon: 'i-lucide-log-out',
            onSelect: logoutFunction,
            to: '/login'
        }
    ]
])

const route = useRoute();

const labels: Record<string, { label: string; icon: string }> = {
    dashboard: { label: 'Dashboard', icon: 'i-lucide-layout-dashboard' },
    accounts: { label: 'Accounts', icon: 'i-lucide-wallet' },
    categories: { label: 'Categories', icon: 'i-lucide-tags' },
    alerts: { label: 'Alerts', icon: 'i-lucide-bell' },
    profile: { label: 'Profile', icon: 'i-lucide-user' },
}

const items = computed<BreadcrumbItem[]>(() => {
    console.log('Current route path:', route.path)
    const segments = route.path.split('/').filter(x => x !== '')

    return segments.map((segment, i) => ({
        label: labels[segment]?.label ?? segment,
        icon: labels[segment]?.icon,
        to: '/' + segments.slice(0, i + 1).join('/'),
    }
    ))
})

const itemsSelect = ['UTC', ...Intl.supportedValuesOf('timeZone')]

const selectedTimeZone = computed({
    get: () => userStore.getSelectedTimeZone(),
    set: (tz: string) => userStore.setSelectedTimeZone(tz),
})

</script>

<template>
    <div class="flex flex-1" style="justify-content: center;">

        <USidebar v-model:open="open" collapsible="icon" rail :ui="{
            container: 'h-full border-r border-white/10',
            inner: `divide-transparent ${sidebarTheme}`,
            body: 'py-0',
        }" :menu="{
            ui: { content: sidebarTheme }
        }">
            <template #header>
                <div>
                    <div class="register-wrapper">

                        <span class="brand-mark" aria-hidden="true">
                            <span></span>
                            <span></span>
                            <span></span>
                        </span>

                        <span class="register-txt"> Monetrax </span>
                    </div>
                </div>
            </template>

            <template #default="{ state }">
                <UNavigationMenu :key="state" :items="getItems(state)" orientation="vertical" color="neutral"
                    variant="pill" :ui="{
                        root: 'px-0 py-4',
                        list: 'flex flex-col gap-1',
                        link: 'px-1 py-3 rounded-lg text-sm'
                    }" />
            </template>

            <template #footer>
                <UDropdownMenu :items="userItems" :content="{ align: 'center', collisionPadding: 12 }" :ui="{
                    content: 'w-(--reka-dropdown-menu-trigger-width) min-w-48',
                    viewport: [
                        'bg-[#97c9ff]',
                    ].join(' '),
                    itemLeadingIcon: 'bg-[var(--ui-text)]',

                }

                    ">
                    <UButton v-bind="userInformation" :label="userInformation.name"
                        :avatar="{ alt: userInformation.name }" trailing-icon="i-lucide-chevrons-up-down"
                        color="neutral" variant="ghost" square
                        class="w-full data-[state=open]:bg-elevated overflow-hidden" :ui="{
                            trailingIcon: 'text-dimmed ms-auto'
                        }" />
                </UDropdownMenu>
            </template>
        </USidebar>

        <div class="flex-1 flex flex-col">
            <div class="h-(--ui-header-height) shrink-0 flex items-center px-4 border-default">
                <UButton icon="i-lucide-panel-left" color="neutral" variant="ghost" aria-label="Toggle sidebar"
                    @click="open = !open" />
            </div>

            <div class="flex-1 p-4">
                <Placeholder class="size-full" />
            </div>
        </div>

        <div class="page-main-wrapper">
            <div class="page-header">
                <UBreadcrumb :items="items" :ui="{
                    link: [
                        'text-base font-medium text-slate-500 hover:text-slate-900',
                        'aria-[current=page]:text-[#0b2a43] aria-[current=page]:font-semibold',
                    ].join(' '),
                    linkLeadingIcon: [
                        'size-5 text-slate-500',
                        'group-aria-[current=page]:text-[#0b2a43]',
                    ].join(' '),
                }" />

                <div class="page-header-actions">
                    <USelectMenu v-model="selectedTimeZone" :items="itemsSelect" icon="i-lucide-globe" size="lg"
                        :search-input="{
                            placeholder: 'Search timezone...',
                            icon: 'i-lucide-search',
                            ui: {
                                base: 'text-white placeholder:text-slate-400',
                                leadingIcon: 'text-[#a8ead2]',
                            },
                        }" variant="none" class="w-64" :ui="{
                            base: [
                                'bg-[#0b2a43] text-white rounded-lg color-white',
                                'ring ring-inset ring-white/15',
                                'hover:bg-[#12395a]',
                                'focus-visible:ring-2 focus-visible:ring-[#4dd8ff]',


                            ].join(' '),
                            leadingIcon: 'text-[#a8ead2]',
                            trailingIcon: 'text-white/60',
                            placeholder: 'text-slate-400',
                            content: 'bg-[#0b2a43] text-white ring ring-white/10 rounded-lg shadow-lg',
                            input: 'text-white placeholder:text-slate-400',
                            item: [
                                'text-slate-200 rounded-md',
                                'data-highlighted:before:bg-white/10',
                                'data-[state=checked]:text-[#a8ead2]',
                            ].join(' '),
                            itemTrailingIcon: 'text-[#a8ead2]',
                        }" />
                </div>
            </div>
            <main class="page-main">
                <slot />
            </main>
        </div>
    </div>
</template>

<style scoped>
.register-wrapper {
    gap: 16px;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    user-select: none;
}


.brand-mark {
    width: 30px;
    height: 30px;
    display: flex;
    align-items: end;
    justify-content: center;
    gap: 4px;
    padding: 6px;
    background: #a8ead2;
    border-radius: 8px;
}

.brand-mark span {
    display: block;
    width: 5px;
    border-radius: 3px;
    background: #050a47;
}

.brand-mark span:nth-child(1) {
    height: 10px;
}

.brand-mark span:nth-child(2) {
    height: 14px;
}

.brand-mark span:nth-child(3) {
    height: 12px;
}

.register-txt {
    font-family: 'Montserrat', sans-serif;
    font-weight: 700;
    background: linear-gradient(90deg, #ffffff 0%, #a8ead2 55%, #4dd8ff 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    color: transparent;
    font-size: 37px;
    letter-spacing: -0.8px;
}

.page-main-wrapper {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    width: 100%;
    box-sizing: border-box;
    justify-content: center;
}

.page-main {
    width: 90%;
    min-height: 100vh;
    box-sizing: border-box;
    padding-top: 70px;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 0;
    width: 90%;

}

@media (max-width: 700px) {
    .page-header{
        flex-direction: column;
        justify-content: center;
        gap: 16px;
    }
}
</style>