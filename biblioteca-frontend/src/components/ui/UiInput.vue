<template>
  <div class="space-y-1">
    <label v-if="label" class="text-sm text-slate-600">{{ label }}</label>
    <input
      :type="type"
      v-model="model"
      :placeholder="placeholder"
      class="w-full rounded-xl border border-slate-200 px-3 py-2 focus:ring-2 focus:ring-brand-300 outline-none"
    />
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  modelValue?: string|number|null
  label?: string
  placeholder?: string
  type?: string
}>(), { type: 'text' })
const emit = defineEmits(['update:modelValue'])
const model = computed({
  get: () => props.modelValue ?? '',
  set: v => emit('update:modelValue', v)
})
</script>
