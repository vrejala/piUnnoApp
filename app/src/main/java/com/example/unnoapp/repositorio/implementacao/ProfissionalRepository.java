package com.example.unnoapp.repositorio.implementacao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.unnoapp.modelo.Profissional;
import com.example.unnoapp.repositorio.contrato.IProfissionalRepositorio;
import com.example.unnoapp.util.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfissionalRepository implements IProfissionalRepositorio {

    private final ApiService apiService;

    // Construtor recebendo ApiService
    public ProfissionalRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<List<Profissional>> getTodos() {
        MutableLiveData<List<Profissional>> data = new MutableLiveData<>();

        apiService.getProfissional().enqueue(new Callback<List<Profissional>>() {
            @Override
            public void onResponse(Call<List<Profissional>> call, Response<List<Profissional>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Profissional>> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }


    @Override
    public LiveData<Profissional> getPorId(int id) {
        MutableLiveData<Profissional> data = new MutableLiveData<>();

        apiService.getProfissional(id).enqueue(new Callback<Profissional>() {
            @Override
            public void onResponse(Call<Profissional> call, Response<Profissional> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Profissional> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    @Override
    public void inserir(Profissional profissionais) {
        apiService.criarProfissional(profissionais).enqueue(new Callback<Profissional>() {
            @Override
            public void onResponse(Call<Profissional> call, Response<Profissional> response) {
                if (!response.isSuccessful()) {
                    // Tratar erro caso queira feedback
                }
            }

            @Override
            public void onFailure(Call<Profissional> call, Throwable t) {
                // Tratar erro
            }
        });
    }

    @Override
    public LiveData<List<Profissional>> findByAtivoTrue() {
        MutableLiveData<List<Profissional>> data = new MutableLiveData<>();
        getTodos().observeForever(list -> {
            if (list != null) {
                List<Profissional> ativos = new ArrayList<>();
                for (Profissional p : list) {
                    if (Boolean.TRUE.equals(p.getAtivo())) {  // ✅ usa getAtivo()
                        ativos.add(p);
                    }
                }
                data.setValue(ativos);
            } else {
                data.setValue(null);
            }
        });
        return data;
    }

    @Override
    public LiveData<List<Profissional>> findByAtivoFalse() {
        MutableLiveData<List<Profissional>> data = new MutableLiveData<>();
        getTodos().observeForever(list -> {
            if (list != null) {
                List<Profissional> inativos = new ArrayList<>();
                for (Profissional p : list) {
                    if (!Boolean.TRUE.equals(p.getAtivo())) { // ✅ usa getAtivo()
                        inativos.add(p);
                    }
                }
                data.setValue(inativos);
            } else {
                data.setValue(null);
            }
        });
        return data;
    }



    @Override
    public void alterar(Profissional profissionais) {
        apiService.atualizarProfissional(profissionais.getId(), profissionais).enqueue(new Callback<Profissional>() {
            @Override
            public void onResponse(Call<Profissional> call, Response<Profissional> response) {
                if (!response.isSuccessful()) {
                    // Tratar erro
                }
            }

            @Override
            public void onFailure(Call<Profissional> call, Throwable t) {
                // Tratar erro
            }
        });
    }

    @Override
    public void deletar(int id) {
        apiService.deletarProfissional(id).enqueue(new Callback<Void>() {
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
    public LiveData<List<Profissional>> getFiltro(String nome) {
        MutableLiveData<List<Profissional>> filteredData = new MutableLiveData<>();
        getTodos().observeForever(list -> {
            if (list != null) {
                List<Profissional> result = new ArrayList<>();
                for (Profissional u : list) {
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

    @Override
    public LiveData<List<Profissional>> getProfissionais() {
        return getTodos(); // já existe esse método
    }

    @Override
    public LiveData<List<Profissional>> getProfissionaisPorEspecialidade(String especialidade) {
        MutableLiveData<List<Profissional>> data = new MutableLiveData<>();

        apiService.getProfissionaisPorEspecialidade(especialidade).enqueue(new Callback<List<Profissional>>() {
            @Override
            public void onResponse(Call<List<Profissional>> call, Response<List<Profissional>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                    Log.e("Repository", "Erro response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Profissional>> call, Throwable t) {
                data.setValue(null);
                Log.e("Repository", "Falha na API: " + t.getMessage(), t);
            }
        });

        return data;
    }


}
