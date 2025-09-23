package com.example.unnoapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unnoapp.modelo.Profissional;

import java.util.List;

public class ProfissionalAdapter extends RecyclerView.Adapter<ProfissionalAdapter.ViewHolder> {

    private Context context;
    private List<Profissional> listaProfissionais;

    public ProfissionalAdapter(Context context, List<Profissional> listaProfissionais) {
        this.context = context;
        this.listaProfissionais = listaProfissionais;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_lista_profissionais, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profissional prof = listaProfissionais.get(position);

        holder.txtNome.setText(prof.getNome());
        holder.txtEmail.setText(prof.getEmail());
        holder.txtTelefone.setText(prof.getTelefone());
        holder.txtAbordagem.setText(prof.getAbordagem());
        holder.txtValor.setText("R$ " + prof.getValor());

        // Carregar imagem local (URI)
        String fotoUri = prof.getFotoUri();
        if (fotoUri != null && !fotoUri.isEmpty()) {
            holder.imgProfissional.setImageURI(Uri.parse(fotoUri));
        } else {
            holder.imgProfissional.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return listaProfissionais.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfissional, btnWhatsapp;
        TextView txtNome, txtEmail, txtTelefone, txtAbordagem, txtValor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfissional = itemView.findViewById(R.id.imgProfissional);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtTelefone = itemView.findViewById(R.id.txtTelefone);
            txtAbordagem = itemView.findViewById(R.id.txtAbordagem);
            txtValor = itemView.findViewById(R.id.txtValor);
            btnWhatsapp = itemView.findViewById(R.id.btnWhatsapp);

            // Clique no botão WhatsApp
            btnWhatsapp.setOnClickListener(v -> {
                String telefone = txtTelefone.getText().toString().replaceAll("[^0-9]", ""); // só números
                String mensagem = "Olá, gostaria de mais informações.";

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://wa.me/55" + telefone + "?text=" + Uri.encode(mensagem)));
                    v.getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "WhatsApp não instalado.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
