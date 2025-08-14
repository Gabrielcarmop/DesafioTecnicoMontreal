<template>
  <div class="flex justify-center w-full">
    <button
      :type="type"
      class="inline-flex items-center justify-center rounded-xl px-6 py-2 font-medium transition
             bg-[#3984b6] text-white hover:bg-[#2f6d97] focus:ring-2 focus:ring-sky-300
             disabled:bg-sky-300 disabled:text-slate-100 disabled:cursor-not-allowed"
      :disabled="disabled"
    >
      <slot />
    </button>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    type?: 'button' | 'submit' | 'reset'
    disabled?: boolean
  }>(),
  { type: 'button', disabled: false }
)
</script>
