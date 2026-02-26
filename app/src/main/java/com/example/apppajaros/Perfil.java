package com.example.apppajaros;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

    // declaro mis piezas del tablero
    private TextView tvNombreCompleto, tvUsername, tvCerrarSesion;
    private TextView tvContadorPajaros, tvContadorFotos, tvContadorArticulos;
    private Button btnMiGaleria, btnMisArticulos;
    private ImageView btnEditarDatos, btnVolverAtras, imgFotoPerfil;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // arranco firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // enlazo la vista con mi codigo
        tvNombreCompleto = findViewById(R.id.tvNombreCompleto);
        tvUsername = findViewById(R.id.tvUsername);
        tvCerrarSesion = findViewById(R.id.tvCerrarSesion);
        tvContadorPajaros = findViewById(R.id.tvContadorPajaros);
        tvContadorFotos = findViewById(R.id.tvContadorFotos);
        tvContadorArticulos = findViewById(R.id.tvContadorArticulos);
        btnEditarDatos = findViewById(R.id.btnEditarDatos);
        btnMiGaleria = findViewById(R.id.btnMiGaleria);
        btnMisArticulos = findViewById(R.id.btnMisArticulos);
        btnVolverAtras = findViewById(R.id.btnVolverAtras);
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);

        // cargo los datos por primera vez
        cargarDatosUsuario();

        // configuro mis botones con la logica de Don Viejo
        configurarBotones();
    }

    // este metodo es clave: se lanza cuando vuelvo de Editar Perfil
    @Override
    protected void onResume() {
        super.onResume();
        // refresco los datos por si he cambiado algo en la otra pantalla
        cargarDatosUsuario();
    }

    private void configurarBotones() {
        // flecha para volver a la pantalla anterior
        if (btnVolverAtras != null) {
            btnVolverAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        // boton para ir a editar el nido
        if (btnEditarDatos != null) {
            btnEditarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencion = new Intent(Perfil.this, EditarPerfil.class);
                    startActivity(intencion);
                }
            });
        }

        // mi galeria personal
        if (btnMiGaleria != null) {
            btnMiGaleria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencion = new Intent(Perfil.this, GaleriaActivity.class);
                    startActivity(intencion);
                }
            });
        }

        // mis articulos escritos
        if (btnMisArticulos != null) {
            btnMisArticulos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencion = new Intent(Perfil.this, ArticulosActivity.class);
                    startActivity(intencion);
                }
            });
        }

        // el adios definitivo
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

        // solo trabajo si hay alguien logueado
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // 1. CARGA LOCAL RAPIDA: miro si tengo la foto en el movil
            SharedPreferences prefs = getSharedPreferences("AjustesPajaros", MODE_PRIVATE);
            String fotoLocalStr = prefs.getString("ruta_foto_perfil", "");

            if (!fotoLocalStr.isEmpty()) {
                Uri uriLocal = Uri.parse(fotoLocalStr);
                if (imgFotoPerfil != null) {
                    // la pongo directamente para que el usuario no espere
                    imgFotoPerfil.setImageURI(uriLocal);
                }
            }

            // 2. CARGA DE FIRESTORE: traigo los textos actualizados
            DocumentReference docRef = db.collection("Usuarios").document(userId);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String apellidos = documentSnapshot.getString("apellidos");
                        String username = documentSnapshot.getString("username");

                        if (tvNombreCompleto != null) {
                            // compruebo si hay apellidos para no dejar huecos feos
                            tvNombreCompleto.setText(apellidos != null ? nombre + " " + apellidos : nombre);
                        }

                        if (tvUsername != null) {
                            tvUsername.setText("@" + username);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Perfil.this, "Error al conectar con el nido", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // si por algun motivo no hay usuario aviso
            if (tvNombreCompleto != null) {
                tvNombreCompleto.setText("Sin conexion");
            }
        }
    }

    private void mostrarDialogoCerrarSesion() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        // hago que el fondo sea transparente para que el diseño luzca
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

                    // limpio el historial para que no pueda volver atras al perfil
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