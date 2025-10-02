package com.example.unnoapp.repositorio.contrato;

import androidx.lifecycle.LiveData;
import com.example.unnoapp.modelo.Profissional;
import java.util.List;

public interface IProfissionalRepositorio {
    void inserir(Profissional profissionais);
    void alterar(Profissional profissionais);
    void deletar(int id);
    LiveData<Profissional> getPorId(int id);
    LiveData<List<Profissional>> getTodos();
    LiveData<List<Profissional>> getFiltro(String nome);
    LiveData<List<Profissional>> getProfissionais();
    LiveData<List<Profissional>> getProfissionaisPorEspecialidade(String especialidade);
    LiveData<List<Profissional>> findByAtivoTrue();
    LiveData<List<Profissional>> findByAtivoFalse();
}


