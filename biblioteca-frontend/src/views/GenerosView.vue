<template>
  <div class="grid gap-6">
    <UiCard :title="form.id ? 'Editar Gênero' : 'Cadastrar Gênero'">
      <form class="grid md:grid-cols-2 gap-4" @submit.prevent="save">
        <div class="md:col-span-2">
          <UiInput label="Nome" v-model="form.nome" placeholder="Ex: Programação" />
          <p v-if="errors.nome" class="text-red-600 text-xs mt-1">{{ errors.nome }}</p>
        </div>

        <div class="md:col-span-2">
          <UiInput label="Descrição" v-model="form.descricao" placeholder="Opcional" />
          <div class="flex justify-between">
            <p v-if="errors.descricao" class="text-red-600 text-xs mt-1">{{ errors.descricao }}</p>
            <p class="text-xs text-slate-500 mt-1 ml-auto">{{ (form.descricao?.length || 0) }}/2000</p>
          </div>
        </div>

        <div
          v-if="generalError"
          class="md:col-span-2 bg-red-50 border border-red-200 text-red-700 rounded-lg px-3 py-2"
        >
          {{ generalError }}
        </div>

        <div class="md:col-span-2">
          <UiButton type="submit" :disabled="submitting">
            {{ submitting ? 'Salvando...' : 'Salvar' }}
          </UiButton>
        </div>
      </form>
    </UiCard>

    <UiCard title="Gêneros">
      <ul class="divide-y">
        <li v-for="g in generos" :key="g.id" class="py-3 flex items-center justify-between">
          <div>
            <div class="font-medium">{{ g.nome }}</div>
            <div class="text-sm text-slate-500">{{ g.descricao ?? '' }}</div>
          </div>
          <div class="text-sm">
            <button class="text-brand-600 hover:underline mr-3" @click="edit(g)">Editar</button>
            <button class="text-red-600 hover:underline" @click="remove(g.id)">Excluir</button>
          </div>
        </li>
      </ul>
      <div v-if="!generos.length" class="text-gray-500 mt-3">Nenhum gênero encontrado.</div>
    </UiCard>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import UiCard from '../components/ui/UiCard.vue'
import UiInput from '../components/ui/UiInput.vue'
import UiButton from '../components/ui/UiButton.vue'
import api, { unwrap } from '../services/api'

type GeneroDTO = { id?: number; nome: string; descricao?: string }

const generos = ref<GeneroDTO[]>([])
const form = reactive<GeneroDTO>({ nome: '', descricao: '' })
const errors = reactive<Record<string, string>>({})
const generalError = ref('')
const submitting = ref(false)

function asArray<T>(v: any): T[] {
  if (Array.isArray(v)) return v
  if (Array.isArray(v?.data)) return v.data
  if (Array.isArray(v?.content)) return v.content
  if (Array.isArray(v?.data?.data)) return v.data.data
  if (Array.isArray(v?.data?.content)) return v.data.content
  return []
}

async function load() {
  try {
    const resp = await api.get('/generos')
    const payload = unwrap<any>(resp)
    generos.value = asArray<GeneroDTO>(payload)
  } catch (e: any) {
    generalError.value = e?.response?.data?.message || 'Falha ao carregar gêneros.'
  }
}

function clearErrors() {
  generalError.value = ''
  Object.keys(errors).forEach(k => delete errors[k])
}

function validate(): boolean {
  clearErrors()
  let ok = true

  if (!form.nome || !form.nome.trim()) {
    errors.nome = 'O nome é obrigatório.'
    ok = false
  } else if (form.nome.trim().length < 3) {
    errors.nome = 'O nome deve ter pelo menos 3 caracteres.'
    ok = false
  }

  if (form.descricao && form.descricao.length > 2000) {
    errors.descricao = 'A descrição deve ter no máximo 2000 caracteres.'
    ok = false
  }

  return ok
}

async function save() {
  if (!validate()) return
  submitting.value = true

  try {
    const payload: GeneroDTO = {
      id: form.id,
      nome: form.nome.trim(),
      descricao: form.descricao?.trim() || ''
    }

    if (form.id) {
      await api.put(`/generos/${form.id}`, payload)
    } else {
      await api.post('/generos', payload)
    }

    await load()
    reset()
  } catch (e: any) {
    generalError.value = e?.response?.data?.message || 'Falha ao salvar gênero.'
  } finally {
    submitting.value = false
  }
}

function edit(g: GeneroDTO) {
  clearErrors()
  Object.assign(form, g)
}

async function remove(id?: number) {
  if (!id) return
  if (!confirm('Tem certeza que deseja excluir este gênero?')) return
  try {
    await api.delete(`/generos/${id}`)
    await load()
  } catch (e: any) {
    generalError.value = e?.response?.data?.message || 'Falha ao excluir gênero.'
  }
}

function reset() {
  clearErrors()
  Object.assign(form, { id: undefined, nome: '', descricao: '' })
}

onMounted(load)
</script>
