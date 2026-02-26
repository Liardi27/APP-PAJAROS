package com.example.apppajaros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegistrarseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // la herramienta para guardar el nombre de usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // enciendo el motor de firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // busco los elementos uno a uno
        TextInputEditText et_Nombre = findViewById(R.id.et_Nombre);
        TextInputEditText et_UserName = findViewById(R.id.et_UserName); // el nuevo recluta
        TextInputEditText et_Correo = findViewById(R.id.et_Correo);
        TextInputEditText et_Pass = findViewById(R.id.et_Pass);
        TextInputEditText et_ConfirmarPass = findViewById(R.id.et_ConfirmarPass);

        CheckBox chbx_Terminos = findViewById(R.id.chbx_Terminos);
        Button btn_FinalizarRegistro = findViewById(R.id.btn_FinalizarRegistro);
        TextView txvw_IrALogin = findViewById(R.id.txvw_IrALogin);
        View vistaRaiz = findViewById(R.id.pantalla_registro_raiz);

        // si ya tiene nido, que vuelva al login
        if (txvw_IrALogin != null) {
            txvw_IrALogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencion = new Intent(RegistrarseActivity.this, IniciarSesion.class);
                    startActivity(intencion);
                    finish();
                }
            });
        }

        // logica principal del registro
        if (btn_FinalizarRegistro != null) {
            btn_FinalizarRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (chbx_Terminos != null && chbx_Terminos.isChecked()) {

                        if (et_Nombre != null && et_UserName != null && et_Correo != null && et_Pass != null && et_ConfirmarPass != null && vistaRaiz != null) {

                            String nombre = et_Nombre.getText().toString().trim();
                            String username = et_UserName.getText().toString().trim();
                            String correo = et_Correo.getText().toString().trim();
                            String pass1 = et_Pass.getText().toString();
                            String pass2 = et_ConfirmarPass.getText().toString();

                            // compruebo que nada este vacio y que las claves coincidan
                            if (!nombre.isEmpty() && !username.isEmpty() && !correo.isEmpty() && !pass1.isEmpty()) {

                                if (pass1.equals(pass2)) {

                                    // llamo a google para crear el usuario con correo y clave
                                    mAuth.createUserWithEmailAndPassword(correo, pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {

                                                // si se ha creado bien en auth, guardo el alias en Firestore
                                                String idUsuario = mAuth.getCurrentUser().getUid();
                                                Map<String, Object> usuario = new HashMap<>();
                                                usuario.put("nombre", nombre);
                                                usuario.put("username", username);
                                                usuario.put("correo", correo);

                                                db.collection("Usuarios").document(idUsuario).set(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskDb) {
                                                        if (taskDb.isSuccessful()) {
                                                            // todo perfecto, nos vamos a la principal
                                                            Intent intent = new Intent(RegistrarseActivity.this, PaginaPrincipal.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Snackbar.make(vistaRaiz, "Error al guardar perfil: " + taskDb.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                Snackbar.make(vistaRaiz, "Fallo al registrar: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                } else {
                                    Snackbar.make(vistaRaiz, "Las contrasenas no coinciden", Snackbar.LENGTH_SHORT).show();
                                }

                            } else {
                                Snackbar.make(vistaRaiz, "Rellena todos los campos antes de volar", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    } else if (vistaRaiz != null) {
                        Snackbar.make(vistaRaiz, "Debes aceptar los terminos para entrar", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}