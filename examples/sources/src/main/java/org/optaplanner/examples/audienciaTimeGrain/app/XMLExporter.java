package org.optaplanner.examples.audienciaTimeGrain.app;


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
        Day dayCorrida = null;

        for(Day day : audienciaSchedule.getDayList()){
            if(day.toDate().isEqual(fechaCorrida)){
                dayCorrida = day;
                break;
            }
        }

        for(AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            if(audienciaAssignment.getStartingTimeGrain().getDay().getIdDay() == dayCorrida.getIdDay() + 1 || audienciaAssignment.getStartingTimeGrain().getDay().getIdDay() == dayCorrida.getIdDay() + 2){
                audienciaAssignment.setPinned(true);
            }
            
            int idDayPedido = 0;
            for (Day day : audienciaSchedule.getDayList()){
                if(day.toDate().isEqual(audienciaAssignment.getFechaPedido())){
                    idDayPedido = (int)day.getIdDay();
                }
            }
            
            if(audienciaAssignment.getAudiencia().getTipo().getTiempoFijacion() == dayCorrida.getIdDay() - idDayPedido){
                audienciaAssignment.setPinned(true);
            }
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(AudienciaSchedule.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        String fileName;
        int counter = 1;
        do{
            fileName = fechaCorrida.toString() +"-number-" + counter;
            counter++;
        }while (new File(directory + fileName).exists());


        marshaller.marshal(audienciaSchedule, new File(directory + fileName));
    }

}