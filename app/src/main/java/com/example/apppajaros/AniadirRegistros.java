package com.example.apppajaros;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.Marker;
import java.util.HashMap;

public class AniadirRegistros extends AppCompatActivity {

    private MapView map;
    private TextView btnAtras, btnGuardar;
    private ImageButton btnSubirPortada;
    private SwitchCompat swDismorfia, swMigratorio;
    private LinearLayout llFotosDismorfia, llSeccionMigracion;
    private HashMap<String, GeoPoint> rutaPuntos = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.aniadir_registro);

        vincularComponentes();

        // Boton atras inteligente
        btnAtras.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Lógica de fotos y visibilidad cruzada
        configurarFotosYVisibilidad();

        // Configuracion del mapa
        configurarMapa();
    }

    private void vincularComponentes() {
        map = findViewById(R.id.mapaLibre);
        btnAtras = findViewById(R.id.btnAtras);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnSubirPortada = findViewById(R.id.btnSubirPortada);
        swDismorfia = findViewById(R.id.swDismorfia);
        swMigratorio = findViewById(R.id.swMigratorio);
        llFotosDismorfia = findViewById(R.id.llFotosDismorfia);
        llSeccionMigracion = findViewById(R.id.llSeccionMigracion);
    }

    private void configurarFotosYVisibilidad() {
        // Al empezar, dismorfia es NO
        swDismorfia.setChecked(false);
        swDismorfia.setText("NO");

        swDismorfia.setOnCheckedChangeListener((cb, isChecked) -> {
            if (isChecked) {
                swDismorfia.setText("SI");
                // OCULTA la foto comun arriba
                btnSubirPortada.setVisibility(View.GONE);
                // MUESTRA los botones macho/hembra abajo
                llFotosDismorfia.setVisibility(View.VISIBLE);
            } else {
                swDismorfia.setText("NO");
                // MUESTRA la foto comun arriba
                btnSubirPortada.setVisibility(View.VISIBLE);
                // OCULTA los botones macho/hembra abajo
                llFotosDismorfia.setVisibility(View.GONE);
            }
        });

        swMigratorio.setChecked(false);
        swMigratorio.setText("NO");
        swMigratorio.setOnCheckedChangeListener((cb, isChecked) -> {
            if (isChecked) {
                swMigratorio.setText("SI");
                llSeccionMigracion.setVisibility(View.VISIBLE);
            } else {
                swMigratorio.setText("NO");
                llSeccionMigracion.setVisibility(View.GONE);
            }
        });

        // Click en la foto comun
        btnSubirPortada.setOnClickListener(v -> abrirGaleria(1));
    }

    private void configurarMapa() {
        map.setMultiTouchControls(true);
        map.getController().setZoom(5.0);
        map.getController().setCenter(new GeoPoint(40.41, -3.70));

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // Aqui ya no peta porque no dependemos de un RadioGroup
                Marker m = new Marker(map);
                m.setPosition(p);
                map.getOverlays().add(m);
                map.invalidate();
                return true;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) { return false; }
        };
        map.getOverlays().add(new MapEventsOverlay(mReceive));
    }

    private void abrirGaleria(int codigo) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, codigo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) btnSubirPortada.setImageURI(data.getData());
        }
    }
}