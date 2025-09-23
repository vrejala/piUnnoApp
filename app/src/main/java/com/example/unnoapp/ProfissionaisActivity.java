package com.example.unnoapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unnoapp.modelo.Profissional;

import java.util.ArrayList;
import java.util.List;

public class ProfissionaisActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfissionalAdapter adapter;
    private List<Profissional> listaProfissionais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_profissionais); // seu layout real

        String especialidade = getIntent().getStringExtra("especialidade");

        TextView titulo = findViewById(R.id.txtTitulo);
        titulo.setText("Profissionais em " + especialidade);

        recyclerView = findViewById(R.id.recyclerViewProfissionais);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Criar dados fake para testar
        listaProfissionais = new ArrayList<>();
        listaProfissionais.add(new Profissional(
                "João Silva",
                "joao@email.com",
                "(11) 99999-1111",
                "Psicanálise",
                "120",
                "",
                "Psicólogo"
        ));

        listaProfissionais.add(new Profissional(
                "Maria Costa",
                "maria@email.com",
                "(21) 98888-2222",
                "Terapia Cognitiva",
                "150",
                "",
                "Psicóloga"
        ));

        listaProfissionais.add(new Profissional(
                "Carlos Souza",
                "carlos@email.com",
                "(31) 97777-3333",
                "Nutrição Clínica",
                "100",
                "",
                "Nutricionista"
        ));

        // Passar para o Adapter
        adapter = new ProfissionalAdapter(this, listaProfissionais);
        recyclerView.setAdapter(adapter);
    }
}
