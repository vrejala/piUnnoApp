package com.example.unnoapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unnoapp.modelo.ResetPasswordRequest;
import com.example.unnoapp.util.ApiClient;
import com.example.unnoapp.util.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etToken, etNovaSenha, etConfirmarSenha;
    private Button btnAlterar;
    private ImageView btnMostrarNovaSenha, btnMostrarConfirmarSenha;
    private ProgressDialog progressDialog;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Inicializar views
        etToken = findViewById(R.id.etToken);
        etNovaSenha = findViewById(R.id.etNovaSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
        btnAlterar = findViewById(R.id.btnAlterarSenha);

        btnMostrarNovaSenha = findViewById(R.id.btnMostrarNovaSenha);
        btnMostrarConfirmarSenha = findViewById(R.id.btnMostrarConfirmarSenha);

        // Configurar botões de mostrar senha
        configurarMostrarSenha(etNovaSenha, btnMostrarNovaSenha);
        configurarMostrarSenha(etConfirmarSenha, btnMostrarConfirmarSenha);

        apiService = ApiClient.getApiService();

        // Listener do botão
        btnAlterar.setOnClickListener(v -> {
            String token = etToken.getText().toString().trim();
            String nova = etNovaSenha.getText().toString().trim();
            String conf = etConfirmarSenha.getText().toString().trim();

            if(token.isEmpty() || nova.isEmpty() || conf.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!nova.equals(conf)) {
                Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show();
                return;
            }

            resetSenhaPorToken(token, nova);
        });
    }

    // Método para resetar senha via token
    private void resetSenhaPorToken(String token, String novaSenha) {
        progressDialog = ProgressDialog.show(this, "", "Redefinindo senha...");

        ResetPasswordRequest request = new ResetPasswordRequest(token, novaSenha);
        apiService.confirmarReset(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Senha redefinida com sucesso!", Toast.LENGTH_LONG).show();
                    finish(); // volta para login
                } else {
                    String msg = "Erro ao redefinir senha.";
                    try {
                        if(response.errorBody() != null) msg = response.errorBody().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ChangePasswordActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Reutiliza a função de mostrar senha
    private void configurarMostrarSenha(EditText campo, ImageView botao) {
        botao.setOnClickListener(v -> {
            if (campo.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                campo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                botao.setImageResource(R.drawable.ic_eye);
            } else {
                campo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                botao.setImageResource(R.drawable.ic_ey_offe);
            }
            campo.setSelection(campo.getText().length());
        });
    }
}
