package com.example.unnoapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unnoapp.modelo.ChangePasswordRequest;
import com.example.unnoapp.util.ApiClient;
import com.example.unnoapp.util.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etEmail, etSenhaAtual, etNovaSenha, etConfirmarSenha;
    private Button btnAlterar;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private ImageView btnMostrarSenhaAtual, btnMostrarNovaSenha, btnMostrarConfirmarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etEmail = findViewById(R.id.etEmail);
        etSenhaAtual = findViewById(R.id.etSenhaAtual);
        etNovaSenha = findViewById(R.id.etNovaSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
        btnAlterar = findViewById(R.id.btnAlterarSenha);

        btnMostrarSenhaAtual = findViewById(R.id.btnMostrarSenhaAtual);
        btnMostrarNovaSenha = findViewById(R.id.btnMostrarNovaSenha);
        btnMostrarConfirmarSenha = findViewById(R.id.btnMostrarConfirmarSenha);

        // No onCreate:
        configurarMostrarSenha(etSenhaAtual, btnMostrarSenhaAtual);
        configurarMostrarSenha(etNovaSenha, btnMostrarNovaSenha);
        configurarMostrarSenha(etConfirmarSenha, btnMostrarConfirmarSenha);

        apiService = ApiClient.getApiService();

        btnAlterar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String atual = etSenhaAtual.getText().toString().trim();
            String nova = etNovaSenha.getText().toString().trim();
            String conf = etConfirmarSenha.getText().toString().trim();

            if(email.isEmpty() || atual.isEmpty() || nova.isEmpty() || conf.isEmpty()){
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!nova.equals(conf)){
                Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show();
                return;
            }

            alterarSenha(email, atual, nova);
        });
    }

    private void alterarSenha(String email, String senhaAtual, String novaSenha) {
        progressDialog = ProgressDialog.show(this, "", "Alterando senha...");

        ChangePasswordRequest request = new ChangePasswordRequest(email, senhaAtual, novaSenha);

        apiService.trocarSenha(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    Toast.makeText(ChangePasswordActivity.this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();
                    finish(); // volta para Login
                } else {
                    String msg = "Erro ao alterar senha.";
                    try {
                        if(response.errorBody() != null) msg = response.errorBody().string();
                    } catch (Exception e){
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
