package com.example.apppajaros;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracionActivity extends AppCompatActivity {

    private RadioGroup rgIdioma, rgTema;
    private RadioButton rbEspanol, rbIngles, rbTemaClaro, rbTemaOscuro;
    private Button btnGuardarConfiguracion;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        // SharedPreferences para guardar en el dispositivo
        sharedPreferences = getSharedPreferences("ConfigNido", Context.MODE_PRIVATE);

        rgIdioma = findViewById(R.id.rgIdioma);
        rgTema = findViewById(R.id.rgTema);
        rbEspanol = findViewById(R.id.rbEspanol);
        rbIngles = findViewById(R.id.rbIngles);
        rbTemaClaro = findViewById(R.id.rbTemaClaro);
        rbTemaOscuro = findViewById(R.id.rbTemaOscuro);
        btnGuardarConfiguracion = findViewById(R.id.btnGuardarConfiguracion);

        cargarPreferenciasActuales();

        btnGuardarConfiguracion.setOnClickListener(v -> guardarPreferencias());
    }

    private void cargarPreferenciasActuales() {
        // Por defecto: "es" (español) y "claro" (día)
        String idioma = sharedPreferences.getString("idioma", "es");
        String tema = sharedPreferences.getString("tema", "claro");

        if (idioma.equals("es")) {
            rbEspanol.setChecked(true);
        } else {
            rbIngles.setChecked(true);
        }

        if (tema.equals("claro")) {
            rbTemaClaro.setChecked(true);
        } else {
            rbTemaOscuro.setChecked(true);
        }
    }

    private void guardarPreferencias() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (rbEspanol.isChecked()) {
            editor.putString("idioma", "es");
        } else if (rbIngles.isChecked()) {
            editor.putString("idioma", "en");
        }

        if (rbTemaClaro.isChecked()) {
            editor.putString("tema", "claro");
        } else if (rbTemaOscuro.isChecked()) {
            editor.putString("tema", "oscuro");
        }

        editor.apply();

        Toast.makeText(this, "Configuración guardada. Reinicia la app para ver cambios", Toast.LENGTH_LONG).show();
        finish();
    }
}
