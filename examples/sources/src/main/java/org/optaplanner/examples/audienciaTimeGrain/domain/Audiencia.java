package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Audiencia {
    private int idAudiencia;
    private int numTimeGrains;
    private Tipo tipo;
    private Juez juez;

    public Audiencia(int idAudiencia, int numTimeGrains, Tipo tipo){
        this.idAudiencia = idAudiencia;
        this.numTimeGrains = numTimeGrains;
        this.tipo = tipo;
    }
    public Audiencia(int idAudiencia, int numTimeGrains, Juez juez){
        this.idAudiencia = idAudiencia;
        this.numTimeGrains = numTimeGrains;
        this.juez = juez;
    }

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
}
