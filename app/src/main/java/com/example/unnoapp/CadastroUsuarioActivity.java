package com.example.unnoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.unnoapp.modelo.Usuario;
import com.example.unnoapp.util.ApiClient;
import com.example.unnoapp.util.ApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private EditText etNome, etEmail, etSenha;
    private Button btnSalvar, btnCancelar;

    private static final String PREFS_NAME = "APP_PREFS";
    private static final String KEY_USUARIO = "usuario";
    private Gson gson = new Gson();


    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        // Bind views
        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);

        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancelar = findViewById(R.id.btnCancelar);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usuarioJson = prefs.getString(KEY_USUARIO, null);

        if (usuarioJson != null) {
            Usuario usuarioLogado = gson.fromJson(usuarioJson, Usuario.class);
            etNome.setText(usuarioLogado.getNome());
            etEmail.setText(usuarioLogado.getEmail());
        }


        apiService = ApiClient.getApiService();

       // Botões
        btnSalvar.setOnClickListener(v -> salvarUsuario());
        btnCancelar.setOnClickListener(v -> finish());

        ImageView btnMostrarSenha;

        btnMostrarSenha = findViewById(R.id.btnMostrarSenha);
        configurarMostrarSenha(etSenha, btnMostrarSenha);
    }

    // =====================
    // MÉTODOS
    // =====================
    private void salvarUsuario() {
        String nome = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        if (!validarCampos(nome, email, senha)) return;

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);

        apiService.criarUsuario(usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CadastroUsuarioActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CadastroUsuarioActivity.this, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(CadastroUsuarioActivity.this, "Falha na requisição: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarCampos(String nome, String email, String senha) {
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    enviarTokenParaApi(idToken);
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Falha no login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enviarTokenParaApi(String token) {
        Call<Usuario> call = apiService.loginGoogle(new com.example.unnoapp.modelo.GoogleLoginRequest(token));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CadastroUsuarioActivity.this, "Bem-vindo " + response.body().getNome(), Toast.LENGTH_SHORT).show();
                    finish(); // ou abrir MainActivity
                } else {
                    Toast.makeText(CadastroUsuarioActivity.this, "Erro ao logar com Google", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(CadastroUsuarioActivity.this, "Falha na requisição: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
