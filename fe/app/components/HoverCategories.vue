<script setup lang="ts">
import { stringToColors } from '~/utilis/formaters/colorGetter';

const props = defineProps<{
  categories: string[]
}>()

const items = computed(() =>
  props.categories.map(name => ({ name, ...stringToColors(name) }))
)
</script>

<template>
  <div class="categories-wrapper">
    <span
      v-for="item in items"
      :key="item.name"
      class="category"
      :style="{
        '--category-light': item.light,
        '--category-dark': item.dark,
      }"
    >
      {{ item.name }}
    </span>
  </div>
</template>

<style scoped>
.categories-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 0.375rem;
  background: transparent;
}

.category {
  padding: 0.123rem 0.63rem;
  border: 1px solid var(--category-dark);
  border-radius: 9999px;
  background-color: var(--category-light);
  color: var(--category-dark);
  font-size: 0.75rem;
  font-weight: 500;
  line-height: 1.25rem;
}



</style>