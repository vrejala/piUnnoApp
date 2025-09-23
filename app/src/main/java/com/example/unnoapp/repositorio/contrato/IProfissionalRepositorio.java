package com.example.unnoapp.repositorio.contrato;

import com.example.unnoapp.modelo.Profissional;
import java.util.List;

public interface IProfissionalRepositorio {
    void adicionarProfissional(Profissional prof);
    List<Profissional> getProfissionais();
    List<Profissional> getProfissionaisPorEspecialidade(String especialidade);
}
