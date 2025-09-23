package com.example.unnoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CadastroProfissionalActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    ImageView imgFoto;
    Button btnFoto, btnSalvar, btnAtualizar, btnExcluir;
    EditText etNome, etCadastroProfissional, etEmail, etTelefone, etValor, etSobreMim;
    Spinner spEspecialidade, spAbordagem, spTipoPagamento;
    Switch swStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_profissional);

        // Bind
        imgFoto = findViewById(R.id.imgFoto);
        btnFoto = findViewById(R.id.btnFoto);
        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etTelefone = findViewById(R.id.etTelefone);
        etValor = findViewById(R.id.etValor);
        etCadastroProfissional = findViewById(R.id.etCadastroProfissional);
        etSobreMim = findViewById(R.id.etSobreMim);
        spEspecialidade = findViewById(R.id.spEspecialidade);
        spAbordagem = findViewById(R.id.spAbordagem);
        spTipoPagamento = findViewById(R.id.spTipoPagamento);
        swStatus = findViewById(R.id.swStatus);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnAtualizar = findViewById(R.id.btnAtualizar);
        btnExcluir = findViewById(R.id.btnExcluir);

        // --- Spinners com arrays do strings.xml ---
        ArrayAdapter<CharSequence> adapterEsp = ArrayAdapter.createFromResource(
                this,
                R.array.especialidade_array,
                android.R.layout.simple_spinner_item
        );
        adapterEsp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEspecialidade.setAdapter(adapterEsp);

        ArrayAdapter<CharSequence> adapterAbord = ArrayAdapter.createFromResource(
                this,
                R.array.abordagens_array,
                android.R.layout.simple_spinner_item
        );
        adapterAbord.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAbordagem.setAdapter(adapterAbord);

        ArrayAdapter<CharSequence> adapterPag = ArrayAdapter.createFromResource(
                this,
                R.array.pagamento_array,
                android.R.layout.simple_spinner_item
        );
        adapterPag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoPagamento.setAdapter(adapterPag);

        // --- Upload de Foto ---
        btnFoto.setOnClickListener(v -> {
            String[] options = {"Galeria", "Câmera"};
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Selecionar Foto")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // Abrir galeria
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
                        } else {
                            // Verificar permissão antes de abrir a câmera
                            checkCameraPermission();
                        }
                    }).show();
        });

        // --- Botões CRUD (simulação) ---
        btnSalvar.setOnClickListener(v -> Toast.makeText(this, "Profissional salvo!", Toast.LENGTH_SHORT).show());
        btnAtualizar.setOnClickListener(v -> Toast.makeText(this, "Profissional atualizado!", Toast.LENGTH_SHORT).show());
        btnExcluir.setOnClickListener(v -> Toast.makeText(this, "Profissional excluído!", Toast.LENGTH_SHORT).show());
    }

    // ---- Permissões de Câmera ----
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permissão da câmera negada!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    // ---- Resultado da Galeria ou Câmera ----
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                imgFoto.setImageURI(selectedImage);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgFoto.setImageBitmap(imageBitmap);
            }
        }
    }
}
