package com.example.apppajaros;
//CONFIGURACION PARA EL MODO CLARO Y OSCURO
import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class MiApp extends Application {

    // Este nombre tiene que ser exactamente el mismo que usas en Ajustes.java
    private static final String PREFS_NAME = "AjustesPajaros";

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. Nada más arrancar la app, leemos las preferencias del usuario
        // Por defecto será "false" (modo claro) si nunca ha entrado a Ajustes
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isModoOscuro = prefs.getBoolean("modoOscuro", false);

        // 2. Aplicamos el modo globalmente para toda la aplicación antes de que carguen las pantallas
        if (isModoOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
