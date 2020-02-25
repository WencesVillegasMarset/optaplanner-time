package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Defensor{

    /* Variables */

    private int idDefensor;
    private String nombreDefensor;
    private int idNegocio;

    /* Constructor */

    public Defensor(int idDefensor, String nombreDefensor, int idNegocio) {
        this.idDefensor = idDefensor;
        this.nombreDefensor = nombreDefensor;
        this.idNegocio = idNegocio;
    }

    public Defensor(){}

    /* Setters y Getters */

    public int getIdDefensor() {
        return idDefensor;
    }

    public void setIdDefensor(int idDefensor) {
        this.idDefensor = idDefensor;
    }

    public String getNombreDefensor() {
        return nombreDefensor;
    }

    public void setNombreDefensor(String nombreDefensor) {
        this.nombreDefensor = nombreDefensor;
    }

    public int getIdNegocio() {
        return idNegocio;
    }

    public void setIdNegocio(int idNegocio) {
        this.idNegocio = idNegocio;
    }
}