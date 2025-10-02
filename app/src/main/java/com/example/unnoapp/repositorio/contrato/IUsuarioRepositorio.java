package com.example.unnoapp.repositorio.contrato;

import androidx.lifecycle.LiveData;
import com.example.unnoapp.modelo.Usuario;
import java.util.List;

public interface IUsuarioRepositorio {
    void inserir(Usuario usuario);
    void alterar(Usuario usuario);
    void deletar(int id);
    LiveData<Usuario> getPorId(int id);
    LiveData<List<Usuario>> getTodos();
    LiveData<List<Usuario>> getFiltro(String nome);
}
