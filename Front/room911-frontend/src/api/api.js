import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api"
});

// Adjunta el token JWT a cada peticion si hay sesion activa.
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("room911_token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Sesion expirada o token invalido: limpia la sesion y envia al login.
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401 && !error.config.url.includes("/auth/login")) {
            localStorage.removeItem("room911_token");
            localStorage.removeItem("room911_user");
            if (window.location.pathname !== "/login") {
                window.location.replace("/login");
            }
        }
        return Promise.reject(error);
    }
);

export default api;
