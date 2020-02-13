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
import org.optaplanner.examples.audienciaTimeGrain.helper.LocalDateAdapter;
import org.optaplanner.examples.audienciaTimeGrain.helper.ScoreAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@PlanningSolution
@XmlRootElement(name = "AudienciaSchedule")
public class AudienciaSchedule {

    /* Constraint Configuration */

    @ConstraintConfigurationProvider
    private AudienciaScheduleConstraintConfiguration constraintConfiguration = new AudienciaScheduleConstraintConfiguration();

    public AudienciaScheduleConstraintConfiguration getConstraintConfiguration() {
        return constraintConfiguration;
    }

    public void setConstraintConfiguration(AudienciaScheduleConstraintConfiguration constraintConfiguration) {
        this.constraintConfiguration = constraintConfiguration;
    }

    /* Variables */

    private LocalDate fechaCorrida;

    @ProblemFactCollectionProperty
    private List<Day> dayList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Juez> juezList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Audiencia> audienciaList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Tipo> tipoList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Defensor> defensorList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Fiscal> fiscalList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TimeGrain> timeGrainList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Room> roomList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Querellante> querellanteList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Asesor> asesorList = new ArrayList<>();

    @ValueRangeProvider(id = "roomRange")
    private List<Room> possibleRooms = new ArrayList<>();

    @PlanningEntityCollectionProperty
    private List<AudienciaAssignment> audienciaAssignmentList = new ArrayList<>();

    @PlanningScore(bendableHardLevelsSize = 2, bendableSoftLevelsSize = 3)
    private BendableScore score;

    /* Setters y Getters */

    @XmlElementWrapper(name = "dayList")
    @XmlElement(name = "Day")
    public List<Day> getDayList() {
        return dayList;
    }

    public void setDayList(List<Day> dayList) {
        this.dayList = dayList;
    }

    @XmlElementWrapper(name = "timeGrainList")
    @XmlElement(name = "TimeGrain")
    public List<TimeGrain> getTimeGrainList() {
        return timeGrainList;
    }

    public void setTimeGrainList(List<TimeGrain> timeGrainList) {
        this.timeGrainList = timeGrainList;
    }

    @XmlElementWrapper(name = "roomList")
    @XmlElement(name = "Room")
    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    @XmlElementWrapper(name = "audienciaAssignmentList")
    @XmlElement(name = "AudienciaAssignment")
    public List<AudienciaAssignment> getAudienciaAssignmentList() {
        return audienciaAssignmentList;
    }

    public void setAudienciaAssignmentList(List<AudienciaAssignment> audienciaAssignmentList) {
        this.audienciaAssignmentList = audienciaAssignmentList;
    }

    @XmlElementWrapper(name = "juezList")
    @XmlElement(name = "Juez")
    public List<Juez> getJuezList() {
        return juezList;
    }

    public void setJuezList(List<Juez> juezList) {
        this.juezList = juezList;
    }

    @XmlElementWrapper(name = "audienciaList")
    @XmlElement(name = "Audiencia")
    public List<Audiencia> getAudienciaList() {
        return audienciaList;
    }

    public void setAudienciaList(List<Audiencia> audienciaList) {
        this.audienciaList = audienciaList;
    }

    @XmlElementWrapper(name = "tipoList")
    @XmlElement(name = "Tipo")
    public List<Tipo> getTipoList() {
        return tipoList;
    }

    public void setTipoList(List<Tipo> tipoList) {
        this.tipoList = tipoList;
    }

    @XmlElementWrapper(name = "defensorList")
    @XmlElement(name = "Defensor")
    public List<Defensor> getDefensorList() {
        return defensorList;
    }

    public void setDefensorList(List<Defensor> defensorList) {
        this.defensorList = defensorList;
    }

    @XmlElementWrapper(name = "fiscalList")
    @XmlElement(name = "Fiscal")
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

    @XmlElementWrapper(name = "possibleRoomsList")
    @XmlElement(name = "Room")
    public List<Room> getPossibleRooms() {
        return possibleRooms;
    }

    public void setPossibleRooms(List<Room> posibleRooms) {
        this.possibleRooms = posibleRooms;
    }

    @ValueRangeProvider(id = "timeGrainRange")
    public List<TimeGrain> getPossibleTimeGrains(){
        return this.timeGrainList;
    }

    @XmlElementWrapper(name = "querellanteList")
    @XmlElement(name = "Querellante")
    public List<Querellante> getQuerellanteList() {
        return querellanteList;
    }

    public void setQuerellanteList(List<Querellante> querellanteList) {
        this.querellanteList = querellanteList;
    }

    @XmlElementWrapper(name = "asesorList")
    @XmlElement(name = "Asesor")
    public List<Asesor> getAsesorList() {
        return asesorList;
    }

    public void setAsesorList(List<Asesor> asesorList) {
        this.asesorList = asesorList;
    }


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
