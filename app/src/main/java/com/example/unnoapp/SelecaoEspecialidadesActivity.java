package com.example.unnoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;


public class SelecaoEspecialidadesActivity extends AppCompatActivity {

    CardView cardPsicologo, cardNutricionista;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_especialidades);

        cardPsicologo = findViewById(R.id.cardPsicologo);
        cardNutricionista = findViewById(R.id.cardNutricionista);

        cardPsicologo.setOnClickListener(v -> {
            Intent intent = new Intent(SelecaoEspecialidadesActivity.this, ListaProfissionaisActivity.class);
            intent.putExtra("especialidade", "Psicologo");
            startActivity(intent);
        });

        cardNutricionista.setOnClickListener(v -> {
            Intent intent = new Intent(SelecaoEspecialidadesActivity.this, ListaProfissionaisActivity.class);
            intent.putExtra("especialidade", "Nutricionista");
            startActivity(intent);
        });
    }
}