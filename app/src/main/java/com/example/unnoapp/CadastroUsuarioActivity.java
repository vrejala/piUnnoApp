package com.example.unnoapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    EditText edtNome, edtTelefone, edtCpf, edtEmail, edtSenha, edtRepetirSenha;
    Switch switchResideFora;
    Button btnCadastrar;
    SignInButton btnGoogle;
    GoogleSignInClient mGoogleSignInClient;

    boolean modoEdicao = false; // true = edição de perfil, false = cadastro normal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        // Inicializar componentes
        edtNome = findViewById(R.id.edtNome);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtCpf = findViewById(R.id.edtCpf);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        edtRepetirSenha = findViewById(R.id.edtRepetirSenha);
        switchResideFora = findViewById(R.id.switchResideFora);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnGoogle = findViewById(R.id.btnGoogle);

        // Recebe parâmetro da Intent para definir modo edição
        modoEdicao = getIntent().getBooleanExtra("modoEdicao", false);

        if (modoEdicao) {
            carregarDadosUsuario();
            btnCadastrar.setText("Salvar Alterações");
        } else {
            btnCadastrar.setText("Cadastrar");
        }

        // Clique no botão Cadastrar / Salvar
        btnCadastrar.setOnClickListener(v -> {
            if (validarCampos()) {
                if (modoEdicao) {
                    atualizarUsuario();
                } else {
                    cadastrarUsuario();
                }
            }
        });

        // Configuração do Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Clique no botão Google
        btnGoogle.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account != null) {
                String nome = account.getDisplayName();
                String email = account.getEmail();

                // Preenche os campos automaticamente
                edtNome.setText(nome);
                edtEmail.setText(email);

                Toast.makeText(this, "Login com Google: " + nome, Toast.LENGTH_SHORT).show();
                // Aqui você pode mandar para sua API o email/nome do Google
            }

        } catch (ApiException e) {
            Toast.makeText(this, "Erro no login com Google: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Carrega os dados do usuário para edição
    private void carregarDadosUsuario() {
        // Exemplo fictício: você pode carregar da API ou SharedPreferences
        edtNome.setText("Leandro Galvão");
        edtEmail.setText("leandro@email.com");
        edtTelefone.setText("+55 99999-9999");
        edtCpf.setText("12345678901");
        switchResideFora.setChecked(false);
        // Senha normalmente não é preenchida por segurança
    }

    // Valida os campos do formulário
    private boolean validarCampos() {
        if (TextUtils.isEmpty(edtNome.getText().toString())) {
            edtNome.setError("Informe seu nome");
            return false;
        }
        if (TextUtils.isEmpty(edtTelefone.getText().toString())) {
            edtTelefone.setError("Informe o telefone");
            return false;
        }
        if (TextUtils.isEmpty(edtCpf.getText().toString()) || edtCpf.getText().toString().length() != 11) {
            edtCpf.setError("Informe CPF válido (11 dígitos)");
            return false;
        }
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Informe o email");
            return false;
        }

        // Se estiver em edição e senha estiver vazio, ignora a validação de senha
        if (!modoEdicao || !TextUtils.isEmpty(edtSenha.getText().toString())) {
            if (edtSenha.getText().toString().length() < 8) {
                edtSenha.setError("Senha deve ter no mínimo 8 caracteres");
                return false;
            }
            if (!edtSenha.getText().toString().equals(edtRepetirSenha.getText().toString())) {
                edtRepetirSenha.setError("Senhas não conferem");
                return false;
            }
        }
        return true;
    }

    // Chama API para cadastro
    private void cadastrarUsuario() {
        Toast.makeText(this, "Cadastro enviado para API!", Toast.LENGTH_LONG).show();
        // Aqui você faria o POST para sua API
    }

    // Chama API para atualização de perfil
    private void atualizarUsuario() {
        Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_LONG).show();
        // Aqui você faria o PUT/PATCH para atualizar os dados na API
    }
}
