<template>
  <div class="min-h-screen">
    <header
      v-if="!$route.meta.hideHeader"
      class="sticky top-0 z-20 bg-white/80 backdrop-blur border-b"
    >
      <div class="container-app py-4 flex items-center gap-4">
        <div
          class="w-10 h-10 rounded-full bg-brand-500 flex items-center justify-center text-white font-bold"
        >
          B
        </div>
        <h1 class="text-xl font-semibold">Biblioteca</h1>
        <nav class="ml-auto flex gap-4">
          <RouterLink class="hover:text-brand-600" to="/livros">Livros</RouterLink>
          <RouterLink class="hover:text-brand-600" to="/autores">Autores</RouterLink>
          <RouterLink class="hover:text-brand-600" to="/generos">Gêneros</RouterLink>
        </nav>
      </div>
    </header>

    <main class="container-app py-8">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
const $route = useRoute();
</script>
