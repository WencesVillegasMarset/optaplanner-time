package org.optaplanner.examples.audienciaTimeGrain.domain;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Day{

    private int dayOfYear;
    private long idDay;
    private static final DateTimeFormatter DAY_FORMATTER
            = DateTimeFormatter.ofPattern("E", Locale.ENGLISH);

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

    public String getDateString() {
        return DAY_FORMATTER.format(toDate());
    }

    public LocalDate toDate() {
        return LocalDate.ofYearDay(LocalDate.now().getYear(), dayOfYear);
    }

    @Override
    public String toString() {
        return getDateString();
    }
}