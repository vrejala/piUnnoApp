package com.example.unnoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unnoapp.modelo.EmailRequest;
import com.example.unnoapp.util.ApiClient;
import com.example.unnoapp.util.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EsqueciSenhaActivity extends AppCompatActivity {

    private EditText editEmail;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        editEmail = findViewById(R.id.editEmail);
        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> enviarEmail());
    }

    private void enviarEmail() {
        String email = editEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Digite seu email", Toast.LENGTH_SHORT).show();
            return;
        }

        EmailRequest request = new EmailRequest(email);
        ApiService apiService = ApiClient.getApiService();

        apiService.enviarEmailReset(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EsqueciSenhaActivity.this, "Email de redefinição enviado!", Toast.LENGTH_LONG).show();

                    // Abrir ActivityChangePassword
                    Intent intent = new Intent(EsqueciSenhaActivity.this, ChangePasswordActivity.class);
                    intent.putExtra("modoReset", true);
                    startActivity(intent);

                } else {
                    Toast.makeText(EsqueciSenhaActivity.this, "Erro ao enviar email", Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("EsqueciSenha", "Erro: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EsqueciSenhaActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
