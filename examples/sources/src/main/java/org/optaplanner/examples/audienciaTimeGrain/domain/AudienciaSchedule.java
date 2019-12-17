package org.optaplanner.examples.audienciaTimeGrain.domain;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.audienciaTimeGrain.helper.LocalDateAdapter;
import org.optaplanner.examples.audienciaTimeGrain.helper.ScoreAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@PlanningSolution
@XmlRootElement(name = "AudienciaSchedule")
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

    private LocalDate fechaCorrida;

    @ProblemFactCollectionProperty
    private List<Day> dayList;

    @ProblemFactCollectionProperty
    private List<Juez> juezList;

    @ProblemFactCollectionProperty
    private List<Audiencia> audienciaList;

    @ProblemFactCollectionProperty
    private List<Tipo> tipoList;

    @ProblemFactCollectionProperty
    private List<Defensor> defensorList;

    @ProblemFactCollectionProperty
    private List<Fiscal> fiscalList;

    @ProblemFactCollectionProperty
    private List<TimeGrain> timeGrainList;

    @ProblemFactCollectionProperty
    private List<Room> roomList;

    @ProblemFactCollectionProperty
    private List<Querellante> querellanteList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Asesor> asesorList = new ArrayList<>();

    @ValueRangeProvider(id = "roomRange")
    private List<Room> possibleRooms;

    @PlanningEntityCollectionProperty
    private List<AudienciaAssignment> audienciaAssignmentList;

    @PlanningScore(bendableHardLevelsSize = 2, bendableSoftLevelsSize = 3)
    private BendableScore score;

    private Table<Juez, Day, Integer> table = HashBasedTable.create();

    /* Setters y Getters */

//    public void createTable() {
//        for(Juez juez : this.juezList){
//            for(Day day : this.dayList){
//                if (table.get(juez, day) == null){
//                    int timeGrains = this.audienciaAssignmentList.stream().filter(a -> a.getStartingTimeGrain().getDay() == day && a.getAudiencia().getJuezList().contains(juez)).mapToInt(a -> a.getAudiencia().getNumTimeGrains()).sum();
//                    table.put(juez, day, timeGrains);
//                } else {
//                    table.put(juez, day, table.get(juez, day) )
//                }
//            }
//        }
//    }

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

    @XmlElement(name = "AudienciaAssignment")
    public List<AudienciaAssignment> getAudienciaAssignmentList() {
        return audienciaAssignmentList;
    }

    public void setAudienciaAssignmentList(List<AudienciaAssignment> audienciaAssignmentList) {
        this.audienciaAssignmentList = audienciaAssignmentList;
    }

    public List<Juez> getJuezList() {
        return juezList;
    }

    public void setJuezList(List<Juez> juezList) {
        this.juezList = juezList;
    }

    public List<Audiencia> getAudienciaList() {
        return audienciaList;
    }

    public void setAudienciaList(List<Audiencia> audienciaList) {
        this.audienciaList = audienciaList;
    }

    public List<Tipo> getTipoList() {
        return tipoList;
    }

    public void setTipoList(List<Tipo> tipoList) {
        this.tipoList = tipoList;
    }

    public List<Defensor> getDefensorList() {
        return defensorList;
    }

    public void setDefensorList(List<Defensor> defensorList) {
        this.defensorList = defensorList;
    }

    public List<Fiscal> getFiscalList() {
        return fiscalList;
    }

    public void setFiscalList(List<Fiscal> fiscalList) {
        this.fiscalList = fiscalList;
    }

    @XmlElement(name = "score")
    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getScore() {
        return score;
    }

    public void setScore(BendableScore score) {
        this.score = score;
    }

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate getFechaCorrida() {
        return fechaCorrida;
    }

    public void setFechaCorrida(LocalDate fechaCorrida) {
        this.fechaCorrida = fechaCorrida;
    }

    public List<Room> getPossibleRooms() {
        return possibleRooms;
    }

    public void setPossibleRooms(List<Room> posibleRooms) {
        this.possibleRooms = posibleRooms;
    }

    @ValueRangeProvider(id = "timeGrainRange")
    public List<TimeGrain> getPossibleTimeGrains(){
        return this.timeGrainList.stream().filter(timeGrain -> timeGrain.getDay().toDate().isBefore(this.fechaCorrida.plusDays(25))).collect(Collectors.toList());
    }

    public List<Querellante> getQuerellanteList() {
        return querellanteList;
    }

    public void setQuerellanteList(List<Querellante> querellanteList) {
        this.querellanteList = querellanteList;
    }

    public List<Asesor> getAsesorList() {
        return asesorList;
    }

    public void setAsesorList(List<Asesor> asesorList) {
        this.asesorList = asesorList;
    }

//    public int maximumWorkTimeJuez(){
//        Table<Juez, Day, Integer> table = HashBasedTable.create();
//        table.
//    }

    /* toString */

    @Override
    public String toString() {
        String response = new String();
        for (AudienciaAssignment audienciaAssignment : this.getAudienciaAssignmentList()) {
            response += "Audiencia número " + audienciaAssignment.getAudiencia().getIdAudiencia() + " desde " + audienciaAssignment.getStartingTimeGrain().getDateTimeString() + " hasta " + audienciaAssignment.getFinishingTimeString() + " in room number " + audienciaAssignment.getRoom().getNombreRoom() + '\n';
        }
        return  response;
    }
}
