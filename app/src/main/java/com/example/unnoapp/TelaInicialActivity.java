package com.example.unnoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.unnoapp.modelo.Usuario;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

public class TelaInicialActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private CardView cardCadastroCliente, cardCadastroProfissional, cardSelecaoEspecialidades;

    private static final String PREFS_NAME = "APP_PREFS";
    private static final String KEY_USUARIO = "usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        // Recupera usuário logado
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usuarioJson = prefs.getString(KEY_USUARIO, null);
        Usuario usuarioLogado = null;
        if (usuarioJson != null) {
            usuarioLogado = new Gson().fromJson(usuarioJson, Usuario.class);
        }
        if (usuarioLogado != null) {
            Toast.makeText(this, "Bem-vindo " + usuarioLogado.getNome(), Toast.LENGTH_SHORT).show();
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerLayout e NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Cabeçalho do drawer
        View headerView = navigationView.getHeaderView(0);
        TextView tvNomeUsuarioDrawer = headerView.findViewById(R.id.tvNomeUsuarioDrawer);
        if (usuarioLogado != null) {
            tvNomeUsuarioDrawer.setText("Bem-vindo, " + usuarioLogado.getNome());
        }

        // Toggle do drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Itens do drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(TelaInicialActivity.this, CadastroUsuarioActivity.class));
            } else if (id == R.id.nav_empresa) {
                startActivity(new Intent(TelaInicialActivity.this, EmpresaActivity.class));
            } else if (id == R.id.nav_instagram) {
                abrirInstagram("seu_usuario");
            } else if (id == R.id.nav_logout) {
                logout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Inicializa cards
        cardCadastroCliente = findViewById(R.id.cardCliente);
        cardCadastroProfissional = findViewById(R.id.cardProfissional);
        cardSelecaoEspecialidades = findViewById(R.id.cardEspecialidades);

        // Clique nos cards
        cardCadastroCliente.setOnClickListener(v -> startActivity(new Intent(TelaInicialActivity.this, CadastroClienteActivity.class)));
        cardCadastroProfissional.setOnClickListener(v -> startActivity(new Intent(TelaInicialActivity.this, CadastroProfissionalActivity.class)));
        cardSelecaoEspecialidades.setOnClickListener(v -> startActivity(new Intent(TelaInicialActivity.this, SelecaoEspecialidadesActivity.class)));
    }

    private void abrirInstagram(String usuario) {
        String url = "https://www.instagram.com/" + usuario + "/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.instagram.android");
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove(KEY_USUARIO);
        editor.apply();
        startActivity(new Intent(TelaInicialActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
