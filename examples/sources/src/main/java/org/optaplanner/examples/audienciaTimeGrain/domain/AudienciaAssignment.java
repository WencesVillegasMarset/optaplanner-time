package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.sql.Time;
import java.util.ArrayList;


@PlanningEntity
public class AudienciaAssignment {
    private Audiencia audiencia;
    private Room room;
    private TimeGrain startingTimeGrain;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setStartingTimeGrain(TimeGrain startingTimeGrain){
        this.startingTimeGrain = startingTimeGrain;
    }

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

    public Integer getLastTimeGrainIndex() {
        if (startingTimeGrain == null) {
            return null;
        }
        return startingTimeGrain.getGrainIndex() + audiencia.getNumTimeGrains() - 1;
    }

    public int getStartingTimeGrainIndex(){
        return startingTimeGrain.getGrainIndex();
    }

    public String getFinishingTimeString(){
        int hourOfDay = (startingTimeGrain.getStartingMinuteOfDay() + audiencia.getNumTimeGrains()*TimeGrain.GRAIN_LENGTH_IN_MINUTES) / 60;
        int minuteOfHour = (startingTimeGrain.getStartingMinuteOfDay() + audiencia.getNumTimeGrains()*TimeGrain.GRAIN_LENGTH_IN_MINUTES) % 60;
        return (hourOfDay < 10 ? "0" : "") + hourOfDay
                + ":" + (minuteOfHour < 10 ? "0" : "") + minuteOfHour;
    }

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

    public int getJuez(){
        return this.audiencia.getJuez().getIdJuez();
    }

    @PlanningVariable(valueRangeProviderRefs = {"timeGrainRange"})
    public TimeGrain getStartingTimeGrain(){
        return startingTimeGrain;
    }

    @PlanningVariable(valueRangeProviderRefs = {"roomRange"})
    public Room getRoom() {
        return room;
    }


}