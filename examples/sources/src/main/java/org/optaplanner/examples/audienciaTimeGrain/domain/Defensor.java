package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Defensor{

    /* Variables */

    private String idDefensor;
    private String nombreDefensor;
    private int idNegocio;

    /* Constructor */

    public Defensor(String idDefensor, String nombreDefensor, int idNegocio) {
        this.idDefensor = idDefensor;
        this.nombreDefensor = nombreDefensor;
        this.idNegocio = idNegocio;
    }

    public Defensor(){}

    /* Setters y Getters */

    public String getIdDefensor() {
        return idDefensor;
    }

    public void setIdDefensor(String idDefensor) {
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