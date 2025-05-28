/* eslint-disable react-refresh/only-export-components */
import React, { createContext, useContext, useEffect, useState } from "react";
import { AxiosError } from "axios";//axios,
import API from "../services/api.tsx";

interface User {
  fullname: string;
  email: string;
  profileImageUrl: string;
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  deleteAccount: () => Promise<void>; // New function
  checkAuth: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");
  return context;
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const isAuthenticated = !!user;

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    console.log("[Auth] Checking auth...");
    try {
      const res = await API.get("/api/auth/me", { withCredentials: true });
      console.log("[Auth] checkAuth success:", res.data.user);
      setUser(res.data.user);
    } catch (err) {
      const error = err as AxiosError;
      console.log("[Auth] checkAuth failed:", error.message);
      setUser(null);
    }
  };

  const login = async (email: string, password: string) => {
    const res = await API.post("/api/auth/login", { email, password }, { withCredentials: true });
    console.log("[Auth] Login success:", res.data.user);
    setUser(res.data.user);
  };

  const logout = async () => {
    await API.post("/api/auth/logout", {}, { withCredentials: true });
    console.log("[Auth] Logout success");
    setUser(null);
  };

  const deleteAccount = async () => {
    try {
      await API.delete("/api/auth/delete-account", { withCredentials: true });
      console.log("[Auth] Account deleted.");
      setUser(null);
    } catch (err) {
      const error = err as AxiosError;
      console.log("[Auth] Delete account failed:", error.message);
      throw error; 
    }
  };

  console.log("[Auth] Render — user:", user);
  return (
    <AuthContext.Provider value={{ user, isAuthenticated, login, logout, deleteAccount, checkAuth }}>
      {children}
    </AuthContext.Provider>
  );
};