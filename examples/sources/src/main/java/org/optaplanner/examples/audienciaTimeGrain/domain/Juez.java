package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Juez{

    /* Variables */

    private int idJuez;
    private String nombre;

    /* Constructor */

    public Juez(int idJuez, String nombre){
        this.idJuez = idJuez;
        this.nombre = nombre;
    }

    /* Setters y Getters */

    public int getIdJuez() {
        return idJuez;
    }

    public void setIdJuez(int idJuez) {
        this.idJuez = idJuez;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
