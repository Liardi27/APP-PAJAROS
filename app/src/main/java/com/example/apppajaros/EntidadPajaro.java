package com.example.apppajaros;

import java.util.ArrayList;

public class EntidadPajaro {

        // todos los campos de texto que saco de firebase y guardo en mi movil
        private String id;
        private String titulo;
        private String nombreCientifico;
        private String descripcion;
        private String descripcionCorta;
        private String envergadura;
        private String cuandoMigran;
        private String rutaAudio;
        private String rutaImagenPortada; // asumo este nombre por tu antiguo codigo
        private String comoSeAlimentan;
        private String fechaUltimaModificacion; // asumo este nombre por tu antiguo codigo
        private String autor;

        // mis booleanos que luego paso a 0 y 1 para el sqlite
        private boolean migrante;
        private boolean dimorfismoSexual;

        // mis listas de strings que meto con la magia del gson
        private ArrayList<String> colores;
        private ArrayList<String> dondeMigran;
        private ArrayList<String> etiquetas;
        private ArrayList<String> editores;
        private ArrayList<String> imagenesHembra;
        private ArrayList<String> arrayRutasImagenes; // nombre exacto que usas en el getter

        // constructor vacio para que firebase haga su magia
        public EntidadPajaro() {
        }

        // --- GETTERS Y SETTERS ---

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getNombreCientifico() { return nombreCientifico; }
        public void setNombreCientifico(String nombreCientifico) { this.nombreCientifico = nombreCientifico; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public String getDescripcionCorta() { return descripcionCorta; }
        public void setDescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }

        public String getEnvergadura() { return envergadura; }
        public void setEnvergadura(String envergadura) { this.envergadura = envergadura; }

        public String getCuandoMigran() { return cuandoMigran; }
        public void setCuandoMigran(String cuandoMigran) { this.cuandoMigran = cuandoMigran; }

        public String getRutaAudio() { return rutaAudio; }
        public void setRutaAudio(String rutaAudio) { this.rutaAudio = rutaAudio; }

        public String getRutaImagenPortada() { return rutaImagenPortada; }
        public void setRutaImagenPortada(String rutaImagenPortada) { this.rutaImagenPortada = rutaImagenPortada; }

        public String getComoSeAlimentan() { return comoSeAlimentan; }
        public void setComoSeAlimentan(String comoSeAlimentan) { this.comoSeAlimentan = comoSeAlimentan; }

        public String getFechaUltimaModificacion() { return fechaUltimaModificacion; }
        public void setFechaUltimaModificacion(String fechaUltimaModificacion) { this.fechaUltimaModificacion = fechaUltimaModificacion; }

        public String getAutor() { return autor; }
        public void setAutor(String autor) { this.autor = autor; }

        public boolean isMigrante() { return migrante; }
        public void setMigrante(boolean migrante) { this.migrante = migrante; }

        public boolean isDimorfismoSexual() { return dimorfismoSexual; }
        public void setDimorfismoSexual(boolean dimorfismoSexual) { this.dimorfismoSexual = dimorfismoSexual; }

        public ArrayList<String> getColores() { return colores; }
        public void setColores(ArrayList<String> colores) { this.colores = colores; }

        public ArrayList<String> getDondeMigran() { return dondeMigran; }
        public void setDondeMigran(ArrayList<String> dondeMigran) { this.dondeMigran = dondeMigran; }

        public ArrayList<String> getEtiquetas() { return etiquetas; }
        public void setEtiquetas(ArrayList<String> etiquetas) { this.etiquetas = etiquetas; }

        public ArrayList<String> getEditores() { return editores; }
        public void setEditores(ArrayList<String> editores) { this.editores = editores; }

        public ArrayList<String> getImagenesHembra() { return imagenesHembra; }
        public void setImagenesHembra(ArrayList<String> imagenesHembra) { this.imagenesHembra = imagenesHembra; }

        public ArrayList<String> getArrayRutasImagenes() { return arrayRutasImagenes; }
        public void setArrayRutasImagenes(ArrayList<String> arrayRutasImagenes) { this.arrayRutasImagenes = arrayRutasImagenes; }
    }

