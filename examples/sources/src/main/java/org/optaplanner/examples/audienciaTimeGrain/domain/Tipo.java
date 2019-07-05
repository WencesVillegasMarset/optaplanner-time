package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Tipo {

    /* Variables */

    private int  idTipo;
    private String nombreTipo;

    /* Constructor */

    public Tipo(int idTipo, String nombreTipo) {
        this.idTipo = idTipo;
        this.nombreTipo = nombreTipo;
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
}
