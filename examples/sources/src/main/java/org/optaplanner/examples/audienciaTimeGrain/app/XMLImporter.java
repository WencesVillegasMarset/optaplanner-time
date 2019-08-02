package org.optaplanner.examples.audienciaTimeGrain.app;

import org.optaplanner.examples.audienciaTimeGrain.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class XMLImporter {

    private AudienciaSchedule schedule;
    private LocalDate startingDate;
    private LocalDate endingDate;

    public XMLImporter(AudienciaSchedule audienciaSchedule){
        this.schedule = audienciaSchedule;
    }

    public AudienciaSchedule importar(){
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

        final File folder = new File("data/audienciascheduling/");
        listFilesForFolder(folder, startingDate, endingDate);

        return schedule;
    }

    private void listFilesForFolder(final File folder, LocalDate startingDate, LocalDate endingDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


        for (final File fileEntry : folder.listFiles()) {
            String[] stringList = fileEntry.getName().split("-");
//            System.out.print(stringList[0]);
            String startingDateString = stringList[0] + '-' + stringList[1] + '-' + stringList[2];
            String endingDateString = stringList[3] + '-' + stringList[4] + '-' + stringList[5];
            LocalDate startingLocalDate = LocalDate.parse(startingDateString, formatter);
            LocalDate endingLocalDate = LocalDate.parse(endingDateString, formatter);

            if (!(endingLocalDate.isBefore(startingDate) || startingLocalDate.isAfter(endingDate))){
                importOneFile(fileEntry);
            }
        }

    }

    private void importOneFile(File file) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(AudienciaSchedule.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            AudienciaSchedule audienciaSchedule = (AudienciaSchedule) jaxbUnmarshaller.unmarshal(file);

//            System.out.println(audienciaSchedule.toString());

//            for(AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
//                System.out.println();
//            }

            audienciaSchedule = cleanAudienciaAssignments(audienciaSchedule);

            compare(audienciaSchedule);

            createSolution(audienciaSchedule);

        } catch (JAXBException e) {
            e.printStackTrace();
        }


    }

    private void createSolution(AudienciaSchedule audienciaSchedule){

        List<Audiencia> newAudienciaList = new ArrayList<>();
        List<AudienciaAssignment> newAudienciaAssignmentList = new ArrayList<>();

        for(AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){

            Audiencia audienciaNueva = audienciaAssignment.getAudiencia();

            for(Juez juezexistente : schedule.getJuezList()){
                if(audienciaNueva.getJuez().getIdJuez() == juezexistente.getIdJuez()){
                    audienciaNueva.setJuez(juezexistente);
                    break;
                }
            }

            for(Defensor defensorexistente : schedule.getDefensorList()){
                if(audienciaNueva.getDefensor().getIdDefensor().equals(defensorexistente.getIdDefensor())){
                    audienciaNueva.setDefensor(defensorexistente);
                    break;
                }
            }

            for(Fiscal fiscalexistente : schedule.getFiscalList()){
                if(audienciaNueva.getFiscal().getIdFiscal() == fiscalexistente.getIdFiscal()){
                    audienciaNueva.setFiscal(fiscalexistente);
                    break;
                }
            }

            for(Tipo tipoexistente : schedule.getTipoList()){
                if(audienciaNueva.getTipo().getIdTipo() == tipoexistente.getIdTipo()){
                    audienciaNueva.setTipo(tipoexistente);
                    break;
                }
            }

            audienciaAssignment.setAudiencia(audienciaNueva);

            for(Room roomexistente : schedule.getRoomList()){
               if(audienciaAssignment.getRoom().getIdRoom() == roomexistente.getIdRoom()){
                   audienciaAssignment.setRoom(roomexistente);
                   break;
               }
            }

            for(TimeGrain timeGrain : schedule.getTimeGrainList()){
                if(timeGrain.getDate().isEqual(audienciaAssignment.getStartingTimeGrain().getDate()) && timeGrain.getStartingMinuteOfDay() == audienciaAssignment.getStartingTimeGrain().getStartingMinuteOfDay()){
                    audienciaAssignment.setStartingTimeGrain(timeGrain);
                    break;
                }
            }

            audienciaAssignment.setPinned(true);

            newAudienciaList.add(audienciaNueva);
            newAudienciaAssignmentList.add(audienciaAssignment);
        }

        List<Audiencia> existingAudienciaList = schedule.getAudienciaList();
        List<AudienciaAssignment> existingAudienciaAssignmentList = schedule.getAudienciaAssignmentList();

        for(Audiencia audiencia : newAudienciaList){
            existingAudienciaList.add(audiencia);
        }

        for(AudienciaAssignment audienciaAssignment : newAudienciaAssignmentList){
            existingAudienciaAssignmentList.add(audienciaAssignment);
        }

        schedule.setAudienciaList(existingAudienciaList);
        schedule.setAudienciaAssignmentList(existingAudienciaAssignmentList);

    }

    private AudienciaSchedule cleanAudienciaAssignments(AudienciaSchedule audienciaSchedule){
        List<Audiencia> newAudienciaList = new ArrayList<>();
        List<AudienciaAssignment> newAudienciaAssignmentList = new ArrayList<>();
        for(AudienciaAssignment audienciaAssignment: audienciaSchedule.getAudienciaAssignmentList()){
            if (audienciaAssignment.getStartingTimeGrain().getDate().compareTo(startingDate) < 0 || audienciaAssignment.getStartingTimeGrain().getDate().compareTo(endingDate) > 0){
                newAudienciaAssignmentList.add(audienciaAssignment);
                newAudienciaList.add(audienciaAssignment.getAudiencia());
            } else for(AudienciaAssignment audienciaAssignment1 : schedule.getAudienciaAssignmentList()){
                if(audienciaAssignment1.getId() == audienciaAssignment.getId()){
                    newAudienciaAssignmentList.add(audienciaAssignment);
                    newAudienciaList.add(audienciaAssignment.getAudiencia());
                }
            }
        }

        List<AudienciaAssignment> oldAudienciaAssignmentList = audienciaSchedule.getAudienciaAssignmentList();
        List<Audiencia> oldAudienciaList = audienciaSchedule.getAudienciaList();


        for(AudienciaAssignment audienciaAssignment : newAudienciaAssignmentList){
            oldAudienciaAssignmentList.remove(audienciaAssignment);
        }

        for (Audiencia audiencia : newAudienciaList){
            oldAudienciaList.remove(audiencia);
        }
        audienciaSchedule.setAudienciaList(oldAudienciaList);
        audienciaSchedule.setAudienciaAssignmentList(oldAudienciaAssignmentList);

        return audienciaSchedule;
    }

    private void compare(AudienciaSchedule audienciaSchedule){

        for(AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            checkJuez(audienciaAssignment.getAudiencia().getJuez());
            checkDefensor(audienciaAssignment.getAudiencia().getDefensor());
            checkFiscal(audienciaAssignment.getAudiencia().getFiscal());
            checkRoom(audienciaAssignment.getRoom());
            checkTipo(audienciaAssignment.getAudiencia().getTipo());
        }

    }

    private void checkJuez(Juez juezNuevo){
        List<Juez> juezList = schedule.getJuezList();
        boolean juezExists = false;
        for(Juez juez : juezList){
            if(juez.getIdJuez() == juezNuevo.getIdJuez()){
                juezExists = true;
                break;
            }
        }

        if(!juezExists){
            juezList.add(juezNuevo);
            schedule.setJuezList(juezList);
        }
    }

    private void checkDefensor(Defensor defensorNuevo){
        List<Defensor> defensorList = schedule.getDefensorList();
        boolean defensorExists = false;
        for(Defensor defensor : defensorList){
            if(defensor.getIdDefensor().equals(defensorNuevo.getIdDefensor())){
                defensorExists = true;
                break;
            }
        }

        if(!defensorExists){
            defensorList.add(defensorNuevo);
            schedule.setDefensorList(defensorList);
        }
    }

    private void checkFiscal(Fiscal fiscalNuevo){
        List<Fiscal> fiscalList = schedule.getFiscalList();
        boolean fiscalExists = false;
        for(Fiscal fiscal : fiscalList){
            if(fiscal.getIdFiscal() == fiscalNuevo.getIdFiscal()){
                fiscalExists = true;
                break;
            }
        }

        if(!fiscalExists){
            fiscalList.add(fiscalNuevo);
            schedule.setFiscalList(fiscalList);
        }
    }

    private void checkRoom(Room roomNuevo){
        List<Room> roomList = schedule.getRoomList();
        boolean roomExists = false;
        for(Room room : roomList){
            if(room.getIdRoom() == roomNuevo.getIdRoom()){
                roomExists = true;
                break;
            }
        }
        if(!roomExists){
            roomList.add(roomNuevo);
            schedule.setRoomList(roomList);
        }
    }

    private void checkTipo(Tipo tipoNuevo){
        List<Tipo> tipoList = schedule.getTipoList();
        boolean tipoExists = false;
        for(Tipo tipo : tipoList){
            if(tipo.getIdTipo() == tipoNuevo.getIdTipo()){
                tipoExists = true;
                break;
            }
        }

        if(!tipoExists){
            tipoList.add(tipoNuevo);
            schedule.setTipoList(tipoList);
        }
    }



}
