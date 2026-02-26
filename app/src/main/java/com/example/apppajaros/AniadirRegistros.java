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

public class AniadirRegistros extends AppCompatActivity {

    private EditText etBuscarAPI, etNombre, etNombreCientifico, etDescCorta, etDescLarga, etEnvergadura, etColores, etAlimentacion, etEtiquetas;
    private SwitchCompat swDismorfia, swMigratorio;
    private LinearLayout llFotosDismorfia, llSeccionMigracion;
    private ImageButton btnSubirPortada;
    private TextView btnAtras, btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                }
                if (!isChecked) {
                    swDismorfia.setText("NO");
                    llFotosDismorfia.setVisibility(View.GONE);
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
}