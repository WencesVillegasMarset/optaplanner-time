package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


@PlanningEntity
public class AudienciaAssignment {

    /* Variables */

    private Audiencia audiencia;
    private Room room;
    private TimeGrain startingTimeGrain;
    private int id;
    private boolean pinned = false;

    /* Setters y Getters */

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setAudiencia(Audiencia audiencia){
        this.audiencia = audiencia;
    }

    public Audiencia getAudiencia(){
        return audiencia;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @PlanningVariable(valueRangeProviderRefs = {"roomRange"})
    public Room getRoom() {
        return room;
    }

    public void setStartingTimeGrain(TimeGrain startingTimeGrain){
        this.startingTimeGrain = startingTimeGrain;
    }

    @PlanningVariable(valueRangeProviderRefs = {"timeGrainRange"})
    public TimeGrain getStartingTimeGrain(){
        return startingTimeGrain;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @PlanningPin
    public boolean isPinned(){
        return pinned;
    }

    /* Helper functions */

    /* Calcula si dos AudienciaAssignments se superponen temporalmente */
    public int calculateOverlap(AudienciaAssignment other) {
        if (startingTimeGrain == null || other.getStartingTimeGrain() == null) {
            return 0;
        }
        int start = startingTimeGrain.getGrainIndex();
        int end = start + audiencia.getNumTimeGrains();
        int otherStart = other.startingTimeGrain.getGrainIndex();
        int otherEnd = otherStart + other.audiencia.getNumTimeGrains();

        if (end < otherStart) {
            return 0;
        } else if (otherEnd < start) {
            return 0;
        }
        return Math.min(end, otherEnd) - Math.max(start, otherStart);
    }

    /* Devuelve el index del ultimo TimeGrain que utiliza */
    public Integer getLastTimeGrainIndex() {
        if (startingTimeGrain == null) {
            return null;
        }
        return startingTimeGrain.getGrainIndex() + audiencia.getNumTimeGrains() - 1;
    }

    /* Devuelve el index del primer TimeGrain que utiliza */
    public int getStartingTimeGrainIndex(){
        return startingTimeGrain.getGrainIndex();
    }

    /* Devuelve el tiempo en el que finaliza en un String */
    public String getFinishingTimeString(){
        int hourOfDay = (startingTimeGrain.getStartingMinuteOfDay() + audiencia.getNumTimeGrains()*TimeGrain.GRAIN_LENGTH_IN_MINUTES) / 60;
        int minuteOfHour = (startingTimeGrain.getStartingMinuteOfDay() + audiencia.getNumTimeGrains()*TimeGrain.GRAIN_LENGTH_IN_MINUTES) % 60;
        return (hourOfDay < 10 ? "0" : "") + hourOfDay
                + ":" + (minuteOfHour < 10 ? "0" : "") + minuteOfHour;
    }

    /* Devuelve true si existen violaciones de restricciones de Rooms y TimeGrains */
    public boolean timeGrainRoomRestriction(TimeGrain timeGrain){
        boolean respuesta = false;
//        for (TimeGrain timeGrain : timeGrainArrayList){
            if (timeGrain.getGrainIndex() >= startingTimeGrain.getGrainIndex() && timeGrain.getGrainIndex() <= getLastTimeGrainIndex()){
                for(Room timeGrainRoom : timeGrain.getProhibitedRooms()){
                    if (this.room == timeGrainRoom){
                        respuesta = true;
                    }
                }

            }
//        }
        return respuesta;
    }

    /* Devuelve el id del Juez de la audiencia */
    public int getJuezId(){
        return this.audiencia.getJuez().getIdJuez();
    }

    public Juez getJuez(){
        return this.audiencia.getJuez();
    }

    /* Devuelve el id del Defensor de la audiencia */
    public Defensor getDefensor(){ return this.audiencia.getDefensor();}

    /* Devuelve el id del Fiscal de la audiencia */
    public int getFiscalId(){ return this.audiencia.getFiscal().getIdFiscal();}

    public Fiscal getFiscal(){
        return this.audiencia.getFiscal();
    }

    /* Devuelve el id del Tipo de la audiencia */
    public int getTipo(){ return this.audiencia.getTipo().getIdTipo();}

    /* toString */
    public String toString(){
        return "Id: " + getId();
    }

    /* Calculate if there's a break between two assignments */
    public boolean isThereABreak(AudienciaAssignment other){
        boolean response = false;
        int secondGrain = this.getStartingTimeGrain().getGrainIndex();
        int firstGrain = other.getStartingTimeGrain().getGrainIndex();
        int resta = secondGrain - firstGrain;

        int minutes = resta * TimeGrain.GRAIN_LENGTH_IN_MINUTES;

        int secondGrainMinute = this.getStartingTimeGrain().getStartingMinuteOfDay();
        int firstGrainMinute = other.getStartingTimeGrain().getStartingMinuteOfDay();
        if(secondGrainMinute - firstGrainMinute != minutes){
            response = true;
        }

        return response;
    }

    public LocalDate getFechaPedido(){
        return audiencia.getFechaPedido();
    }

    public int isMinimumStartingTime(){
        LocalDate timeGrainDate = this.startingTimeGrain.getDate();
        LocalDate pedidoDate = this.audiencia.getFechaPedido();
        long days = Math.abs(ChronoUnit.DAYS.between(pedidoDate, timeGrainDate)) + 1;
//        System.out.println(days);

        if(days > audiencia.getTipo().getTiempoRealizacionMinimo()){
            return 0;
        } else {
            return audiencia.getTipo().getTiempoRealizacionMinimo() - (int)days;
        }
    }

    public int isMaximumStartingTime(){
        LocalDate timeGrainDate = startingTimeGrain.getDate();
        LocalDate pedidoDate = audiencia.getFechaPedido();
        long days = Math.abs(ChronoUnit.DAYS.between(pedidoDate, timeGrainDate)) + 1;

        if(audiencia.getTipo().getTiempoRealizacionMaximo() == 0 || audiencia.getTipo().getTiempoRealizacionMaximo() > days){
            return 0;
        } else {
            return (int)days - audiencia.getTipo().getTiempoRealizacionMaximo();
        }

    }


}