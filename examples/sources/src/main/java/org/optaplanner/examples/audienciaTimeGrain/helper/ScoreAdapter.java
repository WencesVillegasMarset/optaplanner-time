package org.optaplanner.examples.audienciaTimeGrain.helper;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ScoreAdapter extends XmlAdapter<String, HardMediumSoftScore>{
    @Override
    public HardMediumSoftScore unmarshal(String v) throws Exception {
        String[] strings = v.split("/");
        HardMediumSoftScore score = HardMediumSoftScore.ofUninitialized(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
        return score;
    }

    @Override
    public String marshal(HardMediumSoftScore v) throws Exception {
        return v.getInitScore() + "/" + v.getHardScore() + "/" + v.getMediumScore() + "/" + v.getSoftScore();
    }
}
