package com.example.usuario.aplicacionmaps.persistencia;

import com.example.usuario.aplicacionmaps.logic.Servicio;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Usuario on 22/03/2017.
 */

public interface ServicioInfoServicio {
    @GET("testWebService/servicioestacion.php")
    Call<List<Servicio>> getServicios(@QueryMap Map<String, String> idEstacion);
}
