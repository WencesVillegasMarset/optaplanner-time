package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.audienciaTimeGrain.solver")

public class AudienciaScheduleConstraintConfiguration {

    public static final String ROOM_CONFLICT = "Room conflict";
    public static final String ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS = "One TimeGrain break between two consecutive meetings";


    @ConstraintWeight(ROOM_CONFLICT)
    private HardMediumSoftScore roomConflict = HardMediumSoftScore.ofHard(1);

    @ConstraintWeight(ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS)
    private HardMediumSoftScore oneTimeGrainBreakBetweenTwoConsecutiveMeetings = HardMediumSoftScore.ofSoft(100);


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

}
