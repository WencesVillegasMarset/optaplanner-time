package org.optaplanner.examples.audienciaTimeGrain.app;


import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaAssignment;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;


public class XMLExporter {

    public XMLExporter(){}

    public void write(AudienciaSchedule audienciaSchedule) throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = JAXBContext.newInstance(AudienciaSchedule.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        LocalDate startingDate = audienciaSchedule.getAudienciaAssignmentList().get(0).getStartingTimeGrain().getDate();
        LocalDate endingDate = audienciaSchedule.getAudienciaAssignmentList().get(0).getStartingTimeGrain().getDate();


        for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            if(audienciaAssignment.getStartingTimeGrain().getDate().isBefore(startingDate)){
                startingDate = audienciaAssignment.getStartingTimeGrain().getDate();
            }
            if(audienciaAssignment.getStartingTimeGrain().getDate().isAfter(endingDate)){
                endingDate = audienciaAssignment.getStartingTimeGrain().getDate();
            }
        }

        String fileName;
        int counter = 1;
        do{
            fileName = startingDate.toString() +"-" + endingDate.toString() + "-number-" + counter;
            counter++;
        }while (new File("data/audienciascheduling/" + fileName).exists());


        marshaller.marshal(audienciaSchedule, new File("data/audienciascheduling/" + fileName));
    }

}