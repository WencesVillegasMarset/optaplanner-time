package org.optaplanner.examples.audienciaTimeGrain.helper;

import org.optaplanner.examples.audienciaTimeGrain.domain.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HistrogramHelper {

    public static void main(String[] args){

        AudienciaSchedule schedule = null;
        LocalDate dia = LocalDate.of(2019,4,3);
        String directory = "data/histograma/audienciascheduling/2019-04-03-number-1";

        File file = new File(directory);
        JAXBContext jaxbContext;

        int[] salas = {500, 499, 0, 456, 498, 502, 458, 454, 501, 497, 457, 521, 451, 504, 503, 455, 875, 452, 484, 469, 314};

        try {
            jaxbContext = JAXBContext.newInstance(AudienciaSchedule.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            schedule = (AudienciaSchedule) jaxbUnmarshaller.unmarshal(file);


        } catch (JAXBException e) {
            e.printStackTrace();
        }
        System.out.println(schedule.getAudienciaAssignmentList().size());
        HashMap<String, Integer> mapa = new HashMap<String, Integer>();
        List<AudienciaAssignment> audienciaAssignmentList = schedule.getAudienciaAssignmentList().stream().filter(audienciaAssignment -> audienciaAssignment.getFechaPedido().isEqual(dia)).collect(Collectors.toList());
        System.out.println(audienciaAssignmentList.size());
        for(AudienciaAssignment audienciaAssignment : audienciaAssignmentList){
            boolean jump = false;
            for(int i = 0; i < salas.length; i++){
                if(audienciaAssignment.getRoom().getIdRoom() == salas[i]){
                    jump = true;
                    break;
                }
            }

            if(jump){
                continue;
            }

            String date = audienciaAssignment.getStartingTimeGrain().getDate().toString();
            if(mapa.containsKey(date)){
                Integer integer = mapa.get(date);
                mapa.put(date, integer + 1);
            } else {
                mapa.put(date, 1);
            }

        }

        for(String localDate : mapa.keySet()){
            System.out.println(localDate + ", " + mapa.get(localDate));

        }

    }
}
