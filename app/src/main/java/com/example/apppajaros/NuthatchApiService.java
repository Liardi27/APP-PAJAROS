package com.example.apppajaros;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NuthatchApiService {

    // Endpoint principal para obtener la lista de aves
    @GET("v2/birds")
    Call<List<Bird>> getBirds(
            @Header("api-key") String apiKey, // Header obligatorio en Nuthatch API
            @Query("family") String family,   // Filtro opcional (ej. "Troglodytidae")
            @Query("hasImg") boolean hasImg   // Filtro opcional para asegurar que traiga foto
    );
}