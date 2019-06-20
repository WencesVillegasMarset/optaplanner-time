package org.optaplanner.examples.audienciaTimeGrain.app;


import org.optaplanner.core.api.solver.Solver;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.Day;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.TimeGrain;
import org.optaplanner.examples.audienciaTimeGrain.app.AudienciaCreator;

public class Main {
    public static void main(String[] args) {

        int[] startingMinuteOfDayOptions = {
                8 * 60, // 08:00
                8 * 60 + 15, // 08:15
                8 * 60 + 30, // 08:30
                8 * 60 + 45, // 08:45
                9 * 60, // 09:00
                9 * 60 + 15, // 09:15
                9 * 60 + 30, // 09:30
                9 * 60 + 45, // 09:45
                10 * 60, // 10:00
                10 * 60 + 15, // 10:15
                10 * 60 + 30, // 10:30
                10 * 60 + 45, // 10:45
                11 * 60, // 11:00
                11 * 60 + 15, // 11:15
                11 * 60 + 30, // 11:30
                11 * 60 + 45, // 11:45
                13 * 60, // 13:00
                13 * 60 + 15, // 13:15
                13 * 60 + 30, // 13:30
                13 * 60 + 45, // 13:45
                14 * 60, // 14:00
                14 * 60 + 15, // 14:15
                14 * 60 + 30, // 14:30
                14 * 60 + 45, // 14:45
                15 * 60, // 15:00
                15 * 60 + 15, // 15:15
                15 * 60 + 30, // 15:30
                15 * 60 + 45, // 15:45
                16 * 60, // 16:00
                16 * 60 + 15, // 16:15
                16 * 60 + 30, // 16:30
                16 * 60 + 45, // 16:45
                17 * 60, // 17:00
                17 * 60 + 15, // 17:15
                17 * 60 + 30, // 17:30
                17 * 60 + 45, // 17:45
        };

        AudienciaSchedule audienciaSchedulePrueba = new AudienciaSchedule();
        int timeGrainListSize = 80;
        AudienciaCreator audienciaCreator = new AudienciaCreator();
        audienciaCreator.createTimeGrainList(audienciaSchedulePrueba, timeGrainListSize, startingMinuteOfDayOptions);

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
