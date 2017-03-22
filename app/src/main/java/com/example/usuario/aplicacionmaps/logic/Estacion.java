package com.example.usuario.aplicacionmaps.logic;

/**
 * Created by Usuario on 21/03/2017.
 */

public class Estacion {

    private int id_estacion;
    private double latitud_estacion;
    private double longitud_estacion;
    private String nombre_estacion;
    private int precio_diesel;
    private int precio_corriente;
    private int precio_gnv;
    private int precio_extra;
    private String marca_mayorista;

    public int getId_estacion() {
        return id_estacion;
    }

    public double getLatitud_estacion() {
        return latitud_estacion;
    }

    public double getLongitud_estacion() {
        return longitud_estacion;
    }

    public String getNombre_estacion() {
        return nombre_estacion;
    }

    public int getPrecio_diesel() {
        return precio_diesel;
    }

    public int getPrecio_corriente() {
        return precio_corriente;
    }

    public int getPrecio_gnv() {
        return precio_gnv;
    }

    public int getPrecio_extra() {
        return precio_extra;
    }

    public String getMarca_mayorista() {
        return marca_mayorista;
    }
}
