package com.example.unnoapp.modelo;

public class Profissional {

    private int id;
    private String nome;
    private String cadastroprofissional;
    private String email;
    private String telefone;
    private Float valor;
    private String sobre;
    private String especialidade;
    private String abordagem;
   private String tipopagamento;

   private boolean ativo = true;


    // Construtor vazio (necessário para Retrofit/Gson)
    public Profissional() {}

    public Profissional(String nome,  String cadastroprofissional, String email, String telefone, Float valor, String sobre, String especialidade, String abordagem, String tipopagamento) {
        this.nome = nome;
        this.cadastroprofissional = cadastroprofissional;
        this.email = email;
        this.telefone = telefone;
        this.valor = valor;
        this.sobre = sobre;
        this.especialidade = especialidade;
        this.abordagem = abordagem;
        this.tipopagamento = tipopagamento;

    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCadastroprofissional() { return cadastroprofissional; }
    public void setCadastroprofissional(String cadastroprofissional) { this.cadastroprofissional = cadastroprofissional; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) {this.telefone = telefone; }

    public Float getValor() { return valor; }
    public void setValor(Float valor) { this.valor = valor; }

    public String getSobre() { return sobre; }
    public void setSobre(String sobre) { this.sobre = sobre; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getAbordagem() { return abordagem; }
    public void setAbordagem(String abordagem) { this.abordagem = abordagem; }

    public String getTipopagamento() { return tipopagamento; }
    public void setTipopagamento(String tipopagamento) { this.tipopagamento = tipopagamento; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo){ this.ativo = ativo; }

    // =============================
    // MÉTODOS DE VALIDAÇÃO
    // =============================

    public boolean isCamposPreenchidos() {
        return  nome != null && !nome.isEmpty() &&
                telefone != null && !telefone.isEmpty() &&
                cadastroprofissional != null && !cadastroprofissional.isEmpty() &&
                email != null && !email.isEmpty() &&
                valor != null && valor > 0 &&
                sobre != null && !sobre.isEmpty() &&
                especialidade != null && !especialidade.isEmpty() &&
                abordagem != null && !abordagem.isEmpty() &&
                tipopagamento != null && !tipopagamento.isEmpty() &&
                ativo;
    }

    public boolean isCadastroValido() {
        return isCamposPreenchidos();
    }

    }
