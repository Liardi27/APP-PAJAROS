package com.example.apppajaros;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.MapEventsOverlay;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

public class AniadirRegistros extends AppCompatActivity {

    private EditText etBuscarAPI, etNombre, etNombreCientifico, etDescCorta, etDescLarga, etEnvergadura, etColores, etAlimentacion, etEtiquetas;
    private SwitchCompat swDismorfia, swMigratorio;
    private LinearLayout llFotosDismorfia, llSeccionMigracion;
    private ImageButton btnSubirPortada;
    private Button btnFotoMacho, btnFotoHembra, btnFotoGeneral;
    private TextView btnAtras, btnGuardar;
    private MapView mapViewMigracion;
    private Marker mapMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.aniadir_registro);

        vincularComponentes();

        // ARREGLO: Boton atras ya funciona cerrando la actividad
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        configurarInterruptores();
        configurarBotonesImagenes();
        configurarMapa();
    }

    private void configurarBotonesImagenes() {
        View.OnClickListener abrirGaleriaListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
            }
        };

        if(btnSubirPortada != null) btnSubirPortada.setOnClickListener(abrirGaleriaListener);
        if(btnFotoMacho != null) btnFotoMacho.setOnClickListener(abrirGaleriaListener);
        if(btnFotoHembra != null) btnFotoHembra.setOnClickListener(abrirGaleriaListener);
        if(btnFotoGeneral != null) btnFotoGeneral.setOnClickListener(abrirGaleriaListener);
    }

    private void abrirGaleria() {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    private void vincularComponentes() {
        etBuscarAPI = findViewById(R.id.etBuscarAPI);
        etNombre = findViewById(R.id.etNombre);
        etNombreCientifico = findViewById(R.id.etNombreCientifico);
        etDescCorta = findViewById(R.id.etDescCorta);
        etDescLarga = findViewById(R.id.etDescLarga);
        etEnvergadura = findViewById(R.id.etEnvergadura);
        etColores = findViewById(R.id.etColores);
        etAlimentacion = findViewById(R.id.etAlimentacion);

        swDismorfia = findViewById(R.id.swDismorfia);
        swMigratorio = findViewById(R.id.swMigratorio);

        llFotosDismorfia = findViewById(R.id.llFotosDismorfia);
        llSeccionMigracion = findViewById(R.id.llSeccionMigracion);

        btnSubirPortada = findViewById(R.id.btnSubirPortada);
        btnAtras = findViewById(R.id.btnAtras);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnFotoMacho = findViewById(R.id.btnFotoMacho);
        btnFotoHembra = findViewById(R.id.btnFotoHembra);
        btnFotoGeneral = findViewById(R.id.btnFotoGeneral);

        mapViewMigracion = findViewById(R.id.mapViewMigracion);
    }

    private void configurarInterruptores() {
        // Empiezan desactivados y con texto NO
        swDismorfia.setChecked(false);
        swDismorfia.setText("NO");
        swDismorfia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swDismorfia.setText("SI");
                    llFotosDismorfia.setVisibility(View.VISIBLE);
                    if(btnFotoGeneral != null) btnFotoGeneral.setVisibility(View.GONE);
                } else {
                    swDismorfia.setText("NO");
                    llFotosDismorfia.setVisibility(View.GONE);
                    if(btnFotoGeneral != null) btnFotoGeneral.setVisibility(View.VISIBLE);
                }
            }
        });

        swMigratorio.setChecked(false);
        swMigratorio.setText("NO");
        swMigratorio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swMigratorio.setText("SI");
                    llSeccionMigracion.setVisibility(View.VISIBLE);
                }
                if (!isChecked) {
                    swMigratorio.setText("NO");
                    llSeccionMigracion.setVisibility(View.GONE);
                }
            }
        });
    }

    private void configurarMapa() {
        if (mapViewMigracion == null) return;
        mapViewMigracion.setMultiTouchControls(true);
        mapViewMigracion.getController().setZoom(5.0);
        mapViewMigracion.getController().setCenter(new GeoPoint(40.4168, -3.7038)); // EspaÃ±a

        mapViewMigracion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (mapMarker != null) {
                    mapViewMigracion.getOverlays().remove(mapMarker);
                }
                mapMarker = new Marker(mapViewMigracion);
                mapMarker.setPosition(p);
                mapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mapViewMigracion.getOverlays().add(mapMarker);
                mapViewMigracion.invalidate();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        mapViewMigracion.getOverlays().add(new MapEventsOverlay(mReceive));
    }
}