package org.optaplanner.examples.audienciaTimeGrain.persistence;

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
    private String directory;

    public XMLImporter(AudienciaSchedule audienciaSchedule, String directory){
        this.schedule = audienciaSchedule;
        this.directory = directory;
    }

    public AudienciaSchedule importar(){
        startingDate = schedule.getFechaCorrida();

        final File folder = new File(directory);
        listFilesForFolder(folder);

        return schedule;
    }

    private void listFilesForFolder(final File folder) {
        File[] fileEntry = folder.listFiles();
        File lastFile = null;
        if(directory.contains("Benchmark")){
            for (File file : fileEntry) {
                String[] stringDate = file.getName().split("-");
                LocalDate date = LocalDate.of(Integer.parseInt(stringDate[0]), Integer.parseInt(stringDate[1]), Integer.parseInt(stringDate[2]));
                if(!schedule.getFechaCorrida().isAfter(date)){
                    continue;
                }
                if(lastFile == null){
                    lastFile = file;
                }else {
                    if(file.lastModified() < lastFile.lastModified()){
                        lastFile = file;
                    }
                }
            }
        } else{
            for (File file : fileEntry) {
                if(lastFile != null){
                    if(file.lastModified()> lastFile.lastModified()){
                        lastFile = file;
                    }
                } else {
                    lastFile = file;
                }

            }
        }
        importOneFile(lastFile);
    }

    private void importOneFile(File file) {
        JAXBContext jaxbContext;
        if(file != null){
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

                if(directory.contains("Benchmark")){
                    file.delete();
                }

            } catch (JAXBException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No existen soluciones anteriores, se procedera con el siguiente paso");
        }



    }

    private void createSolution(AudienciaSchedule audienciaSchedule){

        List<Audiencia> newAudienciaList = new ArrayList<>();
        List<AudienciaAssignment> newAudienciaAssignmentList = new ArrayList<>();

        for(AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){

            Audiencia audienciaNueva = audienciaAssignment.getAudiencia();

            List<Juez> juezToAdd = new ArrayList<>();

            for(Juez juez : audienciaNueva.getJuezList()){
                for(Juez juezexistente : schedule.getJuezList()){
                    if(juez.getIdJuez() == juezexistente.getIdJuez()){
                        juezToAdd.add(juezexistente);
                        break;
                    }
                }
            }
            audienciaNueva.setJuezList(juezToAdd);

            List<Defensor> defensorToAdd = new ArrayList<>();
            for(Defensor defensor : audienciaNueva.getDefensorList()){
                for (Defensor defensorexistente : schedule.getDefensorList()){
                    if(defensor.getIdDefensor().equals(defensorexistente.getIdDefensor())){
                        defensorToAdd.add(defensorexistente);
                        break;
                    }
                }
            }
            audienciaNueva.setDefensorList(defensorToAdd);

            List<Fiscal> fiscalToAdd = new ArrayList<>();
            for(Fiscal fiscal : audienciaNueva.getFiscalList()){
                for(Fiscal fiscalexistente : schedule.getFiscalList()) {
                    if (fiscal.getIdFiscal() == fiscalexistente.getIdFiscal()) {
                        fiscalToAdd.add(fiscalexistente);
                        break;
                    }
                }
            }
            audienciaNueva.setFiscalList(fiscalToAdd);


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
            if (audienciaAssignment.getStartingTimeGrain().getDate().compareTo(startingDate) < 0){
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
            for (Juez juez : audienciaAssignment.getAudiencia().getJuezList()){
                checkJuez(juez);
            }
            for (Defensor defensor : audienciaAssignment.getAudiencia().getDefensorList()){
                checkDefensor(defensor);
            }
            for (Fiscal fiscal : audienciaAssignment.getAudiencia().getFiscalList()){
                checkFiscal(fiscal);
            }
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
