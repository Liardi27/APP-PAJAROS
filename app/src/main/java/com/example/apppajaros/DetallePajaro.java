package com.example.apppajaros; // Cambia por tu paquete

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetallePajaro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detalle_pajaro);

        TextView tvNombre = findViewById(R.id.tvDetalleNombre);
        TextView tvDesc = findViewById(R.id.tvDetalleDescripcion);

        // Recuperamos el objeto Pajaro que enviamos desde el Adaptador
        if (getIntent().hasExtra("PAJARO_SELECCIONADO")) {
            Pajaro pajaro = (Pajaro) getIntent().getSerializableExtra("PAJARO_SELECCIONADO");
            if (pajaro != null) {
                tvNombre.setText(pajaro.getNombre());
                tvDesc.setText(pajaro.getDescripcion());
            }
        }
    }
}