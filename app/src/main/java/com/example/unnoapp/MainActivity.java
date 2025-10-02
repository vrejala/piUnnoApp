package com.example.unnoapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.unnoapp.modelo.HealthResponse;
import com.example.unnoapp.repositorio.implementacao.UsuarioRepository;
import com.example.unnoapp.util.ApiClient;
import com.example.unnoapp.util.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "API_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuração Edge-to-Edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Chamada de teste de conexão à API
        ApiService apiService = ApiClient.getApiService();

        UsuarioRepository repository = new UsuarioRepository(apiService);
        // Exemplo de observação
        repository.getTodos().observe(this, usuarios -> {
            // Atualizar UI com a lista
        });

        apiService.healthCheck().enqueue(new Callback<HealthResponse>() {
            @Override
            public void onResponse(Call<HealthResponse> call, Response<HealthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Conexão bem sucedida! Status: " + response.body().getStatus());
                } else {
                    Log.e(TAG, "Erro da API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<HealthResponse> call, Throwable t) {
                Log.e(TAG, "Falha de conexão: " + t.getMessage(), t);
            }
        });

        Log.d(TAG, "MainActivity criada, teste de conexão disparado");
    }
}
