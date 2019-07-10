package org.optaplanner.examples.audienciaTimeGrain.domain;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Day{

    /* Variables */

    private int dayOfYear; //Dia del año
    private long idDay; //ID
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E dd-MM-yyyy", Locale.ENGLISH); //Formato de fecha

    /* Setters y Getters */

    public int getDayOfYear() {
        return dayOfYear;
    }

    public long getIdDay() {
        return idDay;
    }

    public void setIdDay(long idDay) {
        this.idDay = idDay;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    /* Helper functions */

    public String getDateString() {
        return DAY_FORMATTER.format(toDate());
    } //Devuelve la fecha en String

    public LocalDate toDate() {
        return LocalDate.ofYearDay(LocalDate.now().getYear(), dayOfYear);
    } //Devuelve la fecha en LocalDate

    @Override
    public String toString() {
        return getDateString();
    } //toString
}