<template>
    <div>
        Home Page
    </div>
</template>


<script setup lang="ts">

declare module 'nuxt/app' {
  interface NuxtLayouts {
    'public': unknown
  }
}


definePageMeta({
  layout: 'public',
})

</script>