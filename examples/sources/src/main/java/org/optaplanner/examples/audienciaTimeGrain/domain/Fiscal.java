package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Fiscal{

    /* Variables */

    private int idFiscal;
    private String nombreFiscal;

    /* Constructor */

    public Fiscal(int idFiscal, String nombreFiscal) {
        this.idFiscal = idFiscal;
        this.nombreFiscal = nombreFiscal;
    }

    /* Setters y Getters */

    public int getIdFiscal() {
        return idFiscal;
    }

    public void setIdFiscal(int idFiscal) {
        this.idFiscal = idFiscal;
    }

    public String getNombreFiscal() {
        return nombreFiscal;
    }

    public void setNombreFiscal(String nombreFiscal) {
        this.nombreFiscal = nombreFiscal;
    }
}