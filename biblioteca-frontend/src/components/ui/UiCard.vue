<template>
  <div class="card p-5">
    <div v-if="title" class="mb-3 text-lg font-semibold">{{ title }}</div>
    <slot />
  </div>
</template>
<script setup lang="ts">
defineProps<{ title?: string }>()
</script>
