import {
  createRouter,
  createWebHistory,
  type RouteRecordRaw,
  type NavigationGuardNext,
  type RouteLocationNormalized
} from "vue-router";

import LivrosView from "../views/LivrosView.vue";
import AutoresView from "../views/AutoresView.vue";
import GenerosView from "../views/GenerosView.vue";
import LoginView from "../views/LoginView.vue";
import CadastroView from "../views/CadastroView.vue";

function isTokenValid(token: string | null): boolean {
  if (!token) return false;
  try {
    const [, payload] = token.split(".");
    const json = JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
    return Date.now() < (json.exp as number) * 1000;
  } catch {
    return false;
  }
}

const routes: RouteRecordRaw[] = [
  { path: "/", redirect: "/livros" },

  { path: "/login", name: "login", component: LoginView, meta: { guestOnly: true, hideHeader: true } },
  { path: "/cadastro", name: "cadastro", component: CadastroView, meta: { guestOnly: true, hideHeader: true } },

  { path: "/livros", name: "livros", component: LivrosView, meta: { requiresAuth: true } },
  { path: "/autores", name: "autores", component: AutoresView, meta: { requiresAuth: true } },
  { path: "/generos", name: "generos", component: GenerosView, meta: { requiresAuth: true } },

  { path: "/:pathMatch(.*)*", redirect: "/livros" }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
  const token = localStorage.getItem("token");
  const authenticated = isTokenValid(token);

  if (to.meta.requiresAuth && !authenticated) {
    if (token) localStorage.removeItem("token");
    return next({ name: "login", query: { redirect: to.fullPath } });
  }

  if (to.meta.guestOnly && authenticated) {
    const redirect = (to.query.redirect as string) || "/livros";
    return next(redirect);
  }

  return next();
});

export default router;
