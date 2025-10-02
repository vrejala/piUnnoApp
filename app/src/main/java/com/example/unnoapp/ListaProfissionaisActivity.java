package com.example.unnoapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unnoapp.modelo.Profissional;
import com.example.unnoapp.repositorio.implementacao.ProfissionalRepository;
import com.example.unnoapp.repositorio.contrato.IProfissionalRepositorio;
import com.example.unnoapp.util.ApiClient;

import java.util.List;

public class ListaProfissionaisActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfissionalAdapter adapter;
    private IProfissionalRepositorio repository;
    private EditText edtPesquisa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_profissionais);

        String especialidade = getIntent().getStringExtra("especialidade");

        edtPesquisa = findViewById(R.id.edtPesquisa);
        TextView titulo = findViewById(R.id.txtTitulo);
        titulo.setText("Profissionais em " + especialidade);

        recyclerView = findViewById(R.id.recyclerViewProfissionais);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Criando o repositório
        repository = new ProfissionalRepository(ApiClient.getApiService());

        // Buscando profissionais filtrados
        repository.getProfissionaisPorEspecialidade(especialidade)
                .observe(this, profissionais -> {
                    if (profissionais != null) {
                        adapter = new ProfissionalAdapter(this, profissionais);
                        recyclerView.setAdapter(adapter);

                        // 🔎 pesquisa em tempo real
                        edtPesquisa.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter.filtrar(s.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });

                    } else {
                        Log.e("ListaProfissionais", "Erro ao carregar profissionais ou lista vazia");
                    }
                });


    }
}
