package com.example.semauleo.globalparkmeters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Doc on 19/05/2017.
 */

public class multa{

    private String matricula;
    private String fecha;
    private String importe;
    private String pagada;
    private String motivo;

    public multa(){
        super();
    }

    public multa(String ma, String f, String i, String p, String mo){
        super();
        this.matricula = ma;
        this.fecha = f;
        this.importe = i;
        this.pagada = p;
        this.motivo = mo;
    }

    public void setMatricula(String ma){
        this.matricula = ma;
    }

    public String getMatricula(){
        return this.matricula;
    }

    public void setFecha(String f){
        this.fecha = f;
    }

    public String getFecha(){
        return this.fecha;
    }

    public void setImporte(String i){
        this.importe = i;
    }

    public String getImporte(){
        return this.importe;
    }

    public void setPagada(String p){
        this.pagada = p;
    }

    public String getPagada(){
        return this.pagada;
    }

    public void setMotivo(String mo){
        this.motivo = mo;
    }

    public String getMotivo(){
        return this.motivo;
    }
}