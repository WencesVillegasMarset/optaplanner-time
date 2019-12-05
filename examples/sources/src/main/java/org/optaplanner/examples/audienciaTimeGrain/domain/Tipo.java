package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Tipo {

    /* Variables */

    private int  idTipo;
    private String nombreTipo;
    private int tiempoRealizacionMinimo = 2;
    private int tiempoRealizacionMaximo;
    private int tiempoFijacion;

    /* Constructor */

    public Tipo(int idTipo, String nombreTipo, int tiempoRealizacionMinimo, int tiempoRealizacionMaximo, int tiempoFijacion) {
        this.idTipo = idTipo;
        this.nombreTipo = nombreTipo;
        this.tiempoRealizacionMinimo = tiempoRealizacionMinimo;
        this.tiempoRealizacionMaximo = tiempoRealizacionMaximo;
        this.tiempoFijacion = tiempoFijacion;
    }

    public Tipo(){}

    /* Setters y Getters */

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public int getTiempoRealizacionMinimo() {
        return tiempoRealizacionMinimo;
    }

    public void setTiempoRealizacionMinimo(int tiempoRealizacionMinimo) {
        this.tiempoRealizacionMinimo = tiempoRealizacionMinimo;
    }

    public int getTiempoRealizacionMaximo() {
        return tiempoRealizacionMaximo;
    }

    public void setTiempoRealizacionMaximo(int tiempoRealizacionMaximo) {
        this.tiempoRealizacionMaximo = tiempoRealizacionMaximo;
    }

    public int getTiempoFijacion() {
        return tiempoFijacion;
    }

    public void setTiempoFijacion(int tiempoFijacion) {
        this.tiempoFijacion = tiempoFijacion;
    }
}
