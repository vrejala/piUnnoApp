package com.example.unnoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.unnoapp.modelo.Usuario;
import com.google.gson.Gson;

public class TelaInicialActivity extends AppCompatActivity {

    CardView cardCadastroUsuario, cardCadastroCliente, cardCadastroProfissional, cardSelecaoEspecialidades;

    private static final String PREFS_NAME = "APP_PREFS";
    private static final String KEY_USUARIO = "usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        // Recuperar usuário logado
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usuarioJson = prefs.getString(KEY_USUARIO, null);
        Usuario usuarioLogado = null;
        if (usuarioJson != null) {
            usuarioLogado = new Gson().fromJson(usuarioJson, Usuario.class);
        }

        // Exemplo: mostrar Toast com o nome
        if (usuarioLogado != null) {
            Toast.makeText(this, "Bem-vindo " + usuarioLogado.getNome(), Toast.LENGTH_SHORT).show();
        }


        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Define o ícone de usuário à esquerda
        toolbar.setNavigationIcon(R.drawable.ic_gerenciar_preto);

        // Clique no ícone de usuário
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(TelaInicialActivity.this, CadastroUsuarioActivity.class);
            intent.putExtra("CadastroUsuario", "Usuario");
            intent.putExtra("modoEdicao", true);
            startActivity(intent);
        });


        // Cards
        cardCadastroCliente = findViewById(R.id.cardCliente);
        cardCadastroProfissional = findViewById(R.id.cardProfissional);
        cardSelecaoEspecialidades = findViewById(R.id.cardEspecialidades);

        // Clique nos cards
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
        int itemId = item.getItemId(); // <-- declare a variável

        if (itemId == R.id.action_logout) {
            // Logout
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.remove(KEY_USUARIO);
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;

        } else if (itemId == R.id.action_rede) {
            // Instagram
            String instagramUrl = "https://www.instagram.com/seu_usuario/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl));
            intent.setPackage("com.instagram.android");

            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl));
                startActivity(webIntent);
            }
            return true;

        } else if (itemId == R.id.action_empresa) {
            // Abre a tela com os dados da empresa
            Intent intent = new Intent(this, EmpresaActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
