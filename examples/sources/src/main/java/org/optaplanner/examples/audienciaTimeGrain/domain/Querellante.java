package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Querellante {

    /* Variables */

    private int idQuerellante;
    private String nombreQuerellante;

    /* Constructor */

    public Querellante(int idQuerellante, String nombreQuerellante) {
        this.idQuerellante = idQuerellante;
        this.nombreQuerellante = nombreQuerellante;
    }

    public Querellante(){}

    /* Setters y Getters */

    public int getIdQuerellante() {
        return idQuerellante;
    }

    public void setIdQuerellante(int idQuerellante) {
        this.idQuerellante = idQuerellante;
    }

    public String getNombreQuerellante() {
        return nombreQuerellante;
    }

    public void setNombreQuerellante(String nombreQuerellante) {
        this.nombreQuerellante = nombreQuerellante;
    }
}
