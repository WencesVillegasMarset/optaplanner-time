package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Defensor{

    /* Variables */

    private int idDefensor;
    private String nombreDefensor;

    /* Constructor */

    public Defensor(int idDefensor, String nombreDefensor) {
        this.idDefensor = idDefensor;
        this.nombreDefensor = nombreDefensor;
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
}