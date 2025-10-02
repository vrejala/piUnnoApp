package com.example.unnoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

public class TelaInicialActivity extends AppCompatActivity {

    CardView cardCadastroUsuario, cardCadastroCliente, cardCadastroProfissional, cardSelecaoEspecialidades;

    private static final String PREFS_NAME = "APP_PREFS";
    private static final String KEY_USUARIO = "usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Cards
        cardCadastroUsuario = findViewById(R.id.cardUsuario);
        cardCadastroCliente = findViewById(R.id.cardCliente);
        cardCadastroProfissional = findViewById(R.id.cardProfissional);
        cardSelecaoEspecialidades = findViewById(R.id.cardEspecialidades);

        // Clique nos cards
        cardCadastroUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(TelaInicialActivity.this, CadastroUsuarioActivity.class);
            intent.putExtra("CadastroUsuario", "Usuario");
            intent.putExtra("modoEdicao", true);
            startActivity(intent);
        });

        cardCadastroCliente.setOnClickListener(v -> {
            Intent intent = new Intent(TelaInicialActivity.this, CadastroClienteActivity.class);
            intent.putExtra("CadastroCliente", "Cliente");
            intent.putExtra("modoEdicao", true);
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

    // Inflar menu da Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // menu_main.xml
        return true;
    }

    // Ações do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Remove usuário logado
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.remove(KEY_USUARIO);
            editor.apply();

            // Volta para tela de login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
