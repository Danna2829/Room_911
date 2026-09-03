import { createContext, useContext, useState, useCallback } from "react";
import api from "../api/api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("room911_token"));

  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem("room911_user"));
    } catch {
      return null;
    }
  });

  const login = useCallback(async (correo, contrasena) => {
    const { data } = await api.post("/auth/login", { correo, contrasena });
    const u = {
      idUsuario: data.idUsuario,
      nombre: data.nombre,
      correo: data.correo,
      rol: data.rol,
    };
    localStorage.setItem("room911_token", data.token);
    localStorage.setItem("room911_user", JSON.stringify(u));
    setToken(data.token);
    setUser(u);
    return u;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("room911_token");
    localStorage.removeItem("room911_user");
    setToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
  return ctx;
}
