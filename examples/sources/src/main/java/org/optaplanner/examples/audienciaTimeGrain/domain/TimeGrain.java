package org.optaplanner.examples.audienciaTimeGrain.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
public class TimeGrain {

    /**
     * Time granularity is 15 minutes (which is often recommended when dealing with humans for practical purposes).
     */
    public static final int GRAIN_LENGTH_IN_MINUTES = 15;

    private int grainIndex; // unique

    private Day day;
    private int startingMinuteOfDay;
    private int idTimeGrain;

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

    public void setStartingMinuteOfDay(int startingMinuteOfDay) {
        this.startingMinuteOfDay = startingMinuteOfDay;
    }

    public LocalDate getDate() {
        return day.toDate();
    }

    public LocalTime getTime() {
        return LocalTime.of(startingMinuteOfDay / 60, startingMinuteOfDay % 60);
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(getDate(), getTime());
    }

    public String getTimeString() {
        int hourOfDay = startingMinuteOfDay / 60;
        int minuteOfHour = startingMinuteOfDay % 60;
        return (hourOfDay < 10 ? "0" : "") + hourOfDay
                + ":" + (minuteOfHour < 10 ? "0" : "") + minuteOfHour;
    }

    public String getDateTimeString() {
        return day.getDateString() + " " + getTimeString();
    }

    @Override
    public String toString() {
        return grainIndex + "(" + getDateTimeString() + ")";
    }
}
