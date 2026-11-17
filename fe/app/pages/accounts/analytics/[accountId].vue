<script setup lang="ts">

const route = useRoute()
</script>

<template>
  <div>
    {{ route.params.accountId }}

  </div>
</template>

<style scoped>



</style>