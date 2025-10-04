package com.example.unnoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unnoapp.modelo.Cliente;
import com.example.unnoapp.util.ApiClient;
import com.example.unnoapp.util.ApiService;
import com.example.unnoapp.util.Util;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.example.unnoapp.modelo.Usuario;


import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroClienteActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private ImageView imgFotoUsuario;
    private Button btnFoto, btnSalvar, btnAlterar, btnExcluir;
    private EditText etNome, etTelefone, etCpf, etEmail, etSenha;
    private EditText etEndereco, etNumero, etCep;
    private static final String PREFS_NAME = "APP_PREFS";
    private static final String KEY_USUARIO = "usuario";
    private Gson gson = new Gson();
    private ApiService apiService;


    private Uri imageUri;
    private Bitmap bitmapFoto;
    private int clienteIdAtual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);

        // Bind views
        imgFotoUsuario = findViewById(R.id.imgFotoUsuario);
        btnFoto = findViewById(R.id.btnFoto);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnAlterar = findViewById(R.id.btnAlterar);
        btnExcluir = findViewById(R.id.btnExcluir);

        etNome = findViewById(R.id.etNome);
        etTelefone = findViewById(R.id.etTelefone);
        etCpf = findViewById(R.id.etCpf);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        etEndereco = findViewById(R.id.etEndereco);
        etNumero = findViewById(R.id.etNumero);
        etCep = findViewById(R.id.etCep);

        // Inicializa o ApiService
        apiService = ApiClient.getApiService();

    // Preencher campos do usuário logado
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usuarioJson = prefs.getString(KEY_USUARIO, null);

        if (usuarioJson != null) {
            Usuario usuarioLogado = gson.fromJson(usuarioJson, Usuario.class);

            // Preenche com nome do usuário primeiro
            etNome.setText(usuarioLogado.getNome());
            etEmail.setText(usuarioLogado.getEmail());
            etSenha.setText(usuarioLogado.getSenha());

            apiService.buscarClientePorUsuario(usuarioLogado.getId())
                    .enqueue(new Callback<Cliente>() {
                        @Override
                        public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Cliente c = response.body();

                                // Aqui sobrescreve com o nome do cliente, se houver
                                if (c.getNome() != null && !c.getNome().isEmpty()) {
                                    etNome.setText(c.getNome());
                                }

                                etTelefone.setText(c.getTelefone());
                                etCpf.setText(c.getCpf());
                                etEndereco.setText(c.getEndereco());
                                etNumero.setText(c.getNumero());
                                etCep.setText(c.getCep());
                                clienteIdAtual = c.getId();
                            }
                        }

                        @Override
                        public void onFailure(Call<Cliente> call, Throwable t) {
                            Toast.makeText(CadastroClienteActivity.this, "Falha ao carregar dados do cliente", Toast.LENGTH_SHORT).show();
                        }
                    });
        }




        // Botões
        btnFoto.setOnClickListener(v -> escolherFoto());
        btnSalvar.setOnClickListener(v -> salvarOuAlterarCliente());
        btnAlterar.setOnClickListener(v -> abrirDialogBuscarCliente());
        btnExcluir.setOnClickListener(v -> abrirDialogExcluirCliente());
    }

    private void escolherFoto() {
        String[] options = {"Tirar foto", "Escolher da galeria"};
        new AlertDialog.Builder(this)
                .setTitle("Adicionar Foto")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) abrirCamera();
                    else abrirGaleria();
                })
                .show();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecione uma foto"), PICK_IMAGE_REQUEST);
    }

    private void abrirCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                    imageUri = data.getData();
                    bitmapFoto = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imgFotoUsuario.setImageBitmap(bitmapFoto);
                } else if (requestCode == CAMERA_REQUEST && data != null) {
                    bitmapFoto = (Bitmap) data.getExtras().get("data");
                    imgFotoUsuario.setImageBitmap(bitmapFoto);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validarCampos(String nome, String telefone, String cpf, String email, String senha,
                                  String endereco, String numero, String cep) {
        if (nome.isEmpty() || telefone.isEmpty() || cpf.isEmpty() || email.isEmpty() ||
                senha.isEmpty() || endereco.isEmpty() || numero.isEmpty() || cep.isEmpty()) {
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

    private Cliente montarClienteDosCampos() {
        String nome = etNome.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();
        String cpf = etCpf.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();
        String endereco = etEndereco.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String cep = etCep.getText().toString().trim();

        if (!validarCampos(nome, telefone, cpf, email, senha, endereco, numero, cep)) return null;

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        String usuarioJson = prefs.getString("usuario_logado", null);

        int usuarioId = -1;
        if (usuarioJson != null) {
            Gson gson = new Gson();
            Usuario usuarioLogado = gson.fromJson(usuarioJson, Usuario.class);
            usuarioId = usuarioLogado.getId();
        }

        Cliente c = new Cliente(nome, telefone, cpf, email, senha, endereco, numero, cep, usuarioId);
        return c;
    }

    private void salvarOuAlterarCliente() {
        Cliente cliente = montarClienteDosCampos();
        if (cliente == null) return;

        ApiService apiService = ApiClient.getApiService();

        if (clienteIdAtual != -1) {
            // --- UPDATE ---
            if (bitmapFoto != null) {
                RequestBody nomePart = RequestBody.create(cliente.getNome(), MediaType.parse("text/plain"));
                RequestBody telefonePart = RequestBody.create(cliente.getTelefone(), MediaType.parse("text/plain"));
                RequestBody cpfPart = RequestBody.create(cliente.getCpf(), MediaType.parse("text/plain"));
                RequestBody emailPart = RequestBody.create(cliente.getEmail(), MediaType.parse("text/plain"));
                RequestBody senhaPart = RequestBody.create(cliente.getSenha(), MediaType.parse("text/plain"));
                RequestBody enderecoPart = RequestBody.create(cliente.getEndereco(), MediaType.parse("text/plain"));
                RequestBody numeroPart = RequestBody.create(cliente.getNumero(), MediaType.parse("text/plain"));
                RequestBody cepPart = RequestBody.create(cliente.getCep(), MediaType.parse("text/plain"));
                MultipartBody.Part fotoPart = Util.createMultipartFromBitmap(bitmapFoto, "foto");

                apiService.atualizarClienteComFoto(clienteIdAtual, nomePart, telefonePart, cpfPart, emailPart, senhaPart,
                                enderecoPart, numeroPart, cepPart, fotoPart)
                        .enqueue(new Callback<Cliente>() {
                            @Override
                            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(CadastroClienteActivity.this, "Cliente alterado com sucesso!", Toast.LENGTH_SHORT).show();
                                    limparCampos();
                                }
                            }

                            @Override
                            public void onFailure(Call<Cliente> call, Throwable t) {
                                Toast.makeText(CadastroClienteActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                apiService.atualizarCliente(clienteIdAtual, cliente).enqueue(new Callback<Cliente>() {
                    @Override
                    public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CadastroClienteActivity.this, "Cliente alterado com sucesso!", Toast.LENGTH_SHORT).show();
                            limparCampos();
                        }
                    }

                    @Override
                    public void onFailure(Call<Cliente> call, Throwable t) {
                        Toast.makeText(CadastroClienteActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            // --- CREATE ---
            if (bitmapFoto != null) {
                RequestBody nomePart = RequestBody.create(cliente.getNome(), MediaType.parse("text/plain"));
                RequestBody telefonePart = RequestBody.create(cliente.getTelefone(), MediaType.parse("text/plain"));
                RequestBody cpfPart = RequestBody.create(cliente.getCpf(), MediaType.parse("text/plain"));
                RequestBody emailPart = RequestBody.create(cliente.getEmail(), MediaType.parse("text/plain"));
                RequestBody senhaPart = RequestBody.create(cliente.getSenha(), MediaType.parse("text/plain"));
                RequestBody enderecoPart = RequestBody.create(cliente.getEndereco(), MediaType.parse("text/plain"));
                RequestBody numeroPart = RequestBody.create(cliente.getNumero(), MediaType.parse("text/plain"));
                RequestBody cepPart = RequestBody.create(cliente.getCep(), MediaType.parse("text/plain"));
                MultipartBody.Part fotoPart = Util.createMultipartFromBitmap(bitmapFoto, "foto");

                apiService.criarClienteComFoto(nomePart, telefonePart, cpfPart, emailPart, senhaPart,
                                enderecoPart, numeroPart, cepPart, fotoPart)
                        .enqueue(new Callback<Cliente>() {
                            @Override
                            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(CadastroClienteActivity.this, "Cliente salvo com sucesso!", Toast.LENGTH_SHORT).show();
                                    limparCampos();
                                }
                            }

                            @Override
                            public void onFailure(Call<Cliente> call, Throwable t) {
                                Toast.makeText(CadastroClienteActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                apiService.criarCliente(cliente).enqueue(new Callback<Cliente>() {
                    @Override
                    public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CadastroClienteActivity.this, "Cliente salvo com sucesso!", Toast.LENGTH_SHORT).show();
                            limparCampos();
                        }
                    }

                    @Override
                    public void onFailure(Call<Cliente> call, Throwable t) {
                        Toast.makeText(CadastroClienteActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    // --- Métodos de busca, alteração e exclusão ---
    private void abrirDialogBuscarCliente() {
        final EditText input = new EditText(this);
        input.setHint("Digite o nome do cliente");
        new AlertDialog.Builder(this)
                .setTitle("Alterar Cliente")
                .setView(input)
                .setPositiveButton("Buscar", (dialog, which) -> {
                    String nome = input.getText().toString().trim();
                    if (!nome.isEmpty()) buscarClientePorNome(nome);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void buscarClientePorNome(String nome) {
        ApiService apiService = ApiClient.getApiService();
        apiService.buscarClientePorNome(nome).enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Cliente c = response.body().get(0);
                    preencherCamposParaEdicao(c);
                } else {
                    Toast.makeText(CadastroClienteActivity.this, "Cliente não encontrado.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Toast.makeText(CadastroClienteActivity.this, "Falha na busca: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherCamposParaEdicao(Cliente c) {
        etNome.setText(c.getNome());
        etTelefone.setText(c.getTelefone());
        etCpf.setText(c.getCpf());
        etEmail.setText(c.getEmail());
        etSenha.setText(c.getSenha());
        etEndereco.setText(c.getEndereco());
        etNumero.setText(c.getNumero());
        etCep.setText(c.getCep());
        clienteIdAtual = c.getId();
    }

    private void abrirDialogExcluirCliente() {
        final EditText input = new EditText(this);
        input.setHint("Digite o nome do cliente");
        new AlertDialog.Builder(this)
                .setTitle("Excluir Cliente")
                .setView(input)
                .setPositiveButton("Excluir", (dialog, which) -> {
                    String nome = input.getText().toString().trim();
                    if (!nome.isEmpty()) buscarClienteParaExclusao(nome);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void buscarClienteParaExclusao(String nome) {
        ApiService apiService = ApiClient.getApiService();
        apiService.buscarClientePorNome(nome).enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Cliente c = response.body().get(0);
                    new AlertDialog.Builder(CadastroClienteActivity.this)
                            .setTitle("Confirmação")
                            .setMessage("Deseja realmente excluir o cliente " + c.getNome() + "?")
                            .setPositiveButton("Sim", (d, w) -> excluirCliente(c.getId()))
                            .setNegativeButton("Não", null)
                            .show();
                } else {
                    Toast.makeText(CadastroClienteActivity.this, "Cliente não encontrado.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Toast.makeText(CadastroClienteActivity.this, "Falha na busca: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void excluirCliente(int id) {
        ApiService apiService = ApiClient.getApiService();
        apiService.deletarCliente(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CadastroClienteActivity.this, "Cliente excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    limparCampos();
                } else {
                    Toast.makeText(CadastroClienteActivity.this, "Erro ao excluir cliente", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CadastroClienteActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limparCampos() {
        etNome.setText("");
        etTelefone.setText("");
        etCpf.setText("");
        etEmail.setText("");
        etSenha.setText("");
        etEndereco.setText("");
        etNumero.setText("");
        etCep.setText("");
        imgFotoUsuario.setImageResource(R.drawable.ic_launcher_foreground);
        bitmapFoto = null;
        imageUri = null;
        clienteIdAtual = -1;
    }
}
