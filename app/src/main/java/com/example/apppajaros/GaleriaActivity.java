package com.example.apppajaros;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class GaleriaActivity extends AppCompatActivity {


    private ImageView btnVolverAtras;
    private RecyclerView rvGaleria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);


        // busco las herramientas en el cajon
        btnVolverAtras = findViewById(R.id.btnVolverAtras);
        rvGaleria = findViewById(R.id.rvGaleria);


        // logica para salir pitando de aqui
        if (btnVolverAtras != null) {
            btnVolverAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        // preparo la cuadricula de fotos
        if (rvGaleria != null) {
            // le digo que quiero 3 columnas para que parezca una galeria pro
            GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
            rvGaleria.setLayoutManager(layoutManager);


            // TODO: Aqui conectaremos el adaptador con las fotos de Firebase Storage o la API según nos convenga mejor
        }
    }
}
