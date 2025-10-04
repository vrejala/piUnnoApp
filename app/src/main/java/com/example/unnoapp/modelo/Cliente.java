package com.example.unnoapp.modelo;

public class Cliente {

    private int id;
    private String nome;
    private String telefone;
    private String cpf;
    private String email;
    private String senha;
    private String endereco;
    private String numero;
    private String cep;
    private int usuario_id;


    // Construtor vazio
    public Cliente() {}

    // Construtor completo
    public Cliente(String nome, String telefone, String cpf, String email, String senha,
                   String endereco, String numero, String cep, int usuario_id) {
        this.nome = nome;
        this.telefone = telefone;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.endereco = endereco;
        this.numero = numero;
        this.cep = cep;
        this.usuario_id = usuario_id;

    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public int getUsuario_id() { return usuario_id; }
    public void setUsuario_id(int usuario_id) { this.usuario_id = usuario_id; }

     // Validação de campos
    public boolean isCamposPreenchidos() {
        return nome != null && !nome.isEmpty() &&
                telefone != null && !telefone.isEmpty() &&
                cpf != null && !cpf.isEmpty() &&
                email != null && !email.isEmpty() &&
                senha != null && !senha.isEmpty() &&
                endereco != null && !endereco.isEmpty() &&
                numero != null && !numero.isEmpty() &&
                cep != null && !cep.isEmpty();
    }
}
