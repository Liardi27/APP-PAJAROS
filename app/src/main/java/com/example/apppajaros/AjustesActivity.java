package com.example.apppajaros;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AjustesActivity extends AppCompatActivity {

    private EditText etNombre, etApellidos, etUsername;
    private Button btnGuardarAjustes;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        etNombre = findViewById(R.id.etEditNombre);
        etApellidos = findViewById(R.id.etEditApellidos);
        etUsername = findViewById(R.id.etEditUsername);
        btnGuardarAjustes = findViewById(R.id.btnGuardarAjustes);

        cargarDatosActuales();

        btnGuardarAjustes.setOnClickListener(v -> guardarDatosFirebase());
    }

    private void cargarDatosActuales() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("Usuarios").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etNombre.setText(doc.getString("nombre"));
                        etApellidos.setText(doc.getString("apellidos"));
                        etUsername.setText(doc.getString("username"));
                    }
                });
        }
    }

    private void guardarDatosFirebase() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        if (nombre.isEmpty() || apellidos.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("Usuarios").document(user.getUid());
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("nombre", nombre);
            updates.put("apellidos", apellidos);
            updates.put("username", username);

            docRef.update(updates).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                finish(); // Cierra y vuelve al perfil
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
