import axios, { AxiosError } from "axios";
import router from "../router";

const TOKEN_KEY = "token";
export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const setToken = (t: string) => localStorage.setItem(TOKEN_KEY, t);
export const clearToken = () => localStorage.removeItem(TOKEN_KEY);

const api = axios.create({
  baseURL: "/api/v1",
  timeout: 20000,
});

api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (r) => r,
  async (err: AxiosError) => {
    const status = err.response?.status;
    if (status === 401) {
      clearToken();
      if (router.currentRoute.value.name !== "login") {
        const redirect = router.currentRoute.value.fullPath || "/livros";
        router.push({ name: "login", query: { redirect } });
      }
    }
    return Promise.reject(err);
  }
);

export const authApi = axios.create({ baseURL: "/api/v1/auth", timeout: 20000 });

export async function login(username: string, password: string) {
  const { data } = await authApi.post<{ token: string; roles: string[] }>(
    "/login",
    { username, password }
  );
  setToken(data.token);
  localStorage.setItem("roles", JSON.stringify(data.roles ?? []));
  return data;
}

export type RegisterPayload = {
  username: string; password: string; confirmPassword: string; roles: string[];
};

export async function register(payload: RegisterPayload) {
  const { data } = await authApi.post("/register", payload);
  return data;
}

export default api;
export const unwrap = <T>(res: any) => res?.data?.data as T;
