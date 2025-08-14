<template>
  <div class="grid gap-6">
    <UiCard :title="form.id ? 'Editar Autor' : 'Cadastrar Autor'">
      <form class="grid md:grid-cols-2 gap-4" @submit.prevent="save">

        <div class="md:col-span-2">
          <UiInput label="Nome" v-model="form.nome" placeholder="Ex: Robert C. Martin" />
          <p v-if="errors.nome" class="text-red-600 text-xs mt-1">{{ errors.nome }}</p>
        </div>


        <div>
          <UiInput
            label="Data de Nascimento (YYYY-MM-DD)"
            v-model="form.dataNascimento"
            placeholder="1962-08-05"
          />
          <p v-if="errors.dataNascimento" class="text-red-600 text-xs mt-1">{{ errors.dataNascimento }}</p>
        </div>

        <div class="md:col-span-2">
          <label class="text-sm text-slate-600">Biografia</label>
          <textarea
            v-model="form.biografia"
            class="w-full rounded-xl border border-slate-200 px-3 py-2"
            rows="4"
            maxlength="2000"
          ></textarea>
          <div class="flex justify-between">
            <p v-if="errors.biografia" class="text-red-600 text-xs mt-1">{{ errors.biografia }}</p>
            <p class="text-xs text-slate-500 mt-1 ml-auto">
              {{ (form.biografia?.length || 0) }}/2000
            </p>
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

    <UiCard title="Autores">
      <ul class="divide-y">
        <li v-for="a in autores" :key="a.id" class="py-3 flex items-center justify-between">
          <div>
            <div class="font-medium">{{ a.nome }}</div>
            <div class="text-sm text-slate-500">{{ formatDate(a.dataNascimento) }}</div>
          </div>
          <div class="text-sm">
            <button class="text-brand-600 hover:underline mr-3" @click="edit(a)">Editar</button>
            <button class="text-red-600 hover:underline" @click="remove(a.id)">Excluir</button>
          </div>
        </li>
      </ul>
      <div v-if="!autores.length" class="text-gray-500 mt-3">Nenhum autor encontrado.</div>
    </UiCard>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import UiCard from '../components/ui/UiCard.vue'
import UiInput from '../components/ui/UiInput.vue'
import UiButton from '../components/ui/UiButton.vue'
import api, { unwrap } from '../services/api'

type AutorDTO = {
  id?: number
  nome: string
  biografia?: string
  dataNascimento?: string | null 
}

const autores = ref<AutorDTO[]>([])
const form = reactive<AutorDTO>({ nome: '', biografia: '', dataNascimento: '' })
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
    const resp = await api.get('/autores')
    const payload = unwrap<any>(resp)
    autores.value = asArray<AutorDTO>(payload)
  } catch (e: any) {
    generalError.value = e?.response?.data?.message || 'Falha ao carregar autores.'
  }
}

function clearErrors() {
  generalError.value = ''
  Object.keys(errors).forEach(k => delete errors[k])
}

function isValidDateYYYYMMDD(str: string): boolean {

  const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(str)
  if (!m) return false
  const y = Number(m[1]), mo = Number(m[2]), d = Number(m[3])
  const dt = new Date(Date.UTC(y, mo - 1, d))

  if (dt.getUTCFullYear() !== y || dt.getUTCMonth() + 1 !== mo || dt.getUTCDate() !== d) return false

  const today = new Date()
  const todayUTC = Date.UTC(today.getFullYear(), today.getMonth(), today.getDate())
  return dt.getTime() <= todayUTC && y >= 1850 
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

  if (form.dataNascimento && form.dataNascimento.trim()) {
    const onlyDate = form.dataNascimento.slice(0, 10) 
    if (!isValidDateYYYYMMDD(onlyDate)) {
      errors.dataNascimento = 'Informe uma data válida no formato YYYY-MM-DD (não futura).'
      ok = false
    } else {
      form.dataNascimento = onlyDate
    }
  }

  if (form.biografia && form.biografia.length > 2000) {
    errors.biografia = 'A biografia deve ter no máximo 2000 caracteres.'
    ok = false
  }

  return ok
}

async function save() {
  if (!validate()) return
  submitting.value = true
  try {
    const payload: AutorDTO = {
      id: form.id,
      nome: form.nome.trim(),
      biografia: form.biografia?.trim() || '',
      dataNascimento: form.dataNascimento || null,
    }

    if (form.id) {
      await api.put(`/autores/${form.id}`, payload)
    } else {
      await api.post('/autores', payload)
    }

    await load()
    reset()
  } catch (e: any) {
    generalError.value = e?.response?.data?.message || 'Falha ao salvar autor.'
  } finally {
    submitting.value = false
  }
}

function edit(a: AutorDTO) {
  clearErrors()
  Object.assign(form, {
    id: a.id,
    nome: a.nome,
    biografia: a.biografia || '',
    dataNascimento: a.dataNascimento ? a.dataNascimento.slice(0, 10) : ''
  })
}

async function remove(id?: number) {
  if (!id) return
  if (!confirm('Tem certeza que deseja excluir este autor?')) return
  try {
    await api.delete(`/autores/${id}`)
    await load()
  } catch (e: any) {
    generalError.value = e?.response?.data?.message || 'Falha ao excluir autor.'
  }
}

function formatDate(iso?: string | null) {
  if (!iso) return ''
  return iso.slice(0, 10)
}

function reset() {
  clearErrors()
  Object.assign(form, { id: undefined, nome: '', biografia: '', dataNascimento: '' })
}

onMounted(load)
</script>
