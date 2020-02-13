package org.optaplanner.examples.audienciaTimeGrain.persistence;


import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaAssignment;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.Day;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;


public class XMLExporter {

    private String directory;

    public XMLExporter(String directory){
        this.directory = directory;
    }

    public void write(AudienciaSchedule audienciaSchedule) throws JAXBException, FileNotFoundException {

        LocalDate fechaCorrida = audienciaSchedule.getFechaCorrida();

        if (this.directory.equals("data/audienciascheduling")){
            for(AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
                audienciaAssignment.setPinned(true);
            }
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(AudienciaSchedule.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        String fileName;
        if (this.directory.equals("data/audienciascheduling")){
            int counter = 1;
            do{
                fileName = fechaCorrida.toString() +"-number-" + counter;
                counter++;
            }while (new File(directory + fileName).exists());
        } else{
            fileName = "Result.xml";
            int counter = 1;
            new File(directory + fileName);
        }

        marshaller.marshal(audienciaSchedule, new File(directory + fileName));
    }

}