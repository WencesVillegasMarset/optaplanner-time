package org.optaplanner.examples.audiencia-timegrain.domain;

//nuestro planning entity, que tiene un juez, room y timeslot.

public class Room {
    private Integer idRoom;

    public Room(Integer idRoom){
        this.idRoom = idRoom;
    }

    public Integer getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(Integer idRoom) {
        this.idRoom = idRoom;
    }
}
