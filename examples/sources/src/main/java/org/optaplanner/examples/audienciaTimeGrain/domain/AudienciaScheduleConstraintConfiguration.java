package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.apache.commons.math3.fitting.HarmonicCurveFitter;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import javax.xml.bind.annotation.XmlElement;

@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.audienciaTimeGrain.solver")

public class AudienciaScheduleConstraintConfiguration {

    /* Declarations */

    //HARD - Previene el conflicto de uso de una Room mas de una vez al mismo tiempo
    public static final String ROOM_CONFLICT = "Room conflict";

    //SOFT - Inserta un TimeGrain preventivo entre cada Audiencia
    public static final String ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS = "One TimeGrain break between two consecutive meetings";

    //HARD - Las audiencias deben empezar y terminar el mismo día
    public static final String START_AND_END_ON_SAME_DAY = "Start and end on same day";

    //HARD - Asegura que el TimeGrain en el que termina la Audiencia exista, es decir, que no pase el horario estipulado
    public static final String DONT_GO_IN_OVERTIME = "Don't go in overtime";

    //SOFT - Planificar las Audiencias para tiempos mas cercanos si es posible
    public static final String DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE = "Do all meetings as soon as possible";

    //HARD - Que el Juez no este en más de una Audiencia al mismo tiempo
    public static final String DO_NOT_CONFLICT_JUEZ = "Do not conflict Juez";

    //HARD - Que no se utilice una Room en un tiempo que se determinó que no está disponible
    public static final String DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME = "Do not use room in prohibited time";

    //HARD - Que el Fiscal no este en más de una Audiencia al mismo tiempo
    public static final String DO_NOT_CONFLICT_FISCAL = "Do not conflict Fiscal";

    //HARD - Que el Defensor no este en más de una Audiencia al mismo tiempo
    public static final String DO_NOT_CONFLICT_DEFENSOR = "Do not conflict Defensor";

    //HARD - Que se respeten los breaks durante un mismo dia
    public static final String DO_NOT_USE_BREAKS = "Avoid Mid-break";

    //SOFT - Que se asignen similares cantidades de audiencias por Room
    public static final String DISTRIBUTE_WORKLOAD_FAIRLY = "Distribute workload fairly";

    //HARD - Que se resteten las ubicaciones
    public static final String RESPECT_LOCATIONS = "Respect Locations";

    //HARD - Que se respeten los tiempos minimos de realizacion
    public static final String RESPECT_MINIMUM_STARTING_TIME = "Respect Minimum Starting Time";

    //HARD - Que se respeten los tiempos maximos de realizacion
    public static final String RESPECT_MAXIMUM_STARTING_TIME = "Respect Maximum Starting Time";

    //SOFT - One TimeGrain between two consecutive audiencias with the same Juez
    public static final String ONE_TIMEGRAIN_JUEZ = "One TimeGrain Juez";

    //SOFT - One TimeGrain between two consecutive audiencias with the same Defensor
    public static final String ONE_TIMEGRAIN_DEFENSOR = "One TimeGrain Defensor";

    //SOFT - One TimeGrain between two consecutive audiencias with the same Fiscal
    public static final String ONE_TIMEGRAIN_FISCAL = "One TimeGrain Fiscal";

    //HARD - Que un Juez no tenga audiencias en distintas ubicaciones el mismo dia
    public static final String DONT_CONFLICT_JUEZ_LOCATION = "Dont conflict Juez with locations";

    //HARD - Que un Defensor no tenga audiencias en distintas ubicaciones el mismo dia
    public static final String DONT_CONFLICT_DEFENSOR_LOCATION = "Dont conflict Defensor with locations";

    //HARD - Que un Fiscal no tenga audiencias en distintas ubicaciones el mismo dia
    public static final String DONT_CONFLICT_FISCAL_LOCATION = "Dont conflict Fiscal with locations";

    /* Hard Constraints */
    @ConstraintWeight(ROOM_CONFLICT)
    private HardMediumSoftScore roomConflict = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DONT_GO_IN_OVERTIME)
    private HardMediumSoftScore dontGoInOvertime = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(START_AND_END_ON_SAME_DAY)
    private HardMediumSoftScore startAndEndOnSameDay = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DO_NOT_CONFLICT_JUEZ)
    private HardMediumSoftScore dontConflictJuez = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME)
    private HardMediumSoftScore dontConflictRoomTime = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DO_NOT_CONFLICT_FISCAL)
    private HardMediumSoftScore dontConflictFiscal = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DO_NOT_CONFLICT_DEFENSOR)
    private HardMediumSoftScore dontConflictDefensor = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DO_NOT_USE_BREAKS)
    private HardMediumSoftScore dontUseBreaks = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(RESPECT_LOCATIONS)
    private HardMediumSoftScore respectLocations = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(RESPECT_MINIMUM_STARTING_TIME)
    private HardMediumSoftScore respectMinimumStartingTime = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(RESPECT_MAXIMUM_STARTING_TIME)
    private HardMediumSoftScore respectMaximumStartingTime = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DONT_CONFLICT_JUEZ_LOCATION)
    private HardMediumSoftScore dontConflictJuezLocation = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DONT_CONFLICT_DEFENSOR_LOCATION)
    private HardMediumSoftScore dontConflictDefensorLocation = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DONT_CONFLICT_FISCAL_LOCATION)
    private HardMediumSoftScore dontConflictFiscalLocation = HardMediumSoftScore.ofHard(1);


    /* Soft Constraints */

    @ConstraintWeight(ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS)
    private HardMediumSoftScore oneTimeGrainBreakBetweenTwoConsecutiveMeetings = HardMediumSoftScore.ofSoft(100);
    @ConstraintWeight(DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE)
    private HardMediumSoftScore doAllMeetingsAsSoonAsPossible = HardMediumSoftScore.ofSoft(1);
    @ConstraintWeight(DISTRIBUTE_WORKLOAD_FAIRLY)
    private HardMediumSoftScore distributeWorkloadFairly = HardMediumSoftScore.ofSoft(1);
    @ConstraintWeight(ONE_TIMEGRAIN_JUEZ)
    private HardMediumSoftScore oneTimeGrainJuez = HardMediumSoftScore.ofSoft(100);
    @ConstraintWeight(ONE_TIMEGRAIN_DEFENSOR)
    private HardMediumSoftScore oneTimeGrainDefensor = HardMediumSoftScore.ofSoft(100);
    @ConstraintWeight(ONE_TIMEGRAIN_FISCAL)
    private HardMediumSoftScore oneTimeGrainFiscal = HardMediumSoftScore.ofSoft(100);


    /* Constructor */

    public AudienciaScheduleConstraintConfiguration() {
    }

    /* Setters y Getters */

    public HardMediumSoftScore getRoomConflict() {
        return roomConflict;
    }

    public void setRoomConflict(HardMediumSoftScore roomConflict) {
        this.roomConflict = roomConflict;
    }

    public HardMediumSoftScore getOneTimeGrainBreakBetweenTwoConsecutiveMeetings() {
        return oneTimeGrainBreakBetweenTwoConsecutiveMeetings;
    }

    public void setOneTimeGrainBreakBetweenTwoConsecutiveMeetings(HardMediumSoftScore oneTimeGrainBreakBetweenTwoConsecutiveMeetings) {
        this.oneTimeGrainBreakBetweenTwoConsecutiveMeetings = oneTimeGrainBreakBetweenTwoConsecutiveMeetings;
    }

    public HardMediumSoftScore getDontGoInOvertime() {
        return dontGoInOvertime;
    }

    public void setDontGoInOvertime(HardMediumSoftScore dontGoInOvertime) {
        this.dontGoInOvertime = dontGoInOvertime;
    }

    public HardMediumSoftScore getStartAndEndOnSameDay() {
        return startAndEndOnSameDay;
    }

    public void setStartAndEndOnSameDay(HardMediumSoftScore startAndEndOnSameDay) {
        this.startAndEndOnSameDay = startAndEndOnSameDay;
    }

    public HardMediumSoftScore getDoAllMeetingsAsSoonAsPossible() {
        return doAllMeetingsAsSoonAsPossible;
    }

    public void setDoAllMeetingsAsSoonAsPossible(HardMediumSoftScore doAllMeetingsAsSoonAsPossible) {
        this.doAllMeetingsAsSoonAsPossible = doAllMeetingsAsSoonAsPossible;
    }

    public HardMediumSoftScore getDontConflictJuez() {
        return dontConflictJuez;
    }

    public void setDontConflictJuez(HardMediumSoftScore dontConflictJuez) {
        this.dontConflictJuez = dontConflictJuez;
    }

    public HardMediumSoftScore getDontConflictRoomTime() {
        return dontConflictRoomTime;
    }

    public void setDontConflictRoomTime(HardMediumSoftScore dontConflictRoomTime) {
        this.dontConflictRoomTime = dontConflictRoomTime;
    }

    public HardMediumSoftScore getDontConflictFiscal() {
        return dontConflictFiscal;
    }

    public void setDontConflictFiscal(HardMediumSoftScore dontConflictFiscal) {
        this.dontConflictFiscal = dontConflictFiscal;
    }

    public HardMediumSoftScore getDontConflictDefensor() {
        return dontConflictDefensor;
    }

    public void setDontConflictDefensor(HardMediumSoftScore dontConflictDefensor) {
        this.dontConflictDefensor = dontConflictDefensor;
    }

    public HardMediumSoftScore getDontUseBreaks() {
        return dontUseBreaks;
    }

    public void setDontUseBreaks(HardMediumSoftScore dontUseBreaks) {
        this.dontUseBreaks = dontUseBreaks;
    }

    public HardMediumSoftScore getDistributeWorkloadFairly() {
        return distributeWorkloadFairly;
    }

    public void setDistributeWorkloadFairly(HardMediumSoftScore distributeWorkloadFairly) {
        this.distributeWorkloadFairly = distributeWorkloadFairly;
    }

    public HardMediumSoftScore getRespectLocations() {
        return respectLocations;
    }

    public void setRespectLocations(HardMediumSoftScore respectLocations) {
        this.respectLocations = respectLocations;
    }

    public HardMediumSoftScore getRespectMinimumStartingTime() {
        return respectMinimumStartingTime;
    }

    public void setRespectMinimumStartingTime(HardMediumSoftScore respectMinimumStartingTime) {
        this.respectMinimumStartingTime = respectMinimumStartingTime;
    }

    public HardMediumSoftScore getRespectMaximumStartingTime() {
        return respectMaximumStartingTime;
    }

    public void setRespectMaximumStartingTime(HardMediumSoftScore respectMaximumStartingTime) {
        this.respectMaximumStartingTime = respectMaximumStartingTime;
    }

    public HardMediumSoftScore getOneTimeGrainJuez() {
        return oneTimeGrainJuez;
    }

    public void setOneTimeGrainJuez(HardMediumSoftScore oneTimeGrainJuez) {
        this.oneTimeGrainJuez = oneTimeGrainJuez;
    }

    public HardMediumSoftScore getOneTimeGrainDefensor() {
        return oneTimeGrainDefensor;
    }

    public void setOneTimeGrainDefensor(HardMediumSoftScore oneTimeGrainDefensor) {
        this.oneTimeGrainDefensor = oneTimeGrainDefensor;
    }

    public HardMediumSoftScore getOneTimeGrainFiscal() {
        return oneTimeGrainFiscal;
    }

    public void setOneTimeGrainFiscal(HardMediumSoftScore oneTimeGrainFiscal) {
        this.oneTimeGrainFiscal = oneTimeGrainFiscal;
    }

    public HardMediumSoftScore getDontConflictJuezLocation() {
        return dontConflictJuezLocation;
    }

    public void setDontConflictJuezLocation(HardMediumSoftScore dontConflictJuezLocation) {
        this.dontConflictJuezLocation = dontConflictJuezLocation;
    }

    public HardMediumSoftScore getDontConflictDefensorLocation() {
        return dontConflictDefensorLocation;
    }

    public void setDontConflictDefensorLocation(HardMediumSoftScore dontConflictDefensorLocation) {
        this.dontConflictDefensorLocation = dontConflictDefensorLocation;
    }

    public HardMediumSoftScore getDontConflictFiscalLocation() {
        return dontConflictFiscalLocation;
    }

    public void setDontConflictFiscalLocation(HardMediumSoftScore dontConflictFiscalLocation) {
        this.dontConflictFiscalLocation = dontConflictFiscalLocation;
    }

}
