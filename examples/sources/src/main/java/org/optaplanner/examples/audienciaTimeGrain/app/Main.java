package org.optaplanner.examples.audienciaTimeGrain.app;


import org.optaplanner.core.api.solver.Solver;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.LocalDate;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;


public class Main {
    public static void main(String[] args) {

        AudienciaSchedule unsolvedAudienciaSchedule;

        unsolvedAudienciaSchedule = new AudienciaSchedule();
        AudienciaCreator audienciaCreator = new AudienciaCreator();

        LocalDate startdate = LocalDate.of(2019,3,4);
        LocalDate enddate = LocalDate.of(2019,3,6);
        LocalTime startTime = LocalTime.of(9,00);
        LocalTime endTime = LocalTime.of(11, 00);

        audienciaCreator.createTimeGrainList(startdate, enddate, startTime, endTime, unsolvedAudienciaSchedule);

//        List<TimeGrain> timeGrainList = audienciaSchedulePrueba.getTimeGrainList();
//        for (TimeGrain timeGrain: timeGrainList) {
//            System.out.println(timeGrain);
//        }


        ArrayList<Room> roomList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            roomList.add(new Room(new Integer(i)));
        }


        List<AudienciaAssignment> assignmentList = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            AudienciaAssignment audienciaAssignment = new AudienciaAssignment();
            audienciaAssignment.setAudiencia(new Audiencia(i,3));
            assignmentList.add(audienciaAssignment);
        }
        unsolvedAudienciaSchedule.setAudienciaAssignmentList(assignmentList);
        unsolvedAudienciaSchedule.setRoomList(roomList);

        unsolvedAudienciaSchedule.setConstraintConfiguration(new AudienciaScheduleConstraintConfiguration());

        SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource("audienciaTimeGrainSolverConfig.xml");
        Solver<AudienciaSchedule> solver = solverFactory.buildSolver();
        AudienciaSchedule solvedAudienciaSchedule = solver.solve(unsolvedAudienciaSchedule);
        System.out.println(solvedAudienciaSchedule);

    }
}
