package org.optaplanner.examples.audienciaTimeGrain.helper;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaAssignment;

import java.util.Comparator;

public class AudienciaDifficultyComparator implements Comparator<AudienciaAssignment> {
    @Override
    public int compare(AudienciaAssignment o1, AudienciaAssignment o2) {
        return o1.getAudiencia().getNumTimeGrains() - o2.getAudiencia().getNumTimeGrains();
    }
}
