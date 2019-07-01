package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.audienciaTimeGrain.solver")

public class AudienciaScheduleConstraintConfiguration {

    public static final String ROOM_CONFLICT = "Room conflict";
    public static final String ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS = "One TimeGrain break between two consecutive meetings";
    public static final String START_AND_END_ON_SAME_DAY = "Start and end on same day";
    public static final String DONT_GO_IN_OVERTIME = "Don't go in overtime";
    public static final String DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE = "Do all meetings as soon as possible";
    public static final String DO_NOT_CONFLICT_JUEZ = "Do not conflict Juez";
//    public static final String DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME = "Do not use room in prohibited time";


    @ConstraintWeight(ROOM_CONFLICT)
    private HardMediumSoftScore roomConflict = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DONT_GO_IN_OVERTIME)
    private HardMediumSoftScore dontGoInOvertime = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(START_AND_END_ON_SAME_DAY)
    private HardMediumSoftScore startAndEndOnSameDay = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(DO_NOT_CONFLICT_JUEZ)
    private HardMediumSoftScore dontConflictJuez = HardMediumSoftScore.ofHard(1);
//    @ConstraintWeight(DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME)
//    private HardMediumSoftScore dontConflictRoomTime = HardMediumSoftScore.ofHard(1);

    @ConstraintWeight(ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS)
    private HardMediumSoftScore oneTimeGrainBreakBetweenTwoConsecutiveMeetings = HardMediumSoftScore.ofSoft(100);
    @ConstraintWeight(DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE)
    private HardMediumSoftScore doAllMeetingsAsSoonAsPossible = HardMediumSoftScore.ofSoft(1);



    public AudienciaScheduleConstraintConfiguration() {
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

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

//    public HardMediumSoftScore getDontConflictRoomTime() {
//        return dontConflictRoomTime;
//    }
//
//    public void setDontConflictRoomTime(HardMediumSoftScore dontConflictRoomTime) {
//        this.dontConflictRoomTime = dontConflictRoomTime;
//    }
}
