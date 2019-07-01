package org.optaplanner.examples.audienciaTimeGrain.app;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.Day;
import org.optaplanner.examples.audienciaTimeGrain.domain.TimeGrain;
import org.optaplanner.examples.audienciaTimeGrain.domain.Room;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/* Clase helper */

public class AudienciaCreator {

    /* Crear TimeGrain list */

    public void createTimeGrainList(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, AudienciaSchedule audienciaSchedule) {

        /* Crea la lista de startingMinuteOfDayOptions */

        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long timeGrainsPerDay = minutes / TimeGrain.GRAIN_LENGTH_IN_MINUTES;
        long timeGrainListSize = timeGrainsPerDay * days;
//        System.out.println(timeGrainListSize);
        int[] startingMinuteOfDayOptions = new int[(int)timeGrainsPerDay];
        int j = startTime.getHour()*60;
        for(int i=0; i< timeGrainsPerDay; i++){
            startingMinuteOfDayOptions[i] = j;
            j+= TimeGrain.GRAIN_LENGTH_IN_MINUTES;
        }

        /* Crea los TimeGrains y Days */

        List<Day> dayList = new ArrayList<>((int)timeGrainListSize);
        long dayId = 0;
        Day day = null;
        List<TimeGrain> timeGrainList = new ArrayList<>((int)timeGrainListSize);
        int startingDay = startDate.getDayOfYear();
        for (int i = 0; i < timeGrainListSize; i++) {
            TimeGrain timeGrain = new TimeGrain();
            timeGrain.setIdTimeGrain(i);
            timeGrain.setGrainIndex(i);
            int dayOfYear = (i / startingMinuteOfDayOptions.length) + startingDay;
            if (day == null || day.getDayOfYear() != dayOfYear) {
                day = new Day();
                day.setIdDay(dayId);
                day.setDayOfYear(dayOfYear);
                dayId++;
                dayList.add(day);
            }
            timeGrain.setDay(day);
            int startingMinuteOfDay = startingMinuteOfDayOptions[i % startingMinuteOfDayOptions.length];
            timeGrain.setStartingMinuteOfDay(startingMinuteOfDay);
            timeGrainList.add(timeGrain);
        }
        audienciaSchedule.setDayList(dayList);
        audienciaSchedule.setTimeGrainList(timeGrainList);
    }

    /* Agrega las restricciones de Rooms a los TimeGrains deseados */

    public void setTimeGrainRoomRestrictions(Room room, LocalDate date, LocalTime startTime, LocalTime endTime, AudienciaSchedule audienciaSchedule){
        for (TimeGrain timeGrain : audienciaSchedule.getTimeGrainList()){
            if (timeGrain.getDay().toDate().isEqual(date)){
                if ((timeGrain.getTime().isAfter(startTime) || timeGrain.getTime().plusMinutes((long)TimeGrain.GRAIN_LENGTH_IN_MINUTES).isAfter(startTime) || timeGrain.getTime().equals(startTime)) && timeGrain.getTime().isBefore(endTime)){
                    timeGrain.addProhibitedRooms(room);
                }
            }
        }
    }


}
