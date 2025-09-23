package com.example.unnoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

public class TelaInicialActivity extends AppCompatActivity{

    CardView cardCadastroUsuario, cardCadastroProfissional, cardSelecaoEspecialidades;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        cardCadastroUsuario = findViewById(R.id.cardUsuario);
        cardCadastroProfissional = findViewById(R.id.cardProfissional);
        cardSelecaoEspecialidades = findViewById(R.id.cardEspecialidades);

        cardCadastroUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(TelaInicialActivity.this, CadastroUsuarioActivity.class);
            intent.putExtra("CadastroUsuario", "Usuário");
            intent.putExtra("modoEdicao", true); // importante: modo edição
            startActivity(intent);
        });

        cardCadastroProfissional.setOnClickListener(v -> {
            Intent intent = new Intent(TelaInicialActivity.this, CadastroProfissionalActivity.class);
            intent.putExtra("CastroProfissional", "Profissional");
            startActivity(intent);
                });

        cardSelecaoEspecialidades.setOnClickListener(v -> {
            Intent intent = new Intent(TelaInicialActivity.this, SelecaoEspecialidadesActivity.class);
            intent.putExtra("SelecaoEspecialidades", "Especialidades");
            startActivity(intent);
        });

    }
}