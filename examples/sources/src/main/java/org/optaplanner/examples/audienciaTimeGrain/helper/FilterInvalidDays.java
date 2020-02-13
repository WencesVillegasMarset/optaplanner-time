package org.optaplanner.examples.audienciaTimeGrain.helper;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;

import java.util.List;
import java.util.stream.Collectors;

public class FilterInvalidDays implements SelectionFilter<AudienciaSchedule, ChangeMove> {

    @Override
    public boolean accept(ScoreDirector<AudienciaSchedule> scoreDirector, ChangeMove move){
        AudienciaAssignment audienciaAssignment = (AudienciaAssignment) move.getEntity();
        TimeGrain timeGrain = (TimeGrain) move.getPlanningValues().toArray()[0];
        List<Day> dayList = scoreDirector.getWorkingSolution().getDayList().stream().filter(d -> d.toDate().isEqual(audienciaAssignment.getFechaPedido())).collect(Collectors.toList());
        if(audienciaAssignment.isMinimum((int)dayList.get(0).getIdDay(), timeGrain) == 0){
            return audienciaAssignment.isMaximum((int) dayList.get(0).getIdDay(), timeGrain) == 0;
        }
        return false;

    }
}
