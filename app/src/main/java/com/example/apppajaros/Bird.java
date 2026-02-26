package com.example.apppajaros;

import java.util.List;

public class Bird {
    private String name;
    private String sciName;
    private String status;
    private List<String> images; // La API devuelve una lista de URLs de imágenes

    // Getters
    public String getName() { return name; }
    public String getSciName() { return sciName; }
    public String getStatus() { return status; }
    public List<String> getImages() { return images; }
}