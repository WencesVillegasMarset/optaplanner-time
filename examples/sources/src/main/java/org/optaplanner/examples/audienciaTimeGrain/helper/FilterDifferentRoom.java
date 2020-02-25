package org.optaplanner.examples.audienciaTimeGrain.helper;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;

import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaAssignment;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.Room;

import java.util.List;

public class FilterDifferentRoom implements SelectionFilter<AudienciaSchedule, ChangeMove> {

    @Override
    public boolean accept(ScoreDirector<AudienciaSchedule> scoreDirector, ChangeMove move){
        AudienciaAssignment audienciaAssignment = (AudienciaAssignment) move.getEntity();
        Room room = (Room) move.getPlanningValues().toArray()[0];

        boolean audienciaBoulogne = audienciaAssignment.getAudiencia().isBoulogne();
        boolean audienciaAlmaFuerte = audienciaAssignment.getAudiencia().isAlmaFuerte();
        boolean roomBoulogne = room.isBoulogne();
        boolean roomAlmaFuerte = room.isAlmaFuerte();
        if (audienciaBoulogne && !roomBoulogne){
            return false;
        }
        if (audienciaAlmaFuerte && !roomAlmaFuerte){
            return false;
        }
        if ((!audienciaAlmaFuerte && !audienciaBoulogne) && (!roomAlmaFuerte && !roomBoulogne)){
            return false;
        }
        return true;
    }
}
