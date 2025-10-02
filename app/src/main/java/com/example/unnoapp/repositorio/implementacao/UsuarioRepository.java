package com.example.unnoapp.repositorio.implementacao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.unnoapp.modelo.Usuario;
import com.example.unnoapp.repositorio.contrato.IUsuarioRepositorio;
import com.example.unnoapp.util.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioRepository implements IUsuarioRepositorio {

    private final ApiService apiService;

    public UsuarioRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<List<Usuario>> getTodos() {
        MutableLiveData<List<Usuario>> data = new MutableLiveData<>();
        apiService.getUsuarios().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                data.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    @Override
    public LiveData<Usuario> getPorId(int id) {
        MutableLiveData<Usuario> data = new MutableLiveData<>();
        apiService.getUsuario(id).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                data.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    @Override
    public void inserir(Usuario usuario) {
        apiService.criarUsuario(usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) { }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) { }
        });
    }

    @Override
    public void alterar(Usuario usuario) {
        apiService.atualizarUsuario(usuario.getId(), usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) { }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) { }
        });
    }

    @Override
    public void deletar(int id) {
        apiService.deletarUsuario(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) { }
            @Override
            public void onFailure(Call<Void> call, Throwable t) { }
        });
    }

    @Override
    public LiveData<List<Usuario>> getFiltro(String nome) {
        MutableLiveData<List<Usuario>> filteredData = new MutableLiveData<>();
        getTodos().observeForever(list -> {
            if (list != null) {
                List<Usuario> result = new ArrayList<>();
                for (Usuario u : list) {
                    if (u.getNome().toLowerCase().contains(nome.toLowerCase())) {
                        result.add(u);
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
