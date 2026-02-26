package com.example.apppajaros;

import java.util.ArrayList;

public class ListarRegistros {

    public static ArrayList<String> categorias = new ArrayList<>();

    public static void setCategorias(String categoria){
        categorias.add(categoria);
    }

}
