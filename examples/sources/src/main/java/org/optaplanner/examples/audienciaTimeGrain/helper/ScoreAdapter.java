package org.optaplanner.examples.audienciaTimeGrain.helper;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ScoreAdapter extends XmlAdapter<String, BendableScore>{
    @Override
    public BendableScore unmarshal(String v) throws Exception {
        String[] strings = v.split("/");
        BendableScore score = BendableScore.ofUninitialized(Integer.parseInt(strings[0]), new int[]{Integer.parseInt(strings[1]), Integer.parseInt(strings[2])}, new int[]{Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5])});
        return score;
    }

    @Override
    public String marshal(BendableScore v) throws Exception {
        return v.getInitScore() + "/" + v.getHardScore(0) + "/" + v.getHardScore(1) + "/" + v.getSoftScore(0) + "/" + v.getSoftScore(1) + "/" + v.getSoftScore(2);
    }
}
