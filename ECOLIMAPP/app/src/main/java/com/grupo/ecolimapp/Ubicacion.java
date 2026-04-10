package com.grupo.ecolimapp;

public class Ubicacion {

    private int idUbicacion;
    private String nombreLugar;

    public Ubicacion(int idUbicacion, String nombreLugar) {
        this.idUbicacion = idUbicacion;
        this.nombreLugar = nombreLugar;
    }

    public int getIdUbicacion() {
        return idUbicacion;
    }

    public String getNombreLugar() {
        return nombreLugar;
    }

    @Override
    public String toString() {
        return nombreLugar;
    }
}