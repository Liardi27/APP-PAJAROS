package com.example.apppajaros; // Cambia esto por tu paquete real

import java.io.Serializable;

public class Pajaro implements Serializable {
    private String nombre;
    private String descripcion;
    private boolean favorito;

    public Pajaro(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.favorito = false;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isFavorito() { return favorito; }
    public void setFavorito(boolean favorito) { this.favorito = favorito; }
}