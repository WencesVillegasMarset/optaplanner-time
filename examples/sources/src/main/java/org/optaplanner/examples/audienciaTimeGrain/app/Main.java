package org.optaplanner.examples.audienciaTimeGrain.app;


import org.optaplanner.core.api.solver.Solver;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.LocalDate;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.Day;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.TimeGrain;
import org.optaplanner.examples.audienciaTimeGrain.app.AudienciaCreator;

public class Main {
    public static void main(String[] args) {

        AudienciaSchedule audienciaSchedulePrueba = new AudienciaSchedule();
        AudienciaCreator audienciaCreator = new AudienciaCreator();

        LocalDate startdate = LocalDate.of(2019,3,4);
        LocalDate enddate = LocalDate.of(2019,3,6);
        LocalTime startTime = LocalTime.of(9,00);
        LocalTime endTime = LocalTime.of(11, 00);

        audienciaCreator.createTimeGrainList(startdate, enddate, startTime, endTime, audienciaSchedulePrueba);

        List<TimeGrain> timeGrainList = audienciaSchedulePrueba.getTimeGrainList();
        for (TimeGrain timeGrain: timeGrainList) {
            System.out.println(timeGrain);
        }


        // TODO CAMBIAR all ESTO


//        AudienciaSchedule unsolvedAudienciaSchedule;
//
//        unsolvedAudienciaSchedule = new AudienciaSchedule();
//
//        for(int i = 0; i < 3; i++){
//            unsolvedAudienciaSchedule.getAudienciaAssignmentList().add(new AudienciaAssignment());
//        }
//        ArrayList<Room> roomList = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            roomList.add(new Room(new Integer(i)));
//        }
//
//
//
//        unsolvedAudienciaSchedule.getRoomList().addAll(roomList);
//        SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource("audienciaScheduleSolverConfiguration.xml");
//        Solver<AudienciaSchedule> solver = solverFactory.buildSolver();
//        AudienciaSchedule solvedAudienciaSchedule = solver.solve(unsolvedAudienciaSchedule);
//        System.out.println(solvedAudienciaSchedule);

    }
}
