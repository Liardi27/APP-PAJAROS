package com.example.apppajaros;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Ajustes extends AppCompatActivity {

    // preparo mis variables del nido
    private ImageView btnVolverAtrasAjustes;
    private Switch swModoOscuro;
    private Button btnIdiomaES, btnIdiomaEN, btnExportarAjustes, btnImportarAjustes, btnBorrarCuenta;
    private TextView btnCerrarSesionAjustes, btnBorrarDatos;

    private FirebaseAuth mAuth;
    private Sqlite dbLocal;

    // Nombre estático para acceder a nuestras preferencias guardadas
    private static final String PREFS_NAME = "AjustesPajaros";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        // instancio mis herramientas
        mAuth = FirebaseAuth.getInstance();
        dbLocal = new Sqlite(this);

        // enlazo los cables con el xml
        btnVolverAtrasAjustes = findViewById(R.id.btnVolverAtrasAjustes);
        swModoOscuro = findViewById(R.id.swModoOscuro);
        btnIdiomaES = findViewById(R.id.btnIdiomaES);
        btnIdiomaEN = findViewById(R.id.btnIdiomaEN);
        btnExportarAjustes = findViewById(R.id.btnExportarAjustes);
        btnImportarAjustes = findViewById(R.id.btnImportarAjustes);
        btnCerrarSesionAjustes = findViewById(R.id.btnCerrarSesionAjustes);
        btnBorrarDatos = findViewById(R.id.btnBorrarDatos);
        btnBorrarCuenta = findViewById(R.id.btnBorrarCuenta);

        // Cargar el estado del modo oscuro guardado en memoria
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isModoOscuro = prefs.getBoolean("modoOscuro", false); // Por defecto claro (false)
        if (swModoOscuro != null) {
            swModoOscuro.setChecked(isModoOscuro);
        }

        configurarEventos();
    }

    private void configurarEventos() {
        // flecha para volver garantizando ir a la pantalla principal
        if (btnVolverAtrasAjustes != null) {
            btnVolverAtrasAjustes.setOnClickListener(v -> volverAlInicio());
        }

        // modo oscuro persistente (se guarda para la proxima vez que abras la app)
        if (swModoOscuro != null) {
            swModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Gaurdar la elección
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("modoOscuro", isChecked);
                editor.apply();

                // Aplicar modo oscuro/claro
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            });
        }

        // cierre de sesion
        if (btnCerrarSesionAjustes != null) {
            btnCerrarSesionAjustes.setOnClickListener(v -> {
                mAuth.signOut();
                volverAlInicio();
            });
        }

        // borrado de datos locales (SQLite y SharedPreferences)
        if (btnBorrarDatos != null) {
            btnBorrarDatos.setOnClickListener(v -> mostrarConfirmacion("¿Borrar datos locales?",
                    "Se limpiará tu base de datos y memoria local, pero tu cuenta seguirá en la nube.",
                    this::limpiarSqliteYPrefs));
        }

        // borrado definitivo de cuenta en Firebase
        if (btnBorrarCuenta != null) {
            // CORREGIDO: apunto al método correcto 'eliminarCuentaFirebase'
            btnBorrarCuenta.setOnClickListener(v -> mostrarConfirmacion("¿ELIMINAR CUENTA?",
                    "Esta acción es irreversible. Perderás todos tus datos y acceso.",
                    this::eliminarCuentaFirebase));
        }
    }

    // metodo Don Viejo para no repetir dialogos
    private void mostrarConfirmacion(String titulo, String msg, Runnable accionSi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(msg);
        builder.setPositiveButton("Sí, eliminar", (dialog, which) -> accionSi.run());
        builder.setNegativeButton("No, cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void limpiarSqliteYPrefs() {
        // reseteo la base de datos local
        dbLocal.onUpgrade(dbLocal.getWritableDatabase(), 1, 1);

        // limpio los ajustes guardados
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Datos locales barridos con éxito", Toast.LENGTH_SHORT).show();
    }

    private void eliminarCuentaFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    limpiarSqliteYPrefs(); // Limpiamos huella local si se borró la cuenta con éxito
                    volverAlInicio();
                } else {
                    Toast.makeText(Ajustes.this, "Vuelve a loguearte para confirmar esta acción", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(Ajustes.this, "No hay ninguna sesión iniciada", Toast.LENGTH_SHORT).show();
        }
    }

    private void volverAlInicio() {
        Intent intent = new Intent(this, PaginaPrincipal.class); // MainActivity debe ser tu pantalla principal
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
