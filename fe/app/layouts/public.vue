<script setup lang="ts">
import type { NavigationMenuItem } from '@nuxt/ui'

const items = ref<NavigationMenuItem[]>([
  {
    label: 'Home',
    icon: 'i-lucide-home',
    to: '/'
    
  },
  {
    label: 'About',
    icon: 'i-lucide-info',
    to: '/about'
  },
 {
    label: 'GitHub',
    icon: 'i-simple-icons-github',
    to: 'https://github.com/Sveto-Ivanovic/monetrax',
    target: '_blank'
  },
  {
    label: 'Login',
    icon: 'i-lucide-info',
    to: '/login'
  }
])
</script>


<template>
  <div class="page-wrapper">
   
    <header class="navigation-header">
      <UNavigationMenu
        :items="items"
        class="navigation-menu"
      />
    </header>

    <main class="page-main">
      <slot />
    </main>

  </div>
</template>



<style scoped>
.page-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}

.navigation-header {
  position: fixed;
  left: 0;
  right: 0;
  top: 16px;
  z-index: 50;

  display: flex;
  justify-content: center;

  padding-left: 1rem;
  padding-right: 1rem;
}

.navigation-menu {
  border-radius: 16px;
  border: 1px solid rgb(255 255 255 / 70%);
  background-color: rgb(255 255 255 / 75%);

  padding: 0.5rem 0.75rem;

  box-shadow:
    0 10px 15px -3px rgb(15 23 42 / 5%),
    0 4px 6px -4px rgb(15 23 42 / 5%);

  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
}

.page-main {
  min-height: 100vh;
  padding-top: 6rem;
}
</style>