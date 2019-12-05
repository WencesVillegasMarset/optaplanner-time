package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Asesor {

    /* Variables */

    private int idAsesor;
    private String nombreAsesor;

    /* Constructor */

    public Asesor(int idAsesor, String nombreAsesor) {
        this.idAsesor = idAsesor;
        this.nombreAsesor = nombreAsesor;
    }

    public Asesor(){}

    /* Setters y Getters */

    public int getIdAsesor() {
        return idAsesor;
    }

    public void setIdAsesor(int idAsesor) {
        this.idAsesor = idAsesor;
    }

    public String getNombreAsesor() {
        return nombreAsesor;
    }

    public void setNombreAsesor(String nombreAsesor) {
        this.nombreAsesor = nombreAsesor;
    }
}
