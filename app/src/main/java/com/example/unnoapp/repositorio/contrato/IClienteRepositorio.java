package com.example.unnoapp.repositorio.contrato;

import androidx.lifecycle.LiveData;
import com.example.unnoapp.modelo.Cliente;
import java.util.List;

public interface IClienteRepositorio {
    void inserir(Cliente cliente);
    void alterar(Cliente cliente);
    void deletar(int id);
    LiveData<Cliente> getPorId(int id);
    LiveData<List<Cliente>> getTodos();
    LiveData<List<Cliente>> getFiltro(String nome);
}
