package com.example.apppajaros;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Perfil extends AppCompatActivity {

    private TextView tvNombreCompleto, tvUsername, tvCerrarSesion;
    private TextView tvContadorPajaros, tvContadorFotos, tvContadorArticulos;
    private Button btnMiGaleria, btnMisArticulos;
    private ImageView btnEditarDatos, btnVolverAtras; // aqui meto la flechita

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // enlazo las piezas
        tvNombreCompleto = findViewById(R.id.tvNombreCompleto);
        tvUsername = findViewById(R.id.tvUsername);
        tvCerrarSesion = findViewById(R.id.tvCerrarSesion);
        tvContadorPajaros = findViewById(R.id.tvContadorPajaros);
        tvContadorFotos = findViewById(R.id.tvContadorFotos);
        tvContadorArticulos = findViewById(R.id.tvContadorArticulos);
        btnEditarDatos = findViewById(R.id.btnEditarDatos);
        btnMiGaleria = findViewById(R.id.btnMiGaleria);
        btnMisArticulos = findViewById(R.id.btnMisArticulos);

        // busco mi flecha
        btnVolverAtras = findViewById(R.id.btnVolverAtras);

        cargarDatosUsuario();

        // el click de volver atras
        if (btnVolverAtras != null) {
            btnVolverAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // cierro esta pantalla y vuelvo de donde venia
                    finish();
                }
            });
        }

        if (btnEditarDatos != null) {
            btnEditarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencion = new Intent(Perfil.this, AjustesActivity.class);
                    startActivity(intencion);
                }
            });
        }

//        if (btnMiGaleria != null) {
//            btnMiGaleria.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intencion = new Intent(Perfil.this, GaleriaActivity.class);
//                    startActivity(intencion);
//                }
//            });
//        }
//
//        if (btnMisArticulos != null) {
//            btnMisArticulos.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intencion = new Intent(Perfil.this, ArticulosActivity.class);
//                    startActivity(intencion);
//                }
//            });
//        }

        if (tvCerrarSesion != null) {
            tvCerrarSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarDialogoCerrarSesion();
                }
            });
        }
    }

    private void cargarDatosUsuario() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = db.collection("Usuarios").document(userId);

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {

                        String nombre = documentSnapshot.getString("nombre");
                        // nota de viejo: apellidos no lo guardamos en el registro, pero lo dejo por si lo añades luego
                        String apellidos = documentSnapshot.getString("apellidos");
                        String username = documentSnapshot.getString("username");

                        if (tvNombreCompleto != null) {
                            if (nombre != null) {
                                // si no hay apellidos, pongo solo el nombre
                                tvNombreCompleto.setText(apellidos != null ? nombre + " " + apellidos : nombre);
                            }
                        }

                        if (tvUsername != null) {
                            if (username != null) {
                                tvUsername.setText("@" + username);
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Perfil.this, "Error al cargar los datos del nido", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (tvNombreCompleto != null && tvUsername != null) {
                tvNombreCompleto.setText("Usuario no logueado");
                tvUsername.setText("");
            }
        }
    }

    private void mostrarDialogoCerrarSesion() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView btnCancel = view.findViewById(R.id.btnCancelLogout);
        Button btnConfirm = view.findViewById(R.id.btnConfirmLogout);

        if (btnCancel != null) {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    dialog.dismiss();

                    Intent intent = new Intent(Perfil.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

        dialog.show();
    }
}