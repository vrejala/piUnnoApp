package com.example.unnoapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unnoapp.modelo.Profissional;
import com.example.unnoapp.repositorio.implementacao.ProfissionalRepository;
import com.example.unnoapp.repositorio.contrato.IProfissionalRepositorio;

import java.util.List;

public class ListaProfissionaisActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfissionalAdapter adapter;
    private IProfissionalRepositorio repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_profissionais);

        String especialidade = getIntent().getStringExtra("especialidade");

        TextView titulo = findViewById(R.id.txtTitulo);
        titulo.setText("Profissionais em " + especialidade);

        recyclerView = findViewById(R.id.recyclerViewProfissionais);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Criando o repositório
        repository = new ProfissionalRepository();

        // Buscando profissionais filtrados
        List<Profissional> profissionais = repository.getProfissionaisPorEspecialidade(especialidade);

        adapter = new ProfissionalAdapter(this, profissionais);
        recyclerView.setAdapter(adapter);
    }
}
