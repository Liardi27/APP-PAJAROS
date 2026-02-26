package com.example.apppajaros; // asegurate de que es tu paquete

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RecuperarPassActivity extends AppCompatActivity {

    // preparo las herramientas de firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_pass);

        // enciendo firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // busco los elementos en la pantalla
        TextInputEditText et_DatoRecuperacion = findViewById(R.id.et_DatoRecuperacion);
        Button btn_EnviarRecuperacion = findViewById(R.id.btn_EnviarRecuperacion);

        // la raiz para que el snackbar sepa de donde salir
        View vistaRaiz = findViewById(R.id.pantalla_recuperar_raiz);

        // me aseguro de que el boton no es un fantasma
        if (btn_EnviarRecuperacion != null) {

            btn_EnviarRecuperacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // compruebo que la caja de texto y la raiz no son nulas
                    if (et_DatoRecuperacion != null && vistaRaiz != null) {

                        // saco lo que ha escrito el usuario
                        String dato = et_DatoRecuperacion.getText().toString().trim();

                        // compruebo si el despistado ha escrito algo o le ha dado al boton sin querer
                        if (!dato.isEmpty()) {

                            // Detectamos automáticamente si el texto tiene formato de correo electrónico
                            if (Patterns.EMAIL_ADDRESS.matcher(dato).matches()) {
                                // Si tiene formato de correo (lleva @ y dominio), enviamos directo
                                enviarCorreoRecuperacion(dato, vistaRaiz);
                            } else {
                                // Si no tiene formato de correo, asumimos que es un username y lo buscamos
                                buscarCorreoPorUsername(dato, vistaRaiz);
                            }

                        } else {
                            // si la caja esta vacia
                            Snackbar.make(vistaRaiz, "Escribe tu correo o username primero, campeón.", Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(getResources().getColor(R.color.marron_tierra))
                                    .show();
                        }
                    }
                }
            });
        }
    }

    // Método para buscar el correo en Firestore usando el username
    private void buscarCorreoPorUsername(String username, View vistaRaiz) {
        Snackbar.make(vistaRaiz, "Buscando tu nido por username...", Snackbar.LENGTH_SHORT).show();

        // Asegúrate de que tu colección se llama "Usuarios" y el campo se llama "username"
        db.collection("Usuarios")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // ¡Encontramos al usuario! Sacamos su correo
                            String correoEncontrado = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Asegúrate de que el campo donde guardas el email en Firestore se llame "correo"
                                correoEncontrado = document.getString("correo"); 
                            }

                            if (correoEncontrado != null && !correoEncontrado.isEmpty()) {
                                // Una vez tenemos el correo real, le decimos a Firebase que mande el enlace
                                enviarCorreoRecuperacion(correoEncontrado, vistaRaiz);
                            } else {
                                Snackbar.make(vistaRaiz, "Error: Este usuario no tiene un correo registrado.", Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(getResources().getColor(R.color.marron_tierra))
                                        .show();
                            }

                        } else {
                            // Si no lo encuentra en la base de datos
                            Snackbar.make(vistaRaiz, "No hemos encontrado ningún pájaro con ese username.", Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(getResources().getColor(R.color.marron_tierra))
                                    .show();
                        }
                    }
                });
    }

    // Método para pedirle a Firebase que envíe el correo real
    private void enviarCorreoRecuperacion(String correo, View vistaRaiz) {
        mAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(vistaRaiz, "Te hemos mandado una paloma mensajera a tu correo.", Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(getResources().getColor(R.color.verde_oscuro))
                                    .show();
                            volverAlLogin();
                        } else {
                            Snackbar.make(vistaRaiz, "Error al enviar el correo. ¿Seguro que existe?", Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(getResources().getColor(R.color.marron_tierra))
                                    .show();
                        }
                    }
                });
    }

    // me creo un metodo chiquitito para la espera y no repetir codigo a lo tonto
    private void volverAlLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // preparo el viaje de vuelta al inicio de sesion
                Intent intencion = new Intent(RecuperarPassActivity.this, IniciarSesion.class);
                startActivity(intencion);

                // cierro esta pantalla
                finish();
            }
        }, 2500); // le doy 2 segundos y medio para que lea el mensaje tranquilamente
    }
}
