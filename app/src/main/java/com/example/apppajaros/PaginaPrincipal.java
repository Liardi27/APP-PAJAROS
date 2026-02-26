package com.example.apppajaros;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

// Importaciones de Retrofit que necesitamos
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaginaPrincipal extends AppCompatActivity {

    private TextView tvUserName;
    private ImageButton btnNotificaciones;
    private boolean tieneNotificacionesSinLeer = false;

    // Variables globales para la lista y el adaptador para poder actualizarlos después
    private PajaroAdapter adapter;
    private List<Pajaro> listaPajaros;

    // TU CLAVE DE LA API (Asegúrate de poner la real)
    private final String API_KEY = "ef022e4d-d081-4134-8f88-ce81efbf84ff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_principal);

        tvUserName = findViewById(R.id.tvUserName);
        btnNotificaciones = findViewById(R.id.btnNotificaciones);

        cargarDatosUsuario();

        // 1. CLIC EN EL PERFIL
        LinearLayout lyPerfilClicable = findViewById(R.id.lyPerfilClicable);
        lyPerfilClicable.setOnClickListener(v -> {
            Intent intent = new Intent(PaginaPrincipal.this, Perfil.class);
            startActivity(intent);
        });

        // 2. CLIC EN NOTIFICACIONES
        btnNotificaciones.setOnClickListener(v -> {
            if (tieneNotificacionesSinLeer) {
                Toast.makeText(this, "Mostrando notificaciones...", Toast.LENGTH_SHORT).show();
                tieneNotificacionesSinLeer = false;
                btnNotificaciones.setImageResource(R.drawable.ic_notifications);
            } else {
                Toast.makeText(this, "No hay notificaciones nuevas", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. CONFIGURAR LISTA DE AVES (Ahora vacía al inicio)
        RecyclerView rvPajaros = findViewById(R.id.rvPajaros);
        rvPajaros.setLayoutManager(new LinearLayoutManager(this));

        listaPajaros = new ArrayList<>(); // Empezamos con la lista vacía
        adapter = new PajaroAdapter(listaPajaros);
        rvPajaros.setAdapter(adapter);

        // Llamamos a la API para que empiece a buscar los datos
        fetchBirds();

        // 4. BOTÓN FLOTANTE
        FloatingActionButton fab = findViewById(R.id.fabAgregarPajaro);
        fab.setOnClickListener(v ->{
            Toast.makeText(this, "Añadir avistamiento", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PaginaPrincipal.this, AniadirRegistros.class);
            startActivity(intent);
            // cierro esta para no llenar la ram
            finish();
        });

        // 5. NAVEGACIÓN INFERIOR
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_inicio);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) return true;
            if (id == R.id.nav_juegos) {
//                Intent intentJuego = new Intent(PaginaPrincipal.this, ConfiguracionActivity.class);
//                startActivity(intentJuego);
//                // cierro esta para no llenar la ram
//                finish();
                return true;}
            if (id == R.id.nav_ajustes){
//                Intent intentAjuste = new Intent(PaginaPrincipal.this, Ajustes.class);
//                startActivity(intentAjuste);
//                // cierro esta para no llenar la ram
//                finish();
                return true;
            }
            return false;
        });
    }

    private void cargarDatosUsuario() {
        String nombreSimulado = "Explorador Pinteño";
        tvUserName.setText(nombreSimulado);
        tieneNotificacionesSinLeer = true;

        if (tieneNotificacionesSinLeer) {
            btnNotificaciones.setImageResource(R.drawable.ic_notifications_active);
        } else {
            btnNotificaciones.setImageResource(R.drawable.ic_notifications);
        }
    }

    // --- NUEVO MÉTODO PARA LLAMAR A LA API ---
    private void fetchBirds() {
        NuthatchApiService apiService = RetrofitClient.getClient().create(NuthatchApiService.class);

        Call<List<Bird>> call = apiService.getBirds(API_KEY, null, true);

        call.enqueue(new Callback<List<Bird>>() {
            @Override
            public void onResponse(Call<List<Bird>> call, Response<List<Bird>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Bird> birdsFromApi = response.body();

                    // Limpiamos la lista por si acaso tenía datos viejos
                    listaPajaros.clear();

                    // Convertimos cada 'Bird' (de internet) a tu clase 'Pajaro'
                    for (Bird b : birdsFromApi) {
                        // Pasamos el nombre común y el nombre científico a tu constructor de Pajaro
                        listaPajaros.add(new Pajaro(b.getName(), b.getSciName()));
                    }

                    // ¡MAGIA! Le avisamos al adapter que los datos cambiaron para que refresque la pantalla
                    adapter.notifyDataSetChanged();

                } else {
                    Log.e("BIRD_API", "Error del servidor: " + response.code());
                    Toast.makeText(PaginaPrincipal.this, "Error al cargar aves", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bird>> call, Throwable t) {
                Log.e("BIRD_API", "Fallo de conexión: " + t.getMessage());
                Toast.makeText(PaginaPrincipal.this, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}