package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Audiencia {

    /* Variables */

    private int idAudiencia;
    private int numTimeGrains; //Cantidad de TimeGrains que necesita
    private Tipo tipo; //Tipo de Audiencia
    private Juez juez; //Juez
    private Defensor defensor; //Defensor
    private Fiscal fiscal; //Fiscal

    /* Constructors */

    public Audiencia(int idAudiencia, int numTimeGrains, Tipo tipo, Juez juez, Defensor defensor, Fiscal fiscal){
        this.idAudiencia = idAudiencia;
        this.numTimeGrains = numTimeGrains;
        this.tipo = tipo;
        this.juez = juez;
        this.defensor = defensor;
        this.fiscal = fiscal;
    }

    public Audiencia(){}

    /* Setters y Getters */

    public int getIdAudiencia() {
        return idAudiencia;
    }

    public void setIdAudiencia(int idAudiencia) {
        this.idAudiencia = idAudiencia;
    }

    public int getNumTimeGrains() {
        return numTimeGrains;
    }

    public void setNumTimeGrains(int numTimeGrains) {
        this.numTimeGrains = numTimeGrains;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Juez getJuez() {
        return juez;
    }

    public void setJuez(Juez juez) {
        this.juez = juez;
    }

    public Defensor getDefensor() {
        return defensor;
    }

    public void setDefensor(Defensor defensor) {
        this.defensor = defensor;
    }

    public Fiscal getFiscal() {
        return fiscal;
    }

    public void setFiscal(Fiscal fiscal) {
        this.fiscal = fiscal;
    }
}
