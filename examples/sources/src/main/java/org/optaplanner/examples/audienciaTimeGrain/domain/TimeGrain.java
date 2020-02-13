package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;

public class TimeGrain {

    /* Granularidad de los TimeGrains */

    public static final int GRAIN_LENGTH_IN_MINUTES = 10;

    /* Variables */

    @PlanningId
    private int grainIndex; //unique
    private Day day;
    private int startingMinuteOfDay;
    private int idTimeGrain;
    private ArrayList<Room> prohibitedRooms = new ArrayList<>();

    /* Setters y Getters */

    public int getGrainIndex() {
        return grainIndex;
    }

    public int getIdTimeGrain() {
        return idTimeGrain;
    }

    public void setIdTimeGrain(int idTimeGrain) {
        this.idTimeGrain = idTimeGrain;
    }

    public void setGrainIndex(int grainIndex) {
        this.grainIndex = grainIndex;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public int getStartingMinuteOfDay() {
        return startingMinuteOfDay;
    }

    public ArrayList<Room> getProhibitedRooms() {
        return prohibitedRooms;
    }

    public void addProhibitedRooms(Room prohibitedRoom) {
        this.prohibitedRooms.add(prohibitedRoom);
    }

    public void setStartingMinuteOfDay(int startingMinuteOfDay) {
        this.startingMinuteOfDay = startingMinuteOfDay;
    }

    public void setProhibitedRooms(ArrayList<Room> prohibitedRooms) {
        this.prohibitedRooms = prohibitedRooms;
    }


    /* Helper functions */

    public LocalDate getDate() {
        return day.toDate();
    } //Devuelve la fecha como LocalDate

    public LocalTime getTime() {
        return LocalTime.of(startingMinuteOfDay / 60, startingMinuteOfDay % 60);
    } //Devuelve la hora como LocalTime

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(getDate(), getTime());
    } //Devuelve fecha y hora como LocalDateTime

    public String getTimeString() {
        int hourOfDay = startingMinuteOfDay / 60;
        int minuteOfHour = startingMinuteOfDay % 60;
        return (hourOfDay < 10 ? "0" : "") + hourOfDay
                + ":" + (minuteOfHour < 10 ? "0" : "") + minuteOfHour;
    } //Devuelve hora como String


    public String getDateTimeString() {
        return day.getDateString() + " " + getTimeString();
    } //Devuelve fecha y hora como String

    @Override
    public String toString() {
        return grainIndex + "(" + getDateTimeString() + ")";
    } //toString
}
