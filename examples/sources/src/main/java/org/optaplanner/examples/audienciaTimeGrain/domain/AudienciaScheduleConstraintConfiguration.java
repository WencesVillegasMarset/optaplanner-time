package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.audienciaTimeGrain.solver")

public class AudienciaScheduleConstraintConfiguration {

    public long id;

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


    /* Soft Constraints */

    @ConstraintWeight(ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS)
    private HardMediumSoftScore oneTimeGrainBreakBetweenTwoConsecutiveMeetings = HardMediumSoftScore.ofSoft(100);
    @ConstraintWeight(DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE)
    private HardMediumSoftScore doAllMeetingsAsSoonAsPossible = HardMediumSoftScore.ofSoft(1);

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
