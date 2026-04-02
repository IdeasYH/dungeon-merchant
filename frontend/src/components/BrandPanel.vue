<script setup>
defineProps({
  eyebrow: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  lead: {
    type: String,
    required: true,
  },
  quote: {
    type: String,
    required: true,
  },
  leftStatLabel: {
    type: String,
    required: true,
  },
  leftStatValue: {
    type: String,
    required: true,
  },
  rightStatLabel: {
    type: String,
    required: true,
  },
  rightStatValue: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <aside class="brand-panel stagger">
    <p class="brand-panel__eyebrow">{{ eyebrow }}</p>
    <h2 class="brand-panel__title">{{ title }}</h2>
    <p class="brand-panel__lead">{{ lead }}</p>
    <p class="brand-panel__quote">{{ quote }}</p>

    <div class="brand-panel__meta">
      <div class="brand-panel__stat">
        <span class="brand-panel__stat-label">{{ leftStatLabel }}</span>
        <span class="brand-panel__stat-value">{{ leftStatValue }}</span>
      </div>

      <div class="brand-panel__stat">
        <span class="brand-panel__stat-label">{{ rightStatLabel }}</span>
        <span class="brand-panel__stat-value">{{ rightStatValue }}</span>
      </div>
    </div>
  </aside>
</template>
