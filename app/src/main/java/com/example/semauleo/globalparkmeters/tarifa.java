package com.example.semauleo.globalparkmeters;

/**
 * Created by Doc on 19/05/2017.
 */

public class tarifa {

    private String zona;
    private Double precio;
    private String tiempo;

    public tarifa(){
        super();
    }

    public tarifa(String z, Double p, String t){
        super();
        this.zona = z;
        this.precio = p;
        this.tiempo = t;
    }

    public void setZona(String z){
        this.zona = z;
    }

    public String getZona(){
        return this.zona;
    }

    public void setPrecio(Double p){
        this.precio = p;
    }

    public Double getPrecio(){
        return this.precio;
    }

    public void setTiempo(String t){
        this.tiempo = t;
    }

    public String getTiempo(){
        return this.tiempo;
    }
}
