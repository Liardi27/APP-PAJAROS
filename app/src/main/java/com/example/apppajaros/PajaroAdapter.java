package com.example.apppajaros; // ¡Asegúrate de que este sea tu paquete real!

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PajaroAdapter extends RecyclerView.Adapter<PajaroAdapter.PajaroViewHolder> {

    private List<Pajaro> listaPajaros;

    public PajaroAdapter(List<Pajaro> listaPajaros) {
        this.listaPajaros = listaPajaros;
        ordenarPorFavoritos(); // Ordenamos la lista nada más cargarla por primera vez
    }

    @NonNull
    @Override
    public PajaroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bird_card, parent, false);
        return new PajaroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PajaroViewHolder holder, int position) {
        Pajaro pajaroActual = listaPajaros.get(position);

        // 1. Asignar los textos
        holder.tvNombre.setText(pajaroActual.getNombre());
        holder.tvDetalle.setText(pajaroActual.getDescripcion());

        // 2. Pintar el corazón según sea favorito o no
        if (pajaroActual.isFavorito()) {
            holder.btnFavorito.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.btnFavorito.setImageResource(R.drawable.ic_favorite_border);
        }

        // 3. EVENTO: AL PULSAR EL CORAZÓN
        holder.btnFavorito.setOnClickListener(v -> {
            // Invertimos el estado (si era true, pasa a false, y viceversa)
            pajaroActual.setFavorito(!pajaroActual.isFavorito());

            // Reordenamos la lista completa para que los favoritos suban
            ordenarPorFavoritos();

            // Le decimos al adaptador que la lista ha cambiado para que mueva las tarjetas visualmente
            notifyDataSetChanged();
        });

        // 4. EVENTO: AL PULSAR LOS 3 PUNTITOS
        holder.btnMenuOpciones.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Editar " + pajaroActual.getNombre(), Toast.LENGTH_SHORT).show()
        );

        // 5. EVENTO: AL PULSAR LA TARJETA (Para ir a Detalles)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetallePajaro.class);
            intent.putExtra("PAJARO_SELECCIONADO", pajaroActual);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaPajaros.size();
    }

    // --- NUEVO MÉTODO PARA ORDENAR ---
    // Este método usa el comparador de Java para poner los true (favoritos) arriba
    public void ordenarPorFavoritos() {
        Collections.sort(listaPajaros, new Comparator<Pajaro>() {
            @Override
            public int compare(Pajaro p1, Pajaro p2) {
                // Comparamos p2 contra p1 para que el orden sea descendente (los true van antes que los false)
                return Boolean.compare(p2.isFavorito(), p1.isFavorito());
            }
        });
    }

    // --- CLASE VIEWHOLDER ---
    public static class PajaroViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalle;
        ImageButton btnMenuOpciones, btnFavorito;

        public PajaroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDetalle = itemView.findViewById(R.id.tvDetalle);
            btnMenuOpciones = itemView.findViewById(R.id.btnEditFiles);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
        }
    }
}