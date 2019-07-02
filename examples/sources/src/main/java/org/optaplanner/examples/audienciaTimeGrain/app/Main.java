package org.optaplanner.examples.audienciaTimeGrain.app;


import org.optaplanner.core.api.solver.Solver;

import java.net.URISyntaxException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.LocalDate;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;


public class Main {
    public static void main(String[] args) {

        /* Excel Reader */

//        ExcelReader excelReader = new ExcelReader();
//        excelReader.readExcelFile();

        /* Crear el AudienciaSchedule y el helper AudienciaCreator */

        AudienciaSchedule unsolvedAudienciaSchedule;

        unsolvedAudienciaSchedule = new AudienciaSchedule();
        AudienciaCreator audienciaCreator = new AudienciaCreator();

        /* Crear los TimeGrains */

        LocalDate startdate = LocalDate.of(2019,3,4);
        LocalDate enddate = LocalDate.of(2019,3,6);
        LocalTime startTime = LocalTime.of(9,00);
        LocalTime endTime = LocalTime.of(11, 00);

        audienciaCreator.createTimeGrainList(startdate, enddate, startTime, endTime, unsolvedAudienciaSchedule);

        /* Crear las Rooms */

        ArrayList<Room> roomList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            roomList.add(new Room( i+1));
        }
        unsolvedAudienciaSchedule.setRoomList(roomList);

        /* Crear restricciones de Rooms con TimeGrains */

        audienciaCreator.setTimeGrainRoomRestrictions(unsolvedAudienciaSchedule.getRoomList().get(0), LocalDate.of(2019,3,4), LocalTime.of(10,01), LocalTime.of(10,19), unsolvedAudienciaSchedule);


        /* Mostrar las restricciones de Rooms con TimeGrains */

//        for(TimeGrain timeGrain : unsolvedAudienciaSchedule.getTimeGrainList()){
//            if(!timeGrain.getProhibitedRooms().isEmpty()){
//                for(Room room : timeGrain.getProhibitedRooms()){
//                    System.out.println("TimeGrain " + timeGrain.getDateTimeString() + " has prohibited Room " + room.getIdRoom());
//                }
//            }
//        }

        /* Crear Jueces */

        Juez juez1 = new Juez(1, "Juan Perez");
        Juez juez2 = new Juez(2, "Roberto Gimenez");

        /* Crear los AudienciaAssignments y Audiencias */

        List<AudienciaAssignment> assignmentList = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            AudienciaAssignment audienciaAssignment = new AudienciaAssignment();
            audienciaAssignment.setAudiencia(new Audiencia(i+1,4, juez1));
            audienciaAssignment.setId(i);
            assignmentList.add(audienciaAssignment);
        }
        for(int i = 3; i < 6; i++){
            AudienciaAssignment audienciaAssignment = new AudienciaAssignment();
            audienciaAssignment.setAudiencia(new Audiencia(i+1,3, juez2));
            audienciaAssignment.setId(i);
            assignmentList.add(audienciaAssignment);
        }
        unsolvedAudienciaSchedule.setAudienciaAssignmentList(assignmentList);

        /* Constraint Configuration */

        unsolvedAudienciaSchedule.setConstraintConfiguration(new AudienciaScheduleConstraintConfiguration());

        /* Solver */

        SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource("org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml");
        Solver<AudienciaSchedule> solver = solverFactory.buildSolver();
        AudienciaSchedule solvedAudienciaSchedule = solver.solve(unsolvedAudienciaSchedule);
        System.out.println(solvedAudienciaSchedule);

    }
}
