<script setup lang="ts">
import type { AlertConditionInformation } from '~/services/alerts/types';


const props = defineProps<{
  condition: AlertConditionInformation
  amount: number
}>()

const low = computed(() => props.condition.limitValueLowOrEqual ?? 0)
const high = computed(() => props.condition.limitValueHigh ?? low.value)

const max = computed(() => Math.max(props.amount, low.value, high.value) * 1.25 || 1)
const pct = (v: number) => Math.min(100, Math.max(0, (v / max.value) * 100))

// Zone start/end in %
const zone = computed(() => {
  switch (props.condition.ruleType) {
    case 'LESS':
    case 'LESS_OR_EQUAL':
      return { start: 0, end: pct(low.value) }
    case 'GREATER':
    case 'GREATER_OR_EQUAL':
      return { start: pct(low.value), end: 100 }
    case 'BETWEEN':
      return { start: pct(low.value), end: pct(high.value) }
    case 'EQUAL':
      return { start: pct(low.value) - 0.5, end: pct(low.value) + 0.5 }
  }
})

const inZone = computed(() => {
  const a = props.amount
  switch (props.condition.ruleType) {
    case 'LESS': return a < low.value
    case 'LESS_OR_EQUAL': return a <= low.value
    case 'GREATER': return a > low.value
    case 'GREATER_OR_EQUAL': return a >= low.value
    case 'BETWEEN': return a >= low.value && a <= high.value
    case 'EQUAL': return a === low.value
  }
})

const isEqual = computed(() => props.condition.ruleType === 'EQUAL')
</script>

<template>
  <div class="condition-bar">
    <div class="condition-bar__header">
      <span class="condition-bar__name">{{ condition.categoryName }}</span>
      <span
        class="condition-bar__amount"
        :class="inZone ? 'is-ok' : 'is-breached'"
      >
        {{ amount }}
      </span>
    </div>

    <div class="condition-bar__track">
      <!-- target zone -->
      <div
        class="condition-bar__zone"
        :class="{ 'condition-bar__zone--line': isEqual }"
        :style="{
          left: zone.start + '%',
          width: zone.end - zone.start + '%',
        }"
      />
      <!-- current value marker -->
      <div
        class="condition-bar__marker"
        :class="inZone ? 'is-ok' : 'is-breached'"
        :style="{ left: `calc(${pct(amount)}% - 2px)` }"
      />
    </div>

    <div class="condition-bar__scale">
      <span>0</span>
      <span>{{ Math.round(max) }}</span>
    </div>
  </div>
</template>

<style scoped>
.condition-bar {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.condition-bar__header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  font-size: 0.875rem;
}

.condition-bar__name {
  color: var(--ui-text-highlighted);
}

.condition-bar__amount {
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

.condition-bar__track {
  position: relative;
  height: 12px;
  border-radius: 9999px;
  background: var(--ui-bg-elevated);
}

.condition-bar__zone {
  position: absolute;
  top: 0;
  bottom: 0;
  border-radius: 9999px;
  background: color-mix(in srgb, var(--ui-primary) 30%, transparent);
  border: 1px solid color-mix(in srgb, var(--ui-primary) 50%, transparent);
  box-sizing: border-box;
}

/* EQUAL: thin solid target line instead of a band */
.condition-bar__zone--line {
  top: -2px;
  bottom: -2px;
  min-width: 3px;
  border: none;
  background: var(--ui-primary);
}

.condition-bar__marker {
  position: absolute;
  top: -4px;
  width: 4px;
  height: 20px;
  border-radius: 2px;
  transition: left 0.3s ease;
}

.condition-bar__scale {
  display: flex;
  justify-content: space-between;
  font-size: 0.75rem;
  color: var(--ui-text-muted);
}

/* state colors */
.condition-bar__amount.is-ok {
  color: var(--ui-success);
}
.condition-bar__amount.is-breached {
  color: var(--ui-error);
}
.condition-bar__marker.is-ok {
  background: var(--ui-success);
}
.condition-bar__marker.is-breached {
  background: var(--ui-error);
}
</style>