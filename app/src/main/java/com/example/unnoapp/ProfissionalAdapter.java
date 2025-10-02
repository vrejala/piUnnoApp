package com.example.unnoapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unnoapp.modelo.Profissional;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ProfissionalAdapter extends RecyclerView.Adapter<ProfissionalAdapter.ViewHolder> {

    private Context context;
    private List<Profissional> listaProfissionais;
    private List<Profissional> listaProfissionaisCompleta;

    public ProfissionalAdapter(Context context, List<Profissional> listaProfissionais) {
        this.context = context;
        // Cria cópia da lista e ordena alfabeticamente pelo nome
        Collections.sort(listaProfissionais, Comparator.comparing(Profissional::getNome, String.CASE_INSENSITIVE_ORDER));
        this.listaProfissionais = listaProfissionais;
        this.listaProfissionaisCompleta = new ArrayList<>(listaProfissionais);
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
        holder.txtCastroprofissional.setText(prof.getCadastroprofissional());
        holder.txtTelefone.setText(prof.getTelefone());
        holder.txtEspecialidade.setText(prof.getEspecialidade());
        holder.txtAbordagem.setText("Abordagem: " + prof.getAbordagem());
        // Formata o valor para R$ xx,xx
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        holder.txtValor.setText("R$ " + df.format(prof.getValor()));
        holder.txtSobre.setText("Sobre mim:\n " + prof.getSobre());

        // Opcional: manter estado expandido mesmo ao rolar a lista
        holder.expandableLayout.setVisibility(holder.isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return listaProfissionais.size();
    }

      // 🔎 Método para filtrar
    public void filtrar(String texto) {
        listaProfissionais.clear();
        if (texto.isEmpty()) {
            listaProfissionais.addAll(listaProfissionaisCompleta);
        } else {
            String filtro = texto.toLowerCase(Locale.getDefault());
            for (Profissional p : listaProfissionaisCompleta) {
                if (p.getNome().toLowerCase(Locale.getDefault()).contains(filtro)) {
                    listaProfissionais.add(p);
                }
            }
        }

        // Ordena os resultados pelo nome
        Collections.sort(listaProfissionais, Comparator.comparing(Profissional::getNome, String.CASE_INSENSITIVE_ORDER));

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfissional, btnWhatsapp;
        TextView txtNome, txtCastroprofissional, txtTelefone, txtEspecialidade, txtAbordagem, txtValor, txtSobre;
        LinearLayout expandableLayout;
        CardView cardView;
        boolean isExpanded = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfissional = itemView.findViewById(R.id.imgProfissional);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtCastroprofissional = itemView.findViewById(R.id.txtCadastroprofissional);
            txtTelefone = itemView.findViewById(R.id.txtTelefone);
            txtEspecialidade = itemView.findViewById(R.id.txtEspecialidade);
            txtAbordagem = itemView.findViewById(R.id.txtAbordagem);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtSobre = itemView.findViewById(R.id.txtSobre);
            btnWhatsapp = itemView.findViewById(R.id.btnWhatsapp);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            cardView = itemView.findViewById(R.id.cardView);

            // Clique no botão WhatsApp
            btnWhatsapp.setOnClickListener(v -> {
                String telefone = txtTelefone.getText().toString().replaceAll("[^0-9]", "");
                String mensagem = "Olá! Vi seu contato no aplicativo e gostaria de saber mais informações, por favor. \uD83D\uDE42";

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://wa.me/55" + telefone + "?text=" + Uri.encode(mensagem)));
                    v.getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "WhatsApp não instalado.", Toast.LENGTH_SHORT).show();
                }
            });

            // Clique no card para expandir/colapsar
            cardView.setOnClickListener(v -> {
                if (expandableLayout.getVisibility() == View.GONE) {
                    expandableLayout.setVisibility(View.VISIBLE);
                } else {
                    expandableLayout.setVisibility(View.GONE);
                }
            });
        }

        // Função para expandir
        private void expand(final View view) {
            view.setVisibility(View.VISIBLE);
            view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final int targetHeight = view.getMeasuredHeight();

            ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
            animator.addUpdateListener(animation -> {
                view.getLayoutParams().height = (int) animation.getAnimatedValue();
                view.requestLayout();
            });
            animator.setDuration(300);
            animator.start();
        }

        // Função para colapsar
        private void collapse(final View view) {
            final int initialHeight = view.getMeasuredHeight();

            ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
            animator.addUpdateListener(animation -> {
                view.getLayoutParams().height = (int) animation.getAnimatedValue();
                view.requestLayout();
                if ((int) animation.getAnimatedValue() == 0) {
                    view.setVisibility(View.GONE);
                }
            });
            animator.setDuration(300);
            animator.start();
        }
    }
}
