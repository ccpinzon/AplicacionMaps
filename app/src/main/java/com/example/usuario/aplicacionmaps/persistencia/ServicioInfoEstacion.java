package com.example.usuario.aplicacionmaps.persistencia;

import com.example.usuario.aplicacionmaps.logic.Estacion;
import com.example.usuario.aplicacionmaps.logic.EstacionInformacion;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Usuario on 22/03/2017.
 */

public interface ServicioInfoEstacion {
    @GET("testWebService/estacion.php")
    Call<EstacionInformacion> getInfoEstacion(@QueryMap Map<String, String> idEstacion);
}
