package org.optaplanner.examples.audienciaTimeGrain.app;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.Day;
import org.optaplanner.examples.audienciaTimeGrain.domain.TimeGrain;

import java.util.ArrayList;
import java.util.List;

public class AudienciaCreator {

    void createTimeGrainList(AudienciaSchedule audienciaSchedule, int timeGrainListSize, int[] startingMinuteOfDayOptions) {
        List<Day> dayList = new ArrayList<>(timeGrainListSize);
        long dayId = 0;
        Day day = null;
        List<TimeGrain> timeGrainList = new ArrayList<>(timeGrainListSize);
        for (int i = 0; i < timeGrainListSize; i++) {
            TimeGrain timeGrain = new TimeGrain();
            timeGrain.setIdTimeGrain(i);
            int grainIndex = i;
            timeGrain.setGrainIndex(grainIndex);
            int dayOfYear = (i / startingMinuteOfDayOptions.length) + 1;
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
}
