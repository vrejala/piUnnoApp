package com.example.unnoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unnoapp.modelo.Profissional;
import com.example.unnoapp.modelo.Usuario;
import com.example.unnoapp.util.ApiClient;
import com.example.unnoapp.util.ApiService;
import com.example.unnoapp.util.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroProfissionalActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private ImageView imgFotoProfissional;
    private Button btnFoto, btnSalvar, btnAlterar, btnExcluir;
    private EditText etNome, etCadastroProfissional, etEmail, etTelefone, etSobre, etValor;
    private Spinner spEspecialidade, spAbordagem, spTipoPagamento;
    private Switch swStatus;
    private int profissionalIdAtual = -1; // se -1, é novo; senão, é edição

    private Uri imageUri;
    private Bitmap bitmapFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_profissional);

        // Bind views
        imgFotoProfissional = findViewById(R.id.imgFotoProfissional);
        btnFoto = findViewById(R.id.btnFoto);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnAlterar = findViewById(R.id.btnAlterar);

        etNome = findViewById(R.id.etNome);
        etCadastroProfissional = findViewById(R.id.etCadastroProfissional);
        etEmail = findViewById(R.id.etEmail);
        etTelefone = findViewById(R.id.etTelefone);
        etSobre = findViewById(R.id.etSobre);
        etValor = findViewById(R.id.etValor);
        spEspecialidade = findViewById(R.id.spEspecialidade);
        spAbordagem = findViewById(R.id.spAbordagem);
        spTipoPagamento = findViewById(R.id.spTipoPagamento);
        swStatus = findViewById(R.id.swStatus);

        etValor.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;
                try {
                    String clean = s.toString().replaceAll("[R$,.\\s]", "");
                    double parsed = clean.isEmpty() ? 0 : Double.parseDouble(clean) / 100.0;
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
                    DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
                    String formatted = df.format(parsed);
                    etValor.setText(formatted);
                    etValor.setSelection(formatted.length());
                } catch (NumberFormatException e) {
                    etValor.setText("0,00");
                    etValor.setSelection(4);
                }
                isUpdating = false;
            }
        });

        btnFoto.setOnClickListener(v -> escolherFoto());
        btnSalvar.setOnClickListener(v -> salvarOuAlterarProfissional());
        btnAlterar.setOnClickListener(v -> abrirDialogBuscarProfissional());
        btnExcluir = findViewById(R.id.btnExcluir);
        btnExcluir.setOnClickListener(v -> abrirDialogExcluirProfissional());
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
                    imgFotoProfissional.setImageBitmap(bitmapFoto);
                } else if (requestCode == CAMERA_REQUEST && data != null) {
                    bitmapFoto = (Bitmap) data.getExtras().get("data");
                    imgFotoProfissional.setImageBitmap(bitmapFoto);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validarCampos(String nome, String cadastro, String email, String telefone, String sobre, String valor) {
        if (nome.isEmpty() || cadastro.isEmpty() || email.isEmpty() || telefone.isEmpty() || sobre.isEmpty() || valor.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
            return false;
        }
        return true;
    }

    private Profissional montarProfissionalDosCampos() {
        String nome = etNome.getText().toString().trim();
        String cadastro = etCadastroProfissional.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();
        String sobre = etSobre.getText().toString().trim();
        String valorStr = etValor.getText().toString().trim();
        String especialidade = spEspecialidade.getSelectedItem().toString();
        String abordagem = spAbordagem.getSelectedItem().toString();
        String tipoPagamento = spTipoPagamento.getSelectedItem().toString();
        boolean ativo = swStatus.isChecked();

        if (!validarCampos(nome, cadastro, email, telefone, sobre, valorStr)) return null;

        float valor;
        try {
            valor = Float.parseFloat(valorStr.replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            etValor.setError("Valor inválido");
            etValor.requestFocus();
            return null;
        }

        Profissional p = new Profissional(nome, cadastro, email, telefone, valor, sobre, especialidade, abordagem, tipoPagamento);
        p.setAtivo(ativo);
        return p;
    }

    private void salvarOuAlterarProfissional() {
        Profissional profissional = montarProfissionalDosCampos();
        if (profissional == null) return;

        ApiService apiService = ApiClient.getApiService();

        if (profissionalIdAtual != -1) {
            // --- UPDATE ---
            if (bitmapFoto != null) {
                Toast.makeText(this, "Alteração com foto não suportada no momento.", Toast.LENGTH_LONG).show();
                return;
            }
            apiService.atualizarProfissional(profissionalIdAtual, profissional)
                    .enqueue(new Callback<Profissional>() {
                        @Override
                        public void onResponse(Call<Profissional> call, Response<Profissional> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(CadastroProfissionalActivity.this, "Alterado com sucesso!", Toast.LENGTH_SHORT).show();
                                profissionalIdAtual = -1;
                                limparCampos();
                            } else {
                                Toast.makeText(CadastroProfissionalActivity.this, "Erro ao alterar: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Profissional> call, Throwable t) {
                            Toast.makeText(CadastroProfissionalActivity.this, "Falha ao alterar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // --- CREATE ---
            if (bitmapFoto != null) {
                RequestBody nomePart = RequestBody.create(profissional.getNome(), okhttp3.MediaType.parse("text/plain"));
                RequestBody cadastroPart = RequestBody.create(profissional.getCadastroprofissional(), okhttp3.MediaType.parse("text/plain"));
                RequestBody emailPart = RequestBody.create(profissional.getEmail(), okhttp3.MediaType.parse("text/plain"));
                RequestBody telefonePart = RequestBody.create(profissional.getTelefone(), okhttp3.MediaType.parse("text/plain"));
                RequestBody valorPart = RequestBody.create(String.valueOf(profissional.getValor()), okhttp3.MediaType.parse("text/plain"));
                RequestBody sobrePart = RequestBody.create(profissional.getSobre(), okhttp3.MediaType.parse("text/plain"));
                RequestBody especialidadePart = RequestBody.create(profissional.getEspecialidade(), okhttp3.MediaType.parse("text/plain"));
                RequestBody abordagemPart = RequestBody.create(profissional.getAbordagem(), okhttp3.MediaType.parse("text/plain"));
                RequestBody tipoPagamentoPart = RequestBody.create(profissional.getTipopagamento(), okhttp3.MediaType.parse("text/plain"));
                RequestBody ativoPart = RequestBody.create(String.valueOf(swStatus.isChecked()), okhttp3.MediaType.parse("text/plain"));

                MultipartBody.Part fotoPart = Util.createMultipartFromBitmap(bitmapFoto, "foto");

                apiService.criarProfissionalComFoto(nomePart, cadastroPart, emailPart, telefonePart, valorPart, sobrePart,
                                especialidadePart, abordagemPart, tipoPagamentoPart, ativoPart, fotoPart)
                        .enqueue(new Callback<Profissional>() {
                            @Override
                            public void onResponse(Call<Profissional> call, Response<Profissional> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(CadastroProfissionalActivity.this, "Profissional salvo com sucesso!", Toast.LENGTH_SHORT).show();
                                    limparCampos();
                                }  else {
                                    try {
                                        // Lê o corpo de erro
                                        String errorMsg = response.errorBody().string();

                                        // Se o servidor retorna JSON {"detail":"CPF já cadastrado"}
                                        JSONObject json = new JSONObject(errorMsg);
                                        String mensagem = json.getString("detail");

                                        // Mostra em um Toast
                                        Toast.makeText(CadastroProfissionalActivity.this, mensagem, Toast.LENGTH_LONG).show();

                                        // Opcional: mostra em um AlertDialog

                                        new AlertDialog.Builder(CadastroProfissionalActivity.this)
                                                .setTitle("Erro ao salvar profissional")
                                                .setMessage(mensagem)
                                                .setPositiveButton("OK", null)
                                                .show();

                                        Log.e("CadastroProfissional", "Erro body: " + errorMsg);

                                    } catch (Exception e) { // IOException ou JSONException
                                        e.printStackTrace();
                                        Toast.makeText(CadastroProfissionalActivity.this, "Erro desconhecido", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Profissional> call, Throwable t) {
                                Toast.makeText(CadastroProfissionalActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                apiService.criarProfissional(profissional)
                        .enqueue(new Callback<Profissional>() {
                            @Override
                            public void onResponse(Call<Profissional> call, Response<Profissional> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(CadastroProfissionalActivity.this, "Profissional salvo com sucesso!", Toast.LENGTH_SHORT).show();
                                    limparCampos();
                                } try {
                                    // Lê o corpo de erro
                                    String errorMsg = response.errorBody().string();

                                    // Se o servidor retorna JSON {"detail":"CPF já cadastrado"}

                                    JSONObject json = new JSONObject(errorMsg);
                                    String mensagem = json.getString("detail");

                                    // Mostra em um Toast
                                    Toast.makeText(CadastroProfissionalActivity.this, mensagem, Toast.LENGTH_LONG).show();

                                    // Opcional: mostra em um AlertDialog
                            /*
                            new AlertDialog.Builder(CadastroUsuarioActivity.this)
                                    .setTitle("Erro ao salvar usuário")
                                    .setMessage(mensagem)
                                    .setPositiveButton("OK", null)
                                    .show();

                             */

                                    Log.e("CadastroProfissional", "Erro body: " + errorMsg);

                                } catch (Exception e) { // IOException ou JSONException
                                    e.printStackTrace();
                                    Toast.makeText(CadastroProfissionalActivity.this, "Erro desconhecido", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Profissional> call, Throwable t) {
                                Toast.makeText(CadastroProfissionalActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    // --- NOVO: Caixa de diálogo para buscar profissional pelo nome ---
    private void abrirDialogBuscarProfissional() {
        final EditText input = new EditText(this);
        input.setHint("Digite o nome do profissional");

        new AlertDialog.Builder(this)
                .setTitle("Alterar Profissional")
                .setView(input)
                .setPositiveButton("Buscar", (dialog, which) -> {
                    String nome = input.getText().toString().trim();
                    if (!nome.isEmpty()) {
                        buscarProfissionalPorNome(nome);
                    } else {
                        Toast.makeText(this, "Informe um nome válido!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void buscarProfissionalPorNome(String nome) {
        ApiService apiService = ApiClient.getApiService();
        apiService.buscarPorNome(nome).enqueue(new Callback<List<Profissional>>() {
            @Override
            public void onResponse(Call<List<Profissional>> call, Response<List<Profissional>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Profissional p = response.body().get(0);
                    preencherCamposParaEdicao(p);
                } else {
                    Toast.makeText(CadastroProfissionalActivity.this, "Profissional não encontrado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Profissional>> call, Throwable t) {
                Toast.makeText(CadastroProfissionalActivity.this, "Falha na busca: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherCamposParaEdicao(Profissional p) {
        etNome.setText(p.getNome());
        etCadastroProfissional.setText(p.getCadastroprofissional());
        etEmail.setText(p.getEmail());
        etTelefone.setText(p.getTelefone());
        etSobre.setText(p.getSobre());
        etValor.setText(String.format(Locale.getDefault(), "%.2f", p.getValor()).replace(".", ","));

        spEspecialidade.setSelection(getSpinnerIndex(spEspecialidade, p.getEspecialidade()));
        spAbordagem.setSelection(getSpinnerIndex(spAbordagem, p.getAbordagem()));
        spTipoPagamento.setSelection(getSpinnerIndex(spTipoPagamento, p.getTipopagamento()));

        // 1️⃣ Setar o estado inicial do Switch conforme o profissional
        swStatus.setChecked(p.getAtivo());

// 2️⃣ Listener para atualizar o objeto Profissional quando o usuário mudar
        swStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            p.setAtivo(isChecked);
        });



        // Se tiver URL de foto, pode carregar com Glide ou Picasso
        // Glide.with(this).load(p.getFotoUrl()).into(imgFotoProfissional);

        profissionalIdAtual = p.getId(); // marca que estamos em edição
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) return i;
        }
        return 0;
    }

    private void abrirDialogExcluirProfissional() {
        final EditText input = new EditText(this);
        input.setHint("Digite o nome do Profissional");

        new AlertDialog.Builder(this)
                .setTitle("Excluir Profissional")
                .setView(input)
                .setPositiveButton("Excluir", (dialog, which) -> {
                    String nome = input.getText().toString().trim();
                    if (!nome.isEmpty()) {
                        buscarProfissionalParaExclusao(nome);
                    } else {
                        Toast.makeText(this, "Informe um nome válido!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void buscarProfissionalParaExclusao(String nome) {
        ApiService apiService = ApiClient.getApiService();
        apiService.buscarPorNome(nome).enqueue(new Callback<List<Profissional>>() {
            @Override
            public void onResponse(Call<List<Profissional>> call, Response<List<Profissional>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Profissional u = response.body().get(0);

                    new AlertDialog.Builder(CadastroProfissionalActivity.this)
                            .setTitle("Confirmação")
                            .setMessage("Deseja realmente excluir o Profissional " + u.getNome() + "?")
                            .setPositiveButton("Sim", (d, w) -> excluirProfissional(u.getId()))
                            .setNegativeButton("Não", null)
                            .show();

                } else {
                    Toast.makeText(CadastroProfissionalActivity.this, "Profissional não encontrado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Profissional>> call, Throwable t) {
                Toast.makeText(CadastroProfissionalActivity.this, "Falha na busca: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void excluirProfissional(int id) {
        ApiService apiService = ApiClient.getApiService();
        apiService.deletarProfissional(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CadastroProfissionalActivity.this, "Profissional excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    limparCampos();
                } else {
                    Toast.makeText(CadastroProfissionalActivity.this, "Erro ao excluir profissional", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CadastroProfissionalActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limparCampos() {
        etNome.setText("");
        etCadastroProfissional.setText("");
        etEmail.setText("");
        etTelefone.setText("");
        etSobre.setText("");
        etValor.setText("");

        spEspecialidade.setSelection(0);
        spAbordagem.setSelection(0);
        spTipoPagamento.setSelection(0);

        imgFotoProfissional.setImageResource(R.drawable.ic_launcher_foreground);
        bitmapFoto = null;
        imageUri = null;

        profissionalIdAtual = -1;
    }
}
