package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Room {

    /* Variables */

    private Integer idRoom;
    private String nombreRoom;

    /* Constructor */

    public Room(Integer idRoom){
        this.idRoom = idRoom;
    }

    public Room(){}

    /* Setters y Getters */

    public Integer getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(Integer idRoom) {
        this.idRoom = idRoom;
    }

    public String getNombreRoom() {
        return nombreRoom;
    }

    public void setNombreRoom(String nombreRoom) {
        this.nombreRoom = nombreRoom;
    }
}
