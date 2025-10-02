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
                "99999",
                "joao@gmail.com",
                "(11) 99999-1111",
                120f,
                "Sou psicólogo(a) dedicado(a) a ajudar pessoas a compreenderem suas emoções, superarem desafios e desenvolverem uma vida mais equilibrada e saudável.",
                "Psicologo",
                "Psicanálise",
                ""
        ));

        listaProfissionais.add(new Profissional(
                "Maria Costa",
                "98888",
                "maria@gmail.com",
                "(11) 99998-1111",
                150f,
                "Sou psicólogo(a) acredito na importância da escuta empática, do acolhimento e de um espaço seguro para que cada indivíduo possa se expressar e encontrar caminhos para o autoconhecimento",
                "Psicologo",
                "Terapia Cognitiva",
                ""
        ));

        listaProfissionais.add(new Profissional(
                "Carlos Souza",
                "97777",
                "carlos@gmail.com",
                "(71) 98803-1111",
                100f,
                "Sou nutricionista apaixonado(a) por promover saúde e bem-estar por meio da alimentação equilibrada e consciente.",
                "Nutricionista",
                "Nutrição Clínica",
                ""
        ));

        // Passar para o Adapter
        adapter = new ProfissionalAdapter(this, listaProfissionais);
        recyclerView.setAdapter(adapter);
    }
}
