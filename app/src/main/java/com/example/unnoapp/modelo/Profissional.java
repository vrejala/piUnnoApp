package com.example.unnoapp.modelo;

public class Profissional {
    private String nome;
    private String email;
    private String telefone;
    private String abordagem;
    private String valor;
    private String fotoUri;
    private String especialidade;

    public Profissional(String nome, String email, String telefone, String abordagem, String valor, String fotoUri, String especialidade) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.abordagem = abordagem;
        this.valor = valor;
        this.fotoUri = fotoUri;
        this.especialidade = especialidade;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getAbordagem() { return abordagem; }
    public String getValor() { return valor; }
    public String getFotoUri() { return fotoUri; }
    public String getEspecialidade() { return especialidade; }
}
