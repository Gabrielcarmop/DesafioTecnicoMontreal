<template>
  <div class="grid gap-6">
    <UiCard :title="form.id ? 'Editar Livro' : 'Cadastrar Livro'">
      <form class="grid md:grid-cols-2 gap-4" @submit.prevent="save">
        <div>
          <UiInput label="Título" v-model="form.titulo" placeholder="Ex: Clean Code" />
          <p v-if="errors.titulo" class="text-red-600 text-xs mt-1">{{ errors.titulo }}</p>
        </div>

        <div>
          <UiInput label="ISBN" v-model="form.isbn" placeholder="Ex: 9781234567890" />
          <p v-if="errors.isbn" class="text-red-600 text-xs mt-1">{{ errors.isbn }}</p>
        </div>

        <div>
          <UiInput label="Editora" v-model="form.editora" placeholder="Ex: Alta Books" />
          <p v-if="errors.editora" class="text-red-600 text-xs mt-1">{{ errors.editora }}</p>
        </div>

        <div>
          <UiInput
            label="Ano Publicação"
            v-model.number="form.anoPublicacao"
            type="number"
            placeholder="2008"
          />
          <p v-if="errors.anoPublicacao" class="text-red-600 text-xs mt-1">{{ errors.anoPublicacao }}</p>
        </div>

        <div>
          <label class="text-sm text-slate-600">Gênero</label>
          <select v-model.number="form.generoId" class="w-full rounded-xl border border-slate-200 px-3 py-2">
            <option :value="null" disabled>Selecione</option>
            <option v-for="g in generos" :key="g.id" :value="g.id">{{ g.nome }}</option>
          </select>
          <p v-if="errors.generoId" class="text-red-600 text-xs mt-1">{{ errors.generoId }}</p>
        </div>

        <div>
          <label class="text-sm text-slate-600">Autor</label>
          <select v-model.number="form.autorId" class="w-full rounded-xl border border-slate-200 px-3 py-2">
            <option :value="null" disabled>Selecione</option>
            <option v-for="a in autores" :key="a.id" :value="a.id">{{ a.nome }}</option>
          </select>
          <p v-if="errors.autorId" class="text-red-600 text-xs mt-1">{{ errors.autorId }}</p>
        </div>

        <div v-if="generalError" class="md:col-span-2 bg-red-50 border border-red-200 text-red-700 rounded-lg px-3 py-2">
          {{ generalError }}
        </div>

        <div class="md:col-span-2">
          <UiButton type="submit" :disabled="submitting">{{ submitting ? 'Salvando...' : 'Salvar' }}</UiButton>
        </div>
      </form>
    </UiCard>

    <UiCard title="Livros">
      <div class="overflow-x-auto">
        <table class="min-w-full text-sm">
          <thead>
            <tr class="text-left border-b">
              <th class="py-2 pr-4">Título</th>
              <th class="py-2 pr-4">ISBN</th>
              <th class="py-2 pr-4">Editora</th>
              <th class="py-2 pr-4">Ano</th>
              <th class="py-2 pr-4">Autor</th>
              <th class="py-2 pr-4">Gênero</th>
              <th class="py-2 pr-4">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="l in livros" :key="l.id" class="border-b last:border-0">
              <td class="py-2 pr-4">{{ l.titulo }}</td>
              <td class="py-2 pr-4">{{ l.isbn }}</td>
              <td class="py-2 pr-4">{{ l.editora }}</td>
              <td class="py-2 pr-4">{{ l.anoPublicacao ?? '-' }}</td>
              <td class="py-2 pr-4">{{ autorNomeFrom(l) }}</td>
              <td class="py-2 pr-4">{{ generoNomeFrom(l) }}</td>
              <td class="py-2 pr-4">
                <button class="text-brand-600 hover:underline mr-3" @click="edit(l)">Editar</button>
                <button class="text-red-600 hover:underline" @click="remove(l.id)">Excluir</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="!livros.length" class="text-gray-500 mt-4">Nenhum livro encontrado.</div>
      </div>
    </UiCard>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import UiCard from '../components/ui/UiCard.vue'
import UiInput from '../components/ui/UiInput.vue'
import UiButton from '../components/ui/UiButton.vue'
import api, { unwrap } from '../services/api'

type AutorDTO = { id: number; nome: string }
type GeneroDTO = { id: number; nome: string }

type LivroDTO = {
  id?: number
  titulo: string
  isbn: string
  editora: string
  anoPublicacao?: number | null
  autorId?: number | null
  generoId?: number | null
  autor?: AutorDTO | null
  genero?: GeneroDTO | null
}

const livros = ref<LivroDTO[]>([])
const autores = ref<AutorDTO[]>([])
const generos = ref<GeneroDTO[]>([])

const form = reactive<LivroDTO>({
  titulo: '',
  isbn: '',
  editora: '',
  anoPublicacao: null,
  generoId: null,
  autorId: null
})

const errors = reactive<Record<string, string>>({})
const generalError = ref('')
const submitting = ref(false)

function asArray<T>(v: any): T[] {
  if (!v) return []
  if (Array.isArray(v)) return v
  if (Array.isArray(v?.content)) return v.content
  if (Array.isArray(v?.data)) return v.data
  if (Array.isArray(v?.data?.data)) return v.data.data
  if (Array.isArray(v?.data?.content)) return v.data.content
  return []
}

async function loadAll() {
  try {
    const [l, a, g] = await Promise.all([
      api.get('/livros'),
      api.get('/autores'),
      api.get('/generos'),
    ])
    livros.value = asArray<LivroDTO>(unwrap<any>(l))
    autores.value = asArray<AutorDTO>(unwrap<any>(a))
    generos.value = asArray<GeneroDTO>(unwrap<any>(g))
  } catch (e: any) {
    generalError.value = e?.response?.data?.message || 'Falha ao carregar dados.'
  }
}

function autorNomeFrom(l: LivroDTO) {
  if (l.autor?.nome) return l.autor.nome
  const a = autores.value.find(x => x.id === l.autorId)
  return a?.nome ?? '—'
}

function generoNomeFrom(l: LivroDTO) {
  if (l.genero?.nome) return l.genero.nome
  const g = generos.value.find(x => x.id === l.generoId)
  return g?.nome ?? '—'
}

function validate(): boolean {
  clearErrors()
  const nowYear = new Date().getFullYear()
  let ok = true

  if (!form.titulo?.trim()) {
    errors.titulo = 'O título é obrigatório.'
    ok = false
  } else if (form.titulo.trim().length < 3) {
    errors.titulo = 'O título deve ter pelo menos 3 caracteres.'
    ok = false
  }

  if (!form.isbn?.trim()) {
    errors.isbn = 'O ISBN é obrigatório.'
    ok = false
  } else if (!/^\d+$/.test(form.isbn.trim())) {
    errors.isbn = 'O ISBN deve conter apenas números.'
    ok = false
  }

  if (!form.editora?.trim()) {
    errors.editora = 'A editora é obrigatória.'
    ok = false
  } else if (form.editora.trim().length < 2) {
    errors.editora = 'A editora deve ter pelo menos 2 caracteres.'
    ok = false
  }

  if (form.anoPublicacao != null) {
    if (form.anoPublicacao < 1000 || form.anoPublicacao > nowYear) {
      errors.anoPublicacao = `Ano de publicação inválido (1000–${nowYear}).`
      ok = false
    }
  }

  if (!form.generoId) {
    errors.generoId = 'Selecione um gênero.'
    ok = false
  }
  if (!form.autorId) {
    errors.autorId = 'Selecione um autor.'
    ok = false
  }

  return ok
}

function clearErrors() {
  generalError.value = ''
  Object.keys(errors).forEach(k => delete errors[k])
}

async function save() {
  if (!validate()) return

  submitting.value = true
  try {
    const payload = {
      id: form.id,
      titulo: form.titulo.trim(),
      isbn: form.isbn.trim(),
      editora: form.editora.trim(),
      anoPublicacao: form.anoPublicacao ?? null,
      autorId: form.autorId,
      generoId: form.generoId
    }

    if (form.id) {
      await api.put(`/livros/${form.id}`, payload)
    } else {
      await api.post('/livros', payload)
    }

    await loadAll()
    reset()
  } catch (e: any) {
    generalError.value = e?.response?.data?.message || 'Falha ao salvar livro.'
  } finally {
    submitting.value = false
  }
}

function edit(l: LivroDTO) {
  clearErrors()
  Object.assign(form, {
    id: l.id,
    titulo: l.titulo,
    isbn: l.isbn,
    editora: l.editora,
    anoPublicacao: l.anoPublicacao ?? null,
    autorId: l.autor?.id ?? l.autorId ?? null,
    generoId: l.genero?.id ?? l.generoId ?? null
  })
}

async function remove(id?: number) {
  if (!id) return
  if (!confirm('Tem certeza que deseja excluir este livro?')) return
  try {
    await api.delete(`/livros/${id}`)
    await loadAll()
  } catch (e: any) {
    generalError.value = e?.response?.data?.message || 'Falha ao excluir livro.'
  }
}

function reset() {
  clearErrors()
  Object.assign(form, {
    id: undefined,
    titulo: '',
    isbn: '',
    editora: '',
    anoPublicacao: null,
    generoId: null,
    autorId: null
  })
}

onMounted(loadAll)
</script>
