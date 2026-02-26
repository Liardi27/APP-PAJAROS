package com.example.apppajaros;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Esta es nuestra central de datos local.
 * Aqui es donde guardamos lo que queremos que sobreviva aunque no haya internet.
 */
public class Sqlite extends SQLiteOpenHelper {

    // Instanciamos Gson SQLite no entiende de listas, solo de texto.
    // Con esto convertimos los Arrays de Firebase en un simple String de texto plano.
    private Gson gson = new Gson();

    public Sqlite(@Nullable Context context) {
        // Le damos nombre al archivo (.db) y subimos a la version 3 para que entren las dos tablas.
        super(context, "PajarosFavoritos.db", null, 3);
    }

    // Este metodo solo se ejecuta la PRIMERA VEZ que se instala la app.
    // Crea el esqueleto de nuestras tablas.
    @Override
    public void onCreate(SQLiteDatabase db) {

        // TABLA DE PAJAROS: Solo guardamos los favoritos.
        // Usamos TEXT para casi todo y INTEGER para los booleanos (0 o 1).
        String sqlPajaros = "CREATE TABLE IF NOT EXISTS pajaros (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID interno de SQLite
                "id_firebase TEXT, " + // El ID que manda en la nube
                "fecha_guardado_local TEXT, " +
                "titulo TEXT, " +
                "nombre_cientifico TEXT, " +
                "descripcion TEXT, " +
                "descripcion_corta TEXT, " +
                "envergadura TEXT, " +
                "cuando_migran TEXT, " +
                "ruta_audio TEXT, " +
                "ruta_portada TEXT, " +
                "como_se_alimentan TEXT, " +
                "fecha_modificacion TEXT, " +
                "autor TEXT, " +
                "migrante INTEGER, " + // 0 = false, 1 = true
                "dimorfismo_sexual INTEGER, " +
                "colores TEXT, " +
                "donde_migran TEXT, " +
                "etiquetas TEXT, " +
                "editores TEXT, " +
                "imagenes_hembra TEXT, " +
                "rutas_imagenes TEXT)";

        // TABLA DE USUARIOS: Para que el perfil no tarde una eternidad en cargar desde Firebase.
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_firebase TEXT, " +
                "nombre TEXT, " +
                "apellidos TEXT, " +
                "nombre_de_usuario TEXT, " +
                "articulos_creados TEXT, " +
                "articulos_participado TEXT, " +
                "imagenes_subidas TEXT)";

        // Ejecutamos las consultas para grabar las tablas en el disco duro del movil.
        db.execSQL(sqlPajaros);
        db.execSQL(sqlUsuarios);
        Log.d("SQLITE_DON_VIEJO", "Base de datos lista con tablas de pajaros y usuarios");
    }

    // Si cambias la version (de 3 a 4, por ejemplo), esto borra todo y lo vuelve a crear.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pajaros");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }

    // --- METODOS DE PAJAROS (LA LOGICA DEL TOGGLE) ---

    // Este es el corazon de los favoritos: Si no existe lo guarda, si existe lo borra.
    public boolean gestionarFavorito(EntidadPajaro p) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ahoraEsFavorito = false;

        // 1. Buscamos si el pajaro ya esta en la tabla usando su ID unico de Firebase.
        Cursor c = db.query("pajaros", null, "id_firebase = ?", new String[]{p.getId()}, null, null, null);

        // 2. LOGICA DE BORRADO: Si el cursor tiene mas de 0 filas, es que ya estaba.
        if (c.getCount() > 0) {
            db.delete("pajaros", "id_firebase = ?", new String[]{p.getId()});
            ahoraEsFavorito = false;
            Log.d("SQLITE", "Pajaro eliminado para no ocupar sitio de mas");
        }

        // 3. LOGICA DE GUARDADO: Si el cursor es 0, es que el usuario lo quiere guardar.
        if (c.getCount() == 0) {
            ContentValues v = new ContentValues();

            // Metemos los datos basicos
            v.put("id_firebase", p.getId());
            v.put("fecha_guardado_local", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            v.put("titulo", p.getTitulo());
            v.put("autor", p.getAutor());

            // Convertimos booleanos de Java a enteros de SQLite (1 o 0).
            v.put("migrante", p.isMigrante() ? 1 : 0);
            v.put("dimorfismo_sexual", p.isDimorfismoSexual() ? 1 : 0);

            // LA MAGIA DE GSON: Pasamos las listas (ArrayList) a un String JSON.
            // Asi SQLite lo guarda como un texto largo sin rechistar.
            v.put("colores", gson.toJson(p.getColores()));
            v.put("etiquetas", gson.toJson(p.getEtiquetas()));
            v.put("rutas_imagenes", gson.toJson(p.getArrayRutasImagenes()));

            db.insert("pajaros", null, v);
            ahoraEsFavorito = true;
            Log.d("SQLITE", "Pajaro guardado en favoritos local");
        }

        c.close();
        db.close();
        return ahoraEsFavorito; // Devolvemos el estado final para cambiar el icono en la pantalla.
    }

    // Metodo rapido para saber si pintar el corazon relleno o vacio al cargar la pantalla.
    public boolean esFavorito(String idFirebase) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("pajaros", null, "id_firebase = ?", new String[]{idFirebase}, null, null, null);
        boolean existe = (c.getCount() > 0);
        c.close();
        db.close();
        return existe;
    }

    // --- METODOS DE USUARIO (PERFIL) ---

    // Este metodo es inteligente: Si el usuario ya existe, lo actualiza. Si no, lo crea.
    public void guardarOActualizarUsuario(Usuario u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        // Preparamos los campos del perfil.
        v.put("nombre", u.getNombre());
        v.put("apellidos", u.getApellidos());
        v.put("nombre_de_usuario", u.getNombreDeUsuario());

        // Guardamos sus listas de participacion tambien como texto JSON.
        v.put("articulos_creados", gson.toJson(u.getArticulosCreados()));
        v.put("articulos_participado", gson.toJson(u.getArticulosParticipoado()));

        Cursor c = db.query("usuarios", null, "id_firebase = ?", new String[]{u.getId()}, null, null, null);

        // Si ya lo tenemos, usamos UPDATE para no crear registros basura.
        if (c.getCount() > 0) {
            db.update("usuarios", v, "id_firebase = ?", new String[]{u.getId()});
            Log.d("SQLITE", "Perfil de usuario actualizado");
        }

        // Si es la primera vez que inicia sesion, usamos INSERT.
        if (c.getCount() == 0) {
            v.put("id_firebase", u.getId());
            db.insert("usuarios", null, v);
            Log.d("SQLITE", "Nuevo usuario guardado en local");
        }

        c.close();
        db.close();
    }

    // Este sirve para rellenar los campos de la pantalla de Ajustes/Perfil sin ir a internet.
    public Usuario obtenerUsuarioLocal(String idFirebase) {
        SQLiteDatabase db = this.getReadableDatabase();
        Usuario u = null;
        Cursor c = db.query("usuarios", null, "id_firebase = ?", new String[]{idFirebase}, null, null, null);

        // Si el cursor tiene datos, montamos el objeto Usuario de vuelta.
        if (c.moveToFirst()) {
            u = new Usuario();
            u.setId(c.getString(c.getColumnIndexOrThrow("id_firebase")));
            u.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
            u.setApellidos(c.getString(c.getColumnIndexOrThrow("apellidos")));
            u.setNombreDeUsuario(c.getString(c.getColumnIndexOrThrow("nombre_de_usuario")));
        }

        c.close();
        db.close();
        return u;
    }
}