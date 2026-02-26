package com.example.apppajaros;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ArticulosActivity extends AppCompatActivity {


    private ImageView btnVolverAtras;
    private RecyclerView rvArticulos;
    private FloatingActionButton fabNuevoArticulo;


    // esta variable es la que controla el cotarro
    // de momento la dejo en 0 para que puedas probar que funciona
    private int cantidadArticulosActuales = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulos);


        // engancho los elementos del xml
        btnVolverAtras = findViewById(R.id.btnVolverAtras);
        rvArticulos = findViewById(R.id.rvArticulos);
        fabNuevoArticulo = findViewById(R.id.fabNuevoArticulo);


        // configuro la lista para que sea responsive y elastica
        if (rvArticulos != null) {
            rvArticulos.setLayoutManager(new LinearLayoutManager(this));
            // aqui ira el adaptador cuando los pajaros empiecen a cantar
        }


        // logica para volver a la pantalla anterior sin dramas
        if (btnVolverAtras != null) {
            btnVolverAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // cierro la ventana y a otra cosa
                    finish();
                }
            });
        }


        // aqui esta  el boton de crear si no hay articulos previamente
        if (fabNuevoArticulo != null) {
            fabNuevoArticulo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    // compruebo si el nido esta vacio
                    if (cantidadArticulosActuales == 0) {


                        // como no hay nada, dejo pasar al usuario
                        Toast.makeText(ArticulosActivity.this, "Abriendo el editor de cronicas...", Toast.LENGTH_SHORT).show();


                        // cuando crees la actividad de escribir, quita los comentarios de abajo
                        // Intent intent = new Intent(ArticulosActivity.this, CrearArticuloActivity.class);
                        // startActivity(intent);


                    } else {


                        // si ya hay articulos, le corto el paso
                        Toast.makeText(ArticulosActivity.this, "Solo se permite un articulo por usuario, frena un poco.", Toast.LENGTH_LONG).show();


                    }
                }
            });
        }
    }
}
