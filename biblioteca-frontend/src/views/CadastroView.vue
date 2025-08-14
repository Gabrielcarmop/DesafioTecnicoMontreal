<template>
  <div class="min-h-screen flex items-center justify-center bg-[#3984b6]">
    <div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-sm">
      <h1 class="text-2xl font-bold mb-6 text-center text-[#3984b6]">Cadastro</h1>

      <form @submit.prevent="onSubmit" novalidate>
        <UiInput label="Usuário" v-model="form.username" placeholder="Digite seu usuário" />
        <UiInput label="Senha" type="password" v-model="form.password" placeholder="Digite sua senha" />
        <UiInput label="Confirmar Senha" type="password" v-model="form.confirmPassword" placeholder="Confirme sua senha" />

        <UiButton type="submit" class="w-full mt-4" :disabled="loading">
          {{ loading ? "Enviando..." : "Cadastrar" }}
        </UiButton>
      </form>

      <p class="mt-4 text-center text-sm text-gray-600">
        Já tem conta?
        <RouterLink
          to="/login"
          class="text-[#3984b6] hover:underline font-semibold"
        >
          Faça login aqui
        </RouterLink>
      </p>

      <p v-if="error" class="error text-red-600 mt-2 text-center">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import UiInput from "../components/ui/UiInput.vue";
import UiButton from "../components/ui/UiButton.vue";
import { register, login, type RegisterPayload } from "../services/api";

const router = useRouter();

const form = reactive<RegisterPayload>({
  username: "",
  password: "",
  confirmPassword: "",
  roles: ["ESCRITA", "LEITURA"], 
});

const loading = ref(false);
const error = ref("");

function validate() {
  error.value = "";
  if (!form.username || !form.password || !form.confirmPassword) {
    error.value = "Preencha todos os campos.";
    return false;
  }
  if (form.password.length < 6) {
    error.value = "A senha deve ter no mínimo 6 caracteres.";
    return false;
  }
  if (form.password !== form.confirmPassword) {
    error.value = "As senhas não conferem.";
    return false;
  }
  return true;
}

async function onSubmit() {
  if (!validate()) return;

  loading.value = true;
  error.value = "";

  try {
    await register(form);
    const { token } = await login(form.username, form.password);
    if (token) {
      router.push("/livros");
    }
  } catch (e: any) {
    error.value = e?.response?.data?.message || "Falha ao cadastrar.";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.error {
  color: #b00020;
}
</style>
