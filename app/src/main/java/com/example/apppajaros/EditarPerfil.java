package com.example.apppajaros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfil extends AppCompatActivity {

    // declaro todas las piezas de mi motor
    private ImageView btnVolverAtrasEditar, imgEditarFotoPerfil;
    private EditText etEditarNombre, etEditarApellidos, etEditarUsername;
    private MaterialButton btnGuardarEditar, btnExportarDatos, btnImportarDatos;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage; // añado el cajon de las fotos en la nube
    private String userId;

    private ActivityResultLauncher<Intent> lanzadorGaleria;
    // aqui me guardo la uri de la foto que elija para subirla luego
    private Uri uriFotoPillada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // arranco los motores de firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance(); // inicializo storage

        // enlazo la vista con mis variables
        btnVolverAtrasEditar = findViewById(R.id.btnVolverAtrasEditar);
        imgEditarFotoPerfil = findViewById(R.id.imgEditarFotoPerfil);
        etEditarNombre = findViewById(R.id.etEditarNombre);
        etEditarApellidos = findViewById(R.id.etEditarApellidos);
        etEditarUsername = findViewById(R.id.etEditarUsername);
        btnGuardarEditar = findViewById(R.id.btnGuardarEditar);
        btnExportarDatos = findViewById(R.id.btnExportarDatos);
        btnImportarDatos = findViewById(R.id.btnImportarDatos);

        // preparo el invento para abrir la galeria
        lanzadorGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            if (result.getData() != null) {
                                uriFotoPillada = result.getData().getData();
                                if (uriFotoPillada != null) {
                                    imgEditarFotoPerfil.setImageURI(uriFotoPillada);
                                }
                            }
                        }
                    }
                }
        );

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            cargarDatosActuales();
            configurarBotones();
        } else {
            Toast.makeText(this, "Nido vacio, logueate primero", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void configurarBotones() {
        if (btnVolverAtrasEditar != null) {
            btnVolverAtrasEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        if (imgEditarFotoPerfil != null) {
            imgEditarFotoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencionGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    lanzadorGaleria.launch(intencionGaleria);
                }
            });
        }

        if (btnGuardarEditar != null) {
            btnGuardarEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardarNuevosDatos();
                }
            });
        }

        // dejo los botones listos para cuando les metamos mano
        if (btnExportarDatos != null) {
            btnExportarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EditarPerfil.this, "Exportar datos en construccion", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnImportarDatos != null) {
            btnImportarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EditarPerfil.this, "Importar datos en construccion", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cargarDatosActuales() {
        DocumentReference docRef = db.collection("Usuarios").document(userId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String nombre = documentSnapshot.getString("nombre");
                    String apellidos = documentSnapshot.getString("apellidos");
                    String username = documentSnapshot.getString("username");

                    if (nombre != null) {
                        etEditarNombre.setText(nombre);
                    }
                    if (apellidos != null) {
                        etEditarApellidos.setText(apellidos);
                    }
                    if (username != null) {
                        etEditarUsername.setText(username);
                    }

                    // cargo la foto local si la tengo guardada de antes
                    SharedPreferences prefs = getSharedPreferences("AjustesPajaros", MODE_PRIVATE);
                    String fotoLocalStr = prefs.getString("ruta_foto_perfil", "");
                    if (!fotoLocalStr.isEmpty()) {
                        Uri fotoLocalUri = Uri.parse(fotoLocalStr);
                        if (imgEditarFotoPerfil != null) {
                            imgEditarFotoPerfil.setImageURI(fotoLocalUri);
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarPerfil.this, "Fallo al cargar tus datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarNuevosDatos() {
        String nuevoNombre = etEditarNombre.getText().toString().trim();
        String nuevosApellidos = etEditarApellidos.getText().toString().trim();
        String nuevoUsername = etEditarUsername.getText().toString().trim();

        // valido con if que los datos clave esten rellenos
        if (!nuevoNombre.isEmpty() && !nuevoUsername.isEmpty()) {

            // compruebo si el usuario ha tocado la foto y tenemos una uri nueva
            if (uriFotoPillada != null) {
                // preparo la ruta en la nube dentro de la carpeta fotos_perfil
                StorageReference fotoRef = storage.getReference().child("fotos_perfil").child(userId + ".jpg");

                // subo la foto a firebase
                fotoRef.putFile(uriFotoPillada).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // si se sube bien saco la url de descarga
                        fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uriDescarga) {

                                // guardo la ruta en local con sharedpreferences
                                SharedPreferences prefs = getSharedPreferences("AjustesPajaros", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("ruta_foto_perfil", uriFotoPillada.toString());
                                editor.apply();

                                // llamo al metodo para guardar los textos y la url de la nube
                                actualizarFirestore(nuevoNombre, nuevosApellidos, nuevoUsername, uriDescarga.toString());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditarPerfil.this, "Error subiendo la foto a la nube", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // si no ha cambiado la foto actualizo solo los textos
                actualizarFirestore(nuevoNombre, nuevosApellidos, nuevoUsername, null);
            }

        } else {
            Toast.makeText(this, "El nombre y el usuario son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }

    // este metodo centraliza la subida a firestore para no repetir codigo
    private void actualizarFirestore(String nombre, String apellidos, String username, String urlFotoNube) {
        Map<String, Object> actualizaciones = new HashMap<>();
        actualizaciones.put("nombre", nombre);
        actualizaciones.put("apellidos", apellidos);
        actualizaciones.put("username", username);

        // solo actualizo la foto si me llega una url nueva
        if (urlFotoNube != null) {
            actualizaciones.put("fotoPerfilUrl", urlFotoNube);
        }

        DocumentReference docRef = db.collection("Usuarios").document(userId);
        docRef.update(actualizaciones).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditarPerfil.this, "Perfil actualizado como un campeon", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarPerfil.this, "Error al guardar los cambios en la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}