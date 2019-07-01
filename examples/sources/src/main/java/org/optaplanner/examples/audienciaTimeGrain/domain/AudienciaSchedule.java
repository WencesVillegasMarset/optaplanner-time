package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.List;


@PlanningSolution
public class AudienciaSchedule {

    /* Constraint Configuration */

    @ConstraintConfigurationProvider
    private AudienciaScheduleConstraintConfiguration constraintConfiguration;

    public AudienciaScheduleConstraintConfiguration getConstraintConfiguration() {
        return constraintConfiguration;
    }

    public void setConstraintConfiguration(AudienciaScheduleConstraintConfiguration constraintConfiguration) {
        this.constraintConfiguration = constraintConfiguration;
    }

    /* Variables */

    @ProblemFactCollectionProperty
    private List<Day> dayList;

    @ValueRangeProvider(id = "timeGrainRange")
    @ProblemFactCollectionProperty
    private List<TimeGrain> timeGrainList;

    @ValueRangeProvider(id = "roomRange")
    @ProblemFactCollectionProperty
    private List<Room> roomList;

    @PlanningEntityCollectionProperty
    private List<AudienciaAssignment> audienciaAssignmentList;

    @PlanningScore
    private HardMediumSoftScore score;

    // HACE FALTA AGREGAR COMO PROBLEM FACT A LAS AUDIENCIAS??

    /* Setters y Getters */

    public List<Day> getDayList() {
        return dayList;
    }

    public void setDayList(List<Day> dayList) {
        this.dayList = dayList;
    }

    public List<TimeGrain> getTimeGrainList() {
        return timeGrainList;
    }

    public void setTimeGrainList(List<TimeGrain> timeGrainList) {
        this.timeGrainList = timeGrainList;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public List<AudienciaAssignment> getAudienciaAssignmentList() {
        return audienciaAssignmentList;
    }

    public void setAudienciaAssignmentList(List<AudienciaAssignment> audienciaAssignmentList) {
        this.audienciaAssignmentList = audienciaAssignmentList;
    }

    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    /* toString */

    @Override
    public String toString() {
        String response = new String();
        for (AudienciaAssignment audienciaAssignment : this.getAudienciaAssignmentList()) {
            response += "Audiencia número " + audienciaAssignment.getAudiencia().getIdAudiencia() + " desde " + audienciaAssignment.getStartingTimeGrain().getDateTimeString() + " hasta " + audienciaAssignment.getFinishingTimeString() + " in room number " + audienciaAssignment.getRoom().getIdRoom() + '\n';
        }
        return  response;
    }
}
