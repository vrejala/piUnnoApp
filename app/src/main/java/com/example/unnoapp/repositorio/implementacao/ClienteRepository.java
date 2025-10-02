package com.example.unnoapp.repositorio.implementacao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.unnoapp.modelo.Cliente;
import com.example.unnoapp.repositorio.contrato.IClienteRepositorio;
import com.example.unnoapp.util.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteRepository implements IClienteRepositorio {

    private final ApiService apiService;

    // Construtor recebendo ApiService
    public ClienteRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<List<Cliente>> getTodos() {
        MutableLiveData<List<Cliente>> data = new MutableLiveData<>();

        apiService.getClientes().enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    @Override
    public LiveData<Cliente> getPorId(int id) {
        MutableLiveData<Cliente> data = new MutableLiveData<>();

        apiService.getCliente(id).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    @Override
    public void inserir(Cliente cliente) {
        apiService.criarCliente(cliente).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (!response.isSuccessful()) {
                    // Tratar erro caso queira feedback
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                // Tratar erro
            }
        });
    }

    @Override
    public void alterar(Cliente cliente) {
        apiService.atualizarCliente(cliente.getId(), cliente).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (!response.isSuccessful()) {
                    // Tratar erro
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                // Tratar erro
            }
        });
    }

    @Override
    public void deletar(int id) {
        apiService.deletarCliente(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    // Tratar erro
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Tratar erro
            }
        });
    }

    @Override
    public LiveData<List<Cliente>> getFiltro(String nome) {
        MutableLiveData<List<Cliente>> filteredData = new MutableLiveData<>();
        getTodos().observeForever(list -> {
            if (list != null) {
                List<Cliente> result = new ArrayList<>();
                for (Cliente c : list) {
                    if (c.getNome().toLowerCase().contains(nome.toLowerCase())) {
                        result.add(c);
                    }
                }
                filteredData.setValue(result);
            } else {
                filteredData.setValue(null);
            }
        });
        return filteredData;
    }
}
