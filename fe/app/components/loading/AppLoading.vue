<script setup lang="ts">
withDefaults(defineProps<{
  label?: string
  fullscreen?: boolean
}>(), {
  label: 'Loading...',
  fullscreen: false
})
</script>

<template>
  <div
    class="app-loading"
    :class="{ 'app-loading--fullscreen': fullscreen }"
    role="status"
    aria-live="polite"
  >
    <span class="app-loading__spinner" />
    <p v-if="label" class="app-loading__label">{{ label }}</p>
  </div>
</template>

<style scoped>
.app-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 48px 0;
}

.app-loading--fullscreen {
  position: fixed;
  inset: 0;
  z-index: 50;
  padding: 0;
  background: color-mix(in srgb, var(--ui-bg, #fff) 80%, transparent);
  backdrop-filter: blur(4px);
}

.app-loading__spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--ui-border, #e5e7eb);
  border-top-color: var(--ui-primary, #00dc82);
  border-radius: 50%;
  animation: app-loading-spin 0.8s linear infinite;
}

.app-loading__label {
  margin: 0;
  font-size: 14px;
  color: var(--ui-text-muted, #6b7280);
}

@keyframes app-loading-spin {
  to { transform: rotate(360deg); }
}
</style>