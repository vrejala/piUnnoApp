package com.example.unnoapp.repositorio.implementacao;

import com.example.unnoapp.modelo.Profissional;
import com.example.unnoapp.repositorio.contrato.IProfissionalRepositorio;
import java.util.ArrayList;
import java.util.List;

public class ProfissionalRepository implements IProfissionalRepositorio {

    private static List<Profissional> profissionais = new ArrayList<>();

    @Override
    public void adicionarProfissional(Profissional prof) {
        profissionais.add(prof);
    }

    @Override
    public List<Profissional> getProfissionais() {
        return profissionais;
    }

    @Override
    public List<Profissional> getProfissionaisPorEspecialidade(String especialidade) {
        List<Profissional> filtrados = new ArrayList<>();
        for (Profissional p : profissionais) {
            if (p.getEspecialidade().equalsIgnoreCase(especialidade)) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }
}
