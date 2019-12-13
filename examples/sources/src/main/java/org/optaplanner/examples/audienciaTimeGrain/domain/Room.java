package org.optaplanner.examples.audienciaTimeGrain.domain;

public class Room {

    /* Variables */

    private int idRoom;
    private String nombreRoom;
    private boolean almaFuerte;
    private boolean boulogne;
//    private int ubicacion;

    /* Constructor */

    public Room(Integer idRoom, String nombreRoom, int ubicacion){
        this.idRoom = idRoom;
        this.nombreRoom = nombreRoom;
//        this.ubicacion = ubicacion;
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

//    public int getUbicacion() {
//        return ubicacion;
//    }
//
//    public void setUbicacion(int ubicacion) {
//        this.ubicacion = ubicacion;
//    }
}
