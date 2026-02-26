package com.example.apppajaros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class IniciarSesion extends AppCompatActivity {

    // preparo la herramienta de firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        // enciendo firebase
        mAuth = FirebaseAuth.getInstance();

        // busco mis elementos en la vista
        TextInputEditText et_CorreoLogin = findViewById(R.id.et_CorreoLogin);
        TextInputEditText et_PassLogin = findViewById(R.id.et_PassLogin);
        Button btn_IniciarSesion = findViewById(R.id.btn_IniciarSesion);
        TextView txvw_OlvidastePass = findViewById(R.id.txvw_OlvidastePass);
        TextView txvw_IrARegistro = findViewById(R.id.txvw_IrARegistro);
        View vistaRaiz = findViewById(R.id.pantalla_login_raiz);

        // mando al usuario al registro si no tiene cuenta
        if (txvw_IrARegistro != null) {
            txvw_IrARegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencion = new Intent(IniciarSesion.this, RegistrarseActivity.class);
                    startActivity(intencion);
                    // cierro esta para no llenar la ram
                    finish();
                }
            });
        }

        // logica para entrar
        if (btn_IniciarSesion != null) {
            btn_IniciarSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (et_CorreoLogin != null && et_PassLogin != null && vistaRaiz != null) {

                        String correo = et_CorreoLogin.getText().toString().trim();
                        String pass = et_PassLogin.getText().toString();

                        // compruebo que no me intenta engañar con campos vacios
                        if (!correo.isEmpty() && !pass.isEmpty()) {

                            Snackbar.make(vistaRaiz, "Validando credenciales...", Snackbar.LENGTH_SHORT).show();

                            // le pregunto a firebase si este usuario es legal
                            mAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> tarea) {

                                    if (tarea.isSuccessful()) {
                                        // si todo ok nos vamos a la pagina principal
                                        Intent intent = new Intent(IniciarSesion.this, PaginaPrincipal.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // si falla le aviso
                                        Snackbar.make(vistaRaiz, "Error: El correo o la clave no cuadran", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Snackbar.make(vistaRaiz, "Rellena los campos, por favor", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        // enlace para recuperar clave
        if (txvw_OlvidastePass != null) {
            txvw_OlvidastePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IniciarSesion.this, RecuperarPassActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}