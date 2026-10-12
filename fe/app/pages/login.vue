<template>
    <div>
        Home Page
    </div>
</template>


<script setup lang="ts">

declare module 'nuxt/app' {
  interface NuxtLayouts {
    "publicCustom": unknown
  }
}


definePageMeta({
  layout: 'public-custom',
})

</script>