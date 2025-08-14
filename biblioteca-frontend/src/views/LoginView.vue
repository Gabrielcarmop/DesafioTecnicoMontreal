<template>
  <div class="min-h-screen flex items-center justify-center bg-[#3984b6]">
    <div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-sm">
      <h1 class="text-2xl font-bold mb-6 text-center text-[#3984b6]">Login</h1>
      <form @submit.prevent="login">
        <UiInput label="Usuário" v-model="username" placeholder="Digite seu usuário" />
        <UiInput label="Senha" v-model="password" type="password" placeholder="Digite sua senha" />

        <UiButton type="submit" class="w-full mt-4">Entrar</UiButton>
      </form>

      <p class="mt-4 text-center text-sm text-gray-600">
        Não tem conta?
        <RouterLink
          to="/cadastro"
          class="text-[#3984b6] hover:underline font-semibold"
        >
          Cadastre-se aqui
        </RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import UiInput from "../components/ui/UiInput.vue";
import UiButton from "../components/ui/UiButton.vue";
import { login as loginApi } from "../services/api"; 
import router from "../router";

const username = ref("");
const password = ref("");

async function login() {
  try {
    const res = await loginApi(username.value, password.value);
    if (res?.token) {
      router.push("/livros");
    }
  } catch (e) {
    alert("Usuário ou senha inválidos");
  }
}
</script>
