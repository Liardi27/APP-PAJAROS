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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfil extends AppCompatActivity {

    // preparo todas mis herramientas de trabajo
    private ImageView btnVolverAtrasEditar, imgEditarFotoPerfil;
    private EditText etEditarNombre, etEditarApellidos, etEditarUsername;
    private MaterialButton btnGuardarEditar, btnExportarDatos, btnImportarDatos;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Sqlite dbLocal;
    private String userId;

    private ActivityResultLauncher<Intent> lanzadorGaleria;
    private Uri uriFotoPillada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // arranco los motores de la app
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        dbLocal = new Sqlite(this);

        // enlazo cada cosa con su id del xml
        btnVolverAtrasEditar = findViewById(R.id.btnVolverAtrasEditar);
        imgEditarFotoPerfil = findViewById(R.id.imgEditarFotoPerfil);
        etEditarNombre = findViewById(R.id.etEditarNombre);
        etEditarApellidos = findViewById(R.id.etEditarApellidos);
        etEditarUsername = findViewById(R.id.etEditarUsername);
        btnGuardarEditar = findViewById(R.id.btnGuardarEditar);
        btnExportarDatos = findViewById(R.id.btnExportarDatos);
        btnImportarDatos = findViewById(R.id.btnImportarDatos);

        // configuro el lanzador de la galeria para atrapar la foto
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

        // verifico quien soy para cargar mis cosas
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            cargarDatosActuales();
            configurarClicks();
        } else {
            // si no hay usuario cierro la pantalla por seguridad
            finish();
        }
    }

    private void configurarClicks() {
        // flecha para volver al nido
        if (btnVolverAtrasEditar != null) {
            btnVolverAtrasEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        // tocar la foto para ir a la galeria
        if (imgEditarFotoPerfil != null) {
            imgEditarFotoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    lanzadorGaleria.launch(intent);
                }
            });
        }

        // el boton principal para guardar todos los cambios
        if (btnGuardarEditar != null) {
            btnGuardarEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardarCambios();
                }
            });
        }

        // botones de gestion de datos
        if (btnExportarDatos != null) {
            btnExportarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EditarPerfil.this, "Preparando exportacion...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnImportarDatos != null) {
            btnImportarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EditarPerfil.this, "Buscando archivo para importar...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cargarDatosActuales() {
        // primero busco en mi base de datos sqlite local
        Usuario userLocal = dbLocal.obtenerUsuarioLocal(userId);
        if (userLocal != null) {
            etEditarNombre.setText(userLocal.getNombre());
            etEditarApellidos.setText(userLocal.getApellidos());
            etEditarUsername.setText(userLocal.getNombreDeUsuario());
        }

        // luego busco en firestore para estar seguro de tener lo ultimo
        db.collection("Usuarios").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String n = documentSnapshot.getString("nombre");
                    String a = documentSnapshot.getString("apellidos");
                    String u = documentSnapshot.getString("nombreDeUsuario");
                    if (n != null) etEditarNombre.setText(n);
                    if (a != null) etEditarApellidos.setText(a);
                    if (u != null) etEditarUsername.setText(u);
                }
            }
        });
    }

    private void guardarCambios() {
        String nombreVal = etEditarNombre.getText().toString().trim();
        String apellidoVal = etEditarApellidos.getText().toString().trim();
        String userVal = etEditarUsername.getText().toString().trim();

        // valido con un if que no me dejen campos vacios
        if (!nombreVal.isEmpty() && !userVal.isEmpty()) {

            // si el usuario ha elegido una foto nueva la subo primero
            if (uriFotoPillada != null) {
                StorageReference ref = storage.getReference().child("fotos_perfil/" + userId + ".jpg");
                ref.putFile(uriFotoPillada).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // ya tengo la url de la foto asi que sincronizo todo
                                sincronizarTodo(nombreVal, apellidoVal, userVal, uri.toString());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditarPerfil.this, "Fallo al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // si no hay foto nueva solo sincronizo los textos
                sincronizarTodo(nombreVal, apellidoVal, userVal, null);
            }

        } else {
            Toast.makeText(this, "Nombre y Usuario son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }

    private void sincronizarTodo(String n, String a, String u, String urlFoto) {
        // 1. PREPARO EL PAQUETE PARA FIREBASE (usando set con merge para evitar errores)
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", n);
        map.put("apellidos", a);
        map.put("nombreDeUsuario", u);
        if (urlFoto != null) {
            map.put("fotoPerfilUrl", urlFoto);
        }

        db.collection("Usuarios").document(userId).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // 2. ACTUALIZO MI SQLITE LOCAL
                Usuario user = new Usuario();
                user.setId(userId);
                user.setNombre(n);
                user.setApellidos(a);
                user.setNombreDeUsuario(u);
                dbLocal.guardarOActualizarUsuario(user);

                // 3. GUARDO LA RUTA LOCAL DE LA FOTO PARA CARGA INSTANTANEA
                if (uriFotoPillada != null) {
                    SharedPreferences.Editor editor = getSharedPreferences("AjustesPajaros", MODE_PRIVATE).edit();
                    editor.putString("ruta_foto_perfil", uriFotoPillada.toString());
                    editor.apply();
                }

                Toast.makeText(EditarPerfil.this, "Cambios guardados con exito", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarPerfil.this, "Error al conectar con el nido", Toast.LENGTH_SHORT).show();
            }
        });
    }
}