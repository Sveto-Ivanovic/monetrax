<script setup lang="ts">
import type { NavigationMenuItem } from '@nuxt/ui'

const items = ref<NavigationMenuItem[]>([
  {
    label: 'Home',
    icon: 'i-lucide-home',
    to: '/'
    
  },
  {
    label: 'About Us',
    icon: 'i-lucide-info',
    to: '/about'
  }
])
</script>


<template>
    <div ">
    <UNavigationMenu :items="items" />
    <main>
      <slot />
    </main>
  </div>
</template>