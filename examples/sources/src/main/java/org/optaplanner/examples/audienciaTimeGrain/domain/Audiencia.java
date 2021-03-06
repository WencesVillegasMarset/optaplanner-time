package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.examples.audienciaTimeGrain.helper.LocalDateAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Audiencia {

    /* Variables */

    private int idAudiencia;
    private int numTimeGrains; //Cantidad de TimeGrains que necesita
    private int durationMinutes;
    private LocalDate fechaRealizacion;
    private int startingMinuteOfDay;
    private Tipo tipo; //Tipo de Audiencia
    private List<Juez> juezList = new ArrayList<>(); //Juez
    private List<Defensor> defensorList = new ArrayList<>(); //Defensor
    private List<Fiscal> fiscalList = new ArrayList<>(); //Fiscal
    private LocalDate fechaPedido;
    private List<Querellante> querellanteList = new ArrayList<>();
    private List<Asesor> asesorList = new ArrayList<>();
    private boolean aLaTarde = false;
    private boolean detenido = false;
    private boolean riesgosa = false;
    private boolean externa = false;
    private boolean almaFuerte = false;
    private boolean boulogne = false;

    /* Constructors */

    public Audiencia(int idAudiencia, int numTimeGrains, Tipo tipo, Juez juez, Defensor defensor, Fiscal fiscal, Querellante querellante, Asesor asesor, LocalDate fechaPedido){
        this.idAudiencia = idAudiencia;
        this.numTimeGrains = numTimeGrains;
        this.tipo = tipo;
        this.juezList.add(juez);
        this.fiscalList.add(fiscal);
        this.defensorList.add(defensor);
        this.querellanteList.add(querellante);
        this.asesorList.add(asesor);
        this.fechaPedido = fechaPedido;
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

    public Juez getJuez(int index) {
        return juezList.get(index);
    }

    public void addJuez(Juez juez) {
        this.juezList.add(juez);
    }

    @XmlElementWrapper(name = "juezList")
    @XmlElement(name = "Juez")
    public List<Juez> getJuezList(){
        return this.juezList;
    }

    public void setJuezList (List<Juez> juezList){
        this.juezList = juezList;
    }

    public Defensor getDefensor(int index) {
        return defensorList.get(index);
    }

    public void addDefensor(Defensor defensor) {
        this.defensorList.add(defensor);
    }

    @XmlElementWrapper(name = "defensorList")
    @XmlElement(name = "Defensor")
    public List<Defensor> getDefensorList(){
        return this.defensorList;
    }

    public Fiscal getFiscal(int index) {
        return fiscalList.get(index);
    }

    public void addFiscal(Fiscal fiscal) {
        this.fiscalList.add(fiscal);
    }

    @XmlElementWrapper(name = "fiscalList")
    @XmlElement(name = "Fiscal")
    public List<Fiscal> getFiscalList(){
        return this.fiscalList;
    }


    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Querellante getQuerellante(int index){
        return this.querellanteList.get(index);
    }

    public void addQuerellante(Querellante querellante){
        this.querellanteList.add(querellante);
    }

    public Asesor getAsesor(int index){
        return this.asesorList.get(index);
    }

    public void addAsesor(Asesor asesor){
        this.asesorList.add(asesor);
    }

    @XmlElementWrapper(name = "querellanteList")
    @XmlElement(name = "Querellante")
    public List<Querellante> getQuerellanteList(){
        return this.querellanteList;
    }

    @XmlElementWrapper(name = "asesorList")
    @XmlElement(name = "Asesor")
    public List<Asesor> getAsesorList(){
        return this.asesorList;
    }

    public void setDefensorList(List<Defensor> defensorList) {
        this.defensorList = defensorList;
    }

    public void setFiscalList(List<Fiscal> fiscalList) {
        this.fiscalList = fiscalList;
    }

    public void setQuerellanteList(List<Querellante> querellanteList) {
        this.querellanteList = querellanteList;
    }

    public void setAsesorList(List<Asesor> asesorList) {
        this.asesorList = asesorList;
    }

    public boolean isaLaTarde() {
        return aLaTarde;
    }

    public void setaLaTarde(boolean aLaTarde) {
        this.aLaTarde = aLaTarde;
    }

    public boolean isDetenido() {
        return detenido;
    }

    public void setDetenido(boolean detenido) {
        this.detenido = detenido;
    }

    public boolean isRiesgosa() {
        return riesgosa;
    }

    public void setRiesgosa(boolean riesgosa) {
        this.riesgosa = riesgosa;
    }

    public boolean isExterna() {
        return externa;
    }

    public void setExterna(boolean externa) {
        this.externa = externa;
    }

    public boolean isAlmaFuerte() {
        return almaFuerte;
    }

    public void setAlmaFuerte(boolean almaFuerte) {
        this.almaFuerte = almaFuerte;
    }

    public boolean isBoulogne() {
        return boulogne;
    }

    public void setBoulogne(boolean boulogne) {
        this.boulogne = boulogne;
    }

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDate fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public int getStartingMinuteOfDay() {
        return startingMinuteOfDay;
    }

    public void setStartingMinuteOfDay(int startingMinuteOfDay) {
        this.startingMinuteOfDay = startingMinuteOfDay;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean containsJuez(int idJuez){
        return juezList.stream().anyMatch(o -> o.getIdJuez() == idJuez);
    }


}
