package com.example.apppajaros;

import java.util.ArrayList;

public class Usuario {

    // variables privadas para que no me las toquen desde fuera
    private String id;
    private String nombre;
    private String apellidos;
    private String nombreDeUsuario;

    // las listas que luego gson me convierte a texto
    private ArrayList<String> articulosCreados;
    private ArrayList<String> ArticulosParticipoado; // mantengo la errata de firebase para que no pete
    private ArrayList<String> imagenesSubidas;

    // constructor vacio obligatorio para que firebase no se vuelva loco al descargar datos
    public Usuario() {
    }

    // --- GETTERS Y SETTERS ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombreDeUsuario() { return nombreDeUsuario; }
    public void setNombreDeUsuario(String nombreDeUsuario) { this.nombreDeUsuario = nombreDeUsuario; }

    public ArrayList<String> getArticulosCreados() { return articulosCreados; }
    public void setArticulosCreados(ArrayList<String> articulosCreados) { this.articulosCreados = articulosCreados; }

    public ArrayList<String> getArticulosParticipoado() { return ArticulosParticipoado; }
    public void setArticulosParticipoado(ArrayList<String> articulosParticipoado) { this.ArticulosParticipoado = articulosParticipoado; }

    public ArrayList<String> getImagenesSubidas() { return imagenesSubidas; }
    public void setImagenesSubidas(ArrayList<String> imagenesSubidas) { this.imagenesSubidas = imagenesSubidas; }
}