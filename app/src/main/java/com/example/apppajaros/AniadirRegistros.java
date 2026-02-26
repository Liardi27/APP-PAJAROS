package com.example.apppajaros;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AniadirRegistros extends AppCompatActivity {

    // declaro mis variables para conectar con el xml
    private EditText etNombre, etNombreCientifico, etDescCorta, etDescLarga, etEnvergadura, etColores, etAlimentacion, etEtiquetas;
    private RadioGroup rgDismorfia, rgMigratorio;
    private LinearLayout llFotosDismorfia, llSeccionMigracion;
    private TextView tvAutorAuto, btnGuardar;
    private Button btnAutocompletar;

    // aqui guardare los meses seleccionados en el futuro
    private GridLayout gridMeses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aniadir_registro);

        // inicio el buscador de piezas
        vincularComponentes();

        // pongo el autor de forma automatica para que haga bulto en el diseño
        if (tvAutorAuto != null) {
            tvAutorAuto.setText("Autor: UsuarioActual");
        }

        // configuro la logica visual
        configurarEventosDinamicos();
    }

    private void vincularComponentes() {
        // busco cada elemento por su id del xml
        etNombre = findViewById(R.id.etNombre);
        etNombreCientifico = findViewById(R.id.etNombreCientifico);
        etDescCorta = findViewById(R.id.etDescCorta);
        etDescLarga = findViewById(R.id.etDescLarga);
        etEnvergadura = findViewById(R.id.etEnvergadura);
        etColores = findViewById(R.id.etColores);
        etAlimentacion = findViewById(R.id.etAlimentacion);
        etEtiquetas = findViewById(R.id.etEtiquetas);

        rgDismorfia = findViewById(R.id.rgDismorfia);
        rgMigratorio = findViewById(R.id.rgMigratorio);

        llFotosDismorfia = findViewById(R.id.llFotosDismorfia);
        llSeccionMigracion = findViewById(R.id.llSeccionMigracion);

        tvAutorAuto = findViewById(R.id.tvAutorAuto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnAutocompletar = findViewById(R.id.btnAutocompletar);
    }

    private void configurarEventosDinamicos() {
        // gestiono el cambio de visibilidad de la dismorfia sexual
        if (rgDismorfia != null) {
            rgDismorfia.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.rbDismorfiaSi) {
                        llFotosDismorfia.setVisibility(View.VISIBLE);
                    }
                    if (checkedId == R.id.rbDismorfiaNo) {
                        llFotosDismorfia.setVisibility(View.GONE);
                    }
                }
            });
        }

        // gestiono si aparecen los campos de migracion o no
        if (rgMigratorio != null) {
            rgMigratorio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.rbMigratorioSi) {
                        llSeccionMigracion.setVisibility(View.VISIBLE);
                    }
                    if (checkedId == R.id.rbMigratorioNo) {
                        llSeccionMigracion.setVisibility(View.GONE);
                    }
                }
            });
        }

        // ejecuto la simulacion usando mi logica de condiciones
        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    simularGuardadoVisual();
                }
            });
        }
    }

    private void simularGuardadoVisual() {
        // extraigo los textos para comprobar que la interfaz responde
        String nombre = etNombre.getText().toString().trim();
        String cientifico = etNombreCientifico.getText().toString().trim();
        String descCorta = etDescCorta.getText().toString().trim();

        // empiezo mis validaciones esteticas
        if (nombre.isEmpty() || cientifico.isEmpty() || descCorta.isEmpty()) {
            // si le falta algo basico, le doy un toque de atencion
            Toast.makeText(this, "Rellena los campos obligatorios antes de seguir", Toast.LENGTH_SHORT).show();
        }

        if (!nombre.isEmpty() && !cientifico.isEmpty() && !descCorta.isEmpty()) {
            // si todo cuadra, hago el paripe de que estoy guardando
            Toast.makeText(this, "Simulando guardado de la especie: " + nombre, Toast.LENGTH_LONG).show();

            // el dia de mañana aqui metere la logica de firestore
            // por ahora me conformo con ver el toast
        }
    }
}