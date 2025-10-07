package com.example.unnoapp.modelo;

public class ResetPasswordRequest {
    private String token;
    private String nova_senha; // ou novaSenha, dependendo do backend

    public ResetPasswordRequest(String token, String nova_senha) {
        this.token = token;
        this.nova_senha = nova_senha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNova_senha() {
        return nova_senha;
    }

    public void setNova_senha(String nova_senha) {
        this.nova_senha = nova_senha;
    }
}
