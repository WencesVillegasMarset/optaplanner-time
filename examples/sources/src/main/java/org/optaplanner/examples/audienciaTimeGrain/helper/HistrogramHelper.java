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
        LocalDate dia = LocalDate.of(2019,4,1);
        String directory = "data/histograma/2019-04-01-scheduled";

        File file = new File(directory);
        JAXBContext jaxbContext;



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
            boolean jump = true;
            for(Room room : schedule.getPossibleRooms()){
                if(audienciaAssignment.getRoom().getIdRoom() == room.getIdRoom()){
                    jump = false;
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

        int counter = 0;
        for(String localDate : mapa.keySet()){
            System.out.println(localDate + ", " + mapa.get(localDate));
            counter += mapa.get(localDate);
        }
        System.out.println(counter);

    }
}
