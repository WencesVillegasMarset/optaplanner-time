package org.optaplanner.examples.audienciaTimeGrain.app;


import org.optaplanner.core.api.solver.Solver;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.LocalDate;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;


public class Main {
    public static final String SOLVER_CONFIG = "org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml";

    public static void main(String[] args) {

        /* Excel Reader */
        File excelFile = new File("src/main/java/org/optaplanner/examples/audienciaTimeGrain/app/test_1.xlsx");
        ExcelReader excelReader = new ExcelReader();
        AudienciaSchedule unsolvedAudienciaSchedule = excelReader.read(excelFile);

        SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource("org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml");
        Solver<AudienciaSchedule> solver = solverFactory.buildSolver();
        AudienciaSchedule solvedAudienciaSchedule = solver.solve(unsolvedAudienciaSchedule);
        System.out.println(solvedAudienciaSchedule);

        System.exit(0);

//        /* Crear el AudienciaSchedule y el helper AudienciaCreator */
//
//        AudienciaSchedule unsolvedAudienciaSchedule;
//
//        unsolvedAudienciaSchedule = new AudienciaSchedule();
//        AudienciaCreator audienciaCreator = new AudienciaCreator();
//
//        /* Crear los TimeGrains */
//
//        LocalDate startdate = LocalDate.of(2019,3,4);
//        LocalDate enddate = LocalDate.of(2019,3,6);
//        LocalTime startTime = LocalTime.of(9,00);
//        LocalTime endTime = LocalTime.of(11, 00);
//
//        audienciaCreator.createTimeGrainList(startdate, enddate, startTime, endTime, unsolvedAudienciaSchedule);
//
//        /* Crear las Rooms */
//
//        ArrayList<Room> roomList = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            roomList.add(new Room( i+1));
//        }
//        unsolvedAudienciaSchedule.setRoomList(roomList);
//
//        /* Crear restricciones de Rooms con TimeGrains */
//
//        audienciaCreator.setTimeGrainRoomRestrictions(unsolvedAudienciaSchedule.getRoomList().get(0), LocalDate.of(2019,3,4), LocalTime.of(10,01), LocalTime.of(10,19), unsolvedAudienciaSchedule);
//
//
//        /* Mostrar las restricciones de Rooms con TimeGrains */
//
////        for(TimeGrain timeGrain : unsolvedAudienciaSchedule.getTimeGrainList()){
////            if(!timeGrain.getProhibitedRooms().isEmpty()){
////                for(Room room : timeGrain.getProhibitedRooms()){
////                    System.out.println("TimeGrain " + timeGrain.getDateTimeString() + " has prohibited Room " + room.getIdRoom());
////                }
////            }
////        }
//
//        /* Crear Jueces, Fiscales, Defensores y Tipos */
//
//        Juez juez1 = new Juez(1, "Juan Perez");
//        Juez juez2 = new Juez(2, "Roberto Gimenez");
//        List<Juez> juezList = new ArrayList<>();
//        juezList.add(juez1);
//        juezList.add(juez2);
//        unsolvedAudienciaSchedule.setJuezList(juezList);
//
//        Fiscal fiscal1 = new Fiscal(1,"Pedro Gil");
//        Fiscal fiscal2 = new Fiscal(2, "Juan Rodriguez");
//        List<Fiscal> fiscalList = new ArrayList<>();
//        fiscalList.add(fiscal1);
//        fiscalList.add(fiscal2);
//        unsolvedAudienciaSchedule.setFiscalList(fiscalList);
//
//        Defensor defensor1 = new Defensor(1, "Alberto Lopez");
//        Defensor defensor2 = new Defensor(2, "Juana Martinez");
//        List<Defensor> defensorList = new ArrayList<>();
//        defensorList.add(defensor1);
//        defensorList.add(defensor2);
//        unsolvedAudienciaSchedule.setDefensorList(defensorList);
//
//        Tipo tipo1 = new Tipo(1, "Habeas Corpus");
//        List<Tipo> tipoList = new ArrayList<>();
//        tipoList.add(tipo1);
//        unsolvedAudienciaSchedule.setTipoList(tipoList);
//
//        /* Crear los AudienciaAssignments y Audiencias */
//
//        List<AudienciaAssignment> assignmentList = new ArrayList<>();
//
//        for(int i = 0; i < 6; i++){
//            AudienciaAssignment audienciaAssignment = new AudienciaAssignment();
//            audienciaAssignment.setId(i);
//            assignmentList.add(audienciaAssignment);
//        }
//
//        Audiencia audiencia1 = new Audiencia(1, 4, tipo1, juez1, defensor1, fiscal1);
//        Audiencia audiencia2 = new Audiencia(2, 3, tipo1, juez2, defensor1, fiscal1);
//        Audiencia audiencia3 = new Audiencia(3, 2, tipo1, juez1, defensor2, fiscal2);
//        Audiencia audiencia4 = new Audiencia(4, 6, tipo1, juez2, defensor2, fiscal2);
//        Audiencia audiencia5 = new Audiencia(5, 5, tipo1, juez1, defensor2, fiscal1);
//        Audiencia audiencia6 = new Audiencia(6, 4, tipo1, juez2, defensor1, fiscal2);
//
//        List<Audiencia> audienciaList = new ArrayList<>();
//        audienciaList.add(audiencia1);
//        audienciaList.add(audiencia2);
//        audienciaList.add(audiencia3);
//        audienciaList.add(audiencia4);
//        audienciaList.add(audiencia5);
//        audienciaList.add(audiencia6);
//
//        unsolvedAudienciaSchedule.setAudienciaList(audienciaList);
//
//        assignmentList.get(0).setAudiencia(audiencia1);
//        assignmentList.get(1).setAudiencia(audiencia2);
//        assignmentList.get(2).setAudiencia(audiencia3);
//        assignmentList.get(3).setAudiencia(audiencia4);
//        assignmentList.get(4).setAudiencia(audiencia5);
//        assignmentList.get(5).setAudiencia(audiencia6);
//
//
//
//        unsolvedAudienciaSchedule.setAudienciaAssignmentList(assignmentList);
//
//        /* Constraint Configuration */
//
//        unsolvedAudienciaSchedule.setConstraintConfiguration(new AudienciaScheduleConstraintConfiguration());
//
//        /* Solver */
//
//        SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource("org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml");
//        Solver<AudienciaSchedule> solver = solverFactory.buildSolver();
//        AudienciaSchedule solvedAudienciaSchedule = solver.solve(unsolvedAudienciaSchedule);
//        System.out.println(solvedAudienciaSchedule);

    }
}
