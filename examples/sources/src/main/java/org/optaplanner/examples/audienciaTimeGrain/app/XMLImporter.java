package org.optaplanner.examples.audienciaTimeGrain.app;

import org.optaplanner.examples.audienciaTimeGrain.domain.Audiencia;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaAssignment;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.Day;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class XMLImporter {

    private AudienciaSchedule schedule;
    private LocalDate startingDate;
    private LocalDate endingDate;

    public XMLImporter(AudienciaSchedule audienciaSchedule){
        this.schedule = audienciaSchedule;
    }

    public List<AudienciaAssignment> importar(){
        startingDate = schedule.getDayList().get(0).toDate();
        endingDate = schedule.getDayList().get(0).toDate();
        for(Day day : schedule.getDayList()){
            if(day.toDate().isBefore(startingDate)){
                startingDate = day.toDate();
            }
            if(day.toDate().isAfter(endingDate)){
                endingDate = day.toDate();
            }
        }
        String stringStartingDate = startingDate.toString();


        final File folder = new File("data/audienciascheduling/");
        List<AudienciaAssignment> finalList = listFilesForFolder(folder, startingDate, endingDate);
        return finalList;
    }

    private List<AudienciaAssignment> listFilesForFolder(final File folder, LocalDate startingDate, LocalDate endingDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<AudienciaAssignment> finalList = null;

        for (final File fileEntry : folder.listFiles()) {
            String[] stringList = fileEntry.getName().split("-");
//            System.out.print(stringList[0]);
            String startingDateString = stringList[0] + '-' + stringList[1] + '-' + stringList[2];
            String endingDateString = stringList[3] + '-' + stringList[4] + '-' + stringList[5];
            LocalDate startingLocalDate = LocalDate.parse(startingDateString, formatter);
            LocalDate endingLocalDate = LocalDate.parse(endingDateString, formatter);

            if (!(endingLocalDate.isBefore(startingDate) || startingLocalDate.isAfter(endingDate))){
                List<AudienciaAssignment> audienciaAssignmentList = importOneFile(fileEntry);
                finalList.addAll(audienciaAssignmentList);
            }
        }

        return finalList;
    }

    private List<AudienciaAssignment> importOneFile(File file) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(AudienciaSchedule.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            AudienciaSchedule audienciaSchedule = (AudienciaSchedule) jaxbUnmarshaller.unmarshal(file);

            audienciaSchedule = cleanAudienciaAssignments(audienciaSchedule);

            return audienciaSchedule.getAudienciaAssignmentList();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;


    }

    private AudienciaSchedule cleanAudienciaAssignments(AudienciaSchedule audienciaSchedule){
        List<Audiencia> newAudienciaList = null;
        List<AudienciaAssignment> newAudienciaAssignmentList = null;
        for(AudienciaAssignment audienciaAssignment: audienciaSchedule.getAudienciaAssignmentList()){
            if (audienciaAssignment.getStartingTimeGrain().getDate().compareTo(startingDate) < 0 || audienciaAssignment.getStartingTimeGrain().getDate().compareTo(endingDate) > 0){
                newAudienciaAssignmentList.add(audienciaAssignment);
                newAudienciaList.add(audienciaAssignment.getAudiencia());
            }
        }

        for(AudienciaAssignment audienciaAssignment : newAudienciaAssignmentList){
            List<AudienciaAssignment> oldAudienciaAssignmentList = audienciaSchedule.getAudienciaAssignmentList();
            oldAudienciaAssignmentList.remove(audienciaAssignment);

        }

        return null;
    }

    private void compareAudienciaAssignmets(){

    }

    private void compare(){

    }

}
