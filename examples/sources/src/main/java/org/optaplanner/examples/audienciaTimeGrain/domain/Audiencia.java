package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.examples.audienciaTimeGrain.helper.LocalDateAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Audiencia {

    /* Variables */

    private int idAudiencia;
    private int numTimeGrains; //Cantidad de TimeGrains que necesita
    private Tipo tipo; //Tipo de Audiencia
    private Juez juez; //Juez
    private List<Defensor> defensor = new ArrayList<>();; //Defensor
    private Fiscal fiscal; //Fiscal
//    private int ubicacion; //Ubicacion geografica
    private LocalDate fechaPedido;

    /* Constructors */



    public Audiencia(int idAudiencia, int numTimeGrains, Tipo tipo, Juez juez, Defensor defensor, Fiscal fiscal, LocalDate fechaPedido){
        this.idAudiencia = idAudiencia;
        this.numTimeGrains = numTimeGrains;
        this.tipo = tipo;
        this.juez = juez;
        this.defensor.add(defensor);
        this.fiscal = fiscal;
//        this.ubicacion = ubicacion;
        this.fechaPedido = fechaPedido;
    }

    public Audiencia(){}

    /* Setters y Getters */

    public void addDefensor(Defensor defensor){
        this.defensor.add(defensor);
    }

    public List<Defensor> getDefensor() {
        return defensor;
    }

    public void setDefensor(List<Defensor> defensor) {
        this.defensor = defensor;
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


    public Fiscal getFiscal() {
        return fiscal;
    }

    public void setFiscal(Fiscal fiscal) {
        this.fiscal = fiscal;
    }

//    public int getUbicacion() {
//        return ubicacion;
//    }
//
//    public void setUbicacion(int ubicacion) {
//        this.ubicacion = ubicacion;
//    }

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
}
