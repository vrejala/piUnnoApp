package com.example.unnoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unnoapp.modelo.GoogleLoginRequest;
import com.example.unnoapp.modelo.Usuario;
import com.example.unnoapp.modelo.UsuarioLogin;
import com.example.unnoapp.util.ApiClient;
import com.example.unnoapp.util.ApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLogin, editTextSenha;
    private Button btnDoSignIn, btnToggle;
    private Gson gson = new Gson();
    private static final String PREFS_NAME = "APP_PREFS";
    private static final String KEY_USUARIO = "usuario";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private ApiService apiService; // seu Retrofit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se já existe usuário logado, vai direto para TelaInicialActivity
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usuarioJson = prefs.getString(KEY_USUARIO, null);
        if (usuarioJson != null) {
            startActivity(new Intent(this, TelaInicialActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        apiService = ApiClient.getApiService();

        // Configuração do Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Botão de login Google
        findViewById(R.id.btnLoginGoogle).setOnClickListener(v -> signInGoogle());

        // Bind views
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextSenha = findViewById(R.id.editTextSenha);
        btnDoSignIn = findViewById(R.id.btnDoSignIn);
        btnToggle = findViewById(R.id.btnToggle);

        // Botão Entrar
        btnDoSignIn.setOnClickListener(v -> realizarLogin());

        // Botão "Inscreva-se" leva para Cadastro
        btnToggle.setOnClickListener(v -> {
            Intent intent = new Intent(this, CadastroUsuarioActivity.class);
            startActivity(intent);
        });

    }

    private void realizarLogin() {
        String login = editTextLogin.getText().toString().trim();
        String senha = editTextSenha.getText().toString().trim();

        if (login.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha login e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioLogin credenciais = new UsuarioLogin(login, senha);
        Log.d("LoginActivity", "JSON enviado: " + gson.toJson(credenciais));

        ApiService apiService = ApiClient.getApiService();
        apiService.login(credenciais).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuarioLogado = response.body();

                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(KEY_USUARIO, gson.toJson(usuarioLogado));
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Bem-vindo " + usuarioLogado.getNome(), Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, TelaInicialActivity.class));
                    finish();

                } else {
                    try {
                        String errorMsg = response.errorBody().string();
                        JSONObject json = new JSONObject(errorMsg);
                        String mensagem = json.getString("detail");
                        Toast.makeText(LoginActivity.this, mensagem, Toast.LENGTH_LONG).show();
                        Log.e("LoginActivity", "Erro: " + errorMsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Erro ao logar", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "Falha Retrofit", t);
            }
        });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        // Aqui você envia para sua API, usando Retrofit
        Call<Usuario> call = apiService.loginGoogle(new GoogleLoginRequest(token));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Login realizado com sucesso
                    Toast.makeText(LoginActivity.this, "Bem-vindo " + response.body().getNome(), Toast.LENGTH_SHORT).show();
                    // Redirecionar para MainActivity
                } else {
                    Toast.makeText(LoginActivity.this, "Erro ao logar com Google", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Falha na requisição: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
