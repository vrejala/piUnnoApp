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
import com.example.unnoapp.modelo.ResetPasswordRequest;
import com.example.unnoapp.util.ApiClient;
import com.example.unnoapp.util.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etEmail, etSenhaAtual, etNovaSenha, etConfirmarSenha, etToken;
    private Button btnAlterar;
    private ImageView btnMostrarSenhaAtual, btnMostrarNovaSenha, btnMostrarConfirmarSenha;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private boolean modoReset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Inicializar views
        etEmail = findViewById(R.id.etEmail);
        etSenhaAtual = findViewById(R.id.etSenhaAtual);
        etNovaSenha = findViewById(R.id.etNovaSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
        etToken = findViewById(R.id.etToken); // novo campo para token
        btnAlterar = findViewById(R.id.btnAlterarSenha);

        btnMostrarSenhaAtual = findViewById(R.id.btnMostrarSenhaAtual);
        btnMostrarNovaSenha = findViewById(R.id.btnMostrarNovaSenha);
        btnMostrarConfirmarSenha = findViewById(R.id.btnMostrarConfirmarSenha);

        // Configurar botões de mostrar senha
        configurarMostrarSenha(etSenhaAtual, btnMostrarSenhaAtual);
        configurarMostrarSenha(etNovaSenha, btnMostrarNovaSenha);
        configurarMostrarSenha(etConfirmarSenha, btnMostrarConfirmarSenha);

        apiService = ApiClient.getApiService();

        // Verifica se a Activity foi aberta em modo reset
        modoReset = getIntent().getBooleanExtra("modoReset", false);
        if(modoReset) {
            etSenhaAtual.setVisibility(EditText.GONE);
            etEmail.setVisibility(EditText.GONE);
            etToken.setVisibility(EditText.VISIBLE); // mostrar campo token
            btnAlterar.setText("Redefinir Senha");
        } else {
            etToken.setVisibility(EditText.GONE);
        }

        // Listener do botão
        btnAlterar.setOnClickListener(v -> {
            String nova = etNovaSenha.getText().toString().trim();
            String conf = etConfirmarSenha.getText().toString().trim();

            if(nova.isEmpty() || conf.isEmpty() || (modoReset && etToken.getText().toString().trim().isEmpty()) ||
                    (!modoReset && (etEmail.getText().toString().trim().isEmpty() || etSenhaAtual.getText().toString().trim().isEmpty()))) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!nova.equals(conf)) {
                Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show();
                return;
            }

            if(modoReset) {
                String token = etToken.getText().toString().trim();
                resetSenhaPorToken(token, nova);
            } else {
                String email = etEmail.getText().toString().trim();
                String atual = etSenhaAtual.getText().toString().trim();
                alterarSenhaNormal(email, atual, nova);
            }
        });
    }

    // Método para alterar senha normal (com senha atual)
    private void alterarSenhaNormal(String email, String senhaAtual, String novaSenha) {
        progressDialog = ProgressDialog.show(this, "", "Alterando senha...");

        ChangePasswordRequest request = new ChangePasswordRequest(email, senhaAtual, novaSenha);
        apiService.trocarSenha(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    Toast.makeText(ChangePasswordActivity.this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();
                    finish(); // volta para login
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
