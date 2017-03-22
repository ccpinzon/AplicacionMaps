package com.example.usuario.aplicacionmaps.persistencia;

import com.example.usuario.aplicacionmaps.logic.Estacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Usuario on 21/03/2017.
 */

public interface ServicioEstacion {
    @GET("testWebService/prueba.php")
    Call<List<Estacion>> getEstaciones();
}
