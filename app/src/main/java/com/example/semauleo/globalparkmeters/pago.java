package com.example.semauleo.globalparkmeters;

/**
 * Created by Doc on 19/05/2017.
 */

public class pago {

    private String id;
    private String ciudad;
    private String zona;
    private String matricula;
    private String fechainicio;
    private String fechafin;
    private String importe;

    public pago(){
        super();
    }

    public pago(String id, String c, String z, String m, String im, String fi, String ff){
        super();
        this.id = id;
        this.ciudad = c;
        this.zona = z;
        this.matricula = m;
        this.fechafin = ff;
        this.fechainicio = fi;
        this.importe = im;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public void setCiudad(String c){
        this.ciudad = c;
    }

    public String getCiudad(){
        return this.ciudad;
    }

    public void setZona(String z){
        this.zona = z;
    }

    public String getZona(){
        return this.zona;
    }

    public void setMatricula(String m){
        this.matricula = m;
    }

    public String getMatricula(){
        return this.matricula;
    }

    public void setFechainicio(String fi){
        this.fechainicio = fi;
    }

    public String getFechainicio(){
        return this.fechainicio;
    }

    public void setFechafin(String ff){
        this.fechafin = ff;
    }

    public String getFechafin(){
        return this.fechafin;
    }

    public void setImporte(String i){
        this.importe = i;
    }

    public String getImporte(){
        return this.importe;
    }
}
