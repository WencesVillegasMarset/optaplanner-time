package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class Room {

    /* Variables */

    @PlanningId
    private int idRoom;
    private String nombreRoom;
    private boolean usable;
    private boolean almaFuerte = false;
    private boolean boulogne = false;

    /* Constructor */

    public Room(Integer idRoom, String nombreRoom, int ubicacion){
        this.idRoom = idRoom;
        this.nombreRoom = nombreRoom;
    }

    public Room(){}

    /* Setters y Getters */

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public String getNombreRoom() {
        return nombreRoom;
    }

    public void setNombreRoom(String nombreRoom) {
        this.nombreRoom = nombreRoom;
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

    public boolean isUsable() {
        return usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

}
