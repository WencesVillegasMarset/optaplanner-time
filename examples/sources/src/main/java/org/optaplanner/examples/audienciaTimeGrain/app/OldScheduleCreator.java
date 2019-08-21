package org.optaplanner.examples.audienciaTimeGrain.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class OldScheduleCreator {
    public static void main(String[] args){

        AudienciaSchedule solvedAudienciaSchedule = null;

        OldScheduler excelReader = new OldScheduler();
        File excelFile = new File("data/2019-8-2.xlsx");
        if(excelFile.exists()){
            solvedAudienciaSchedule = excelReader.read(excelFile);
        }

        SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource("org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml");
        Solver<AudienciaSchedule> solver = solverFactory.buildSolver();

        solvedAudienciaSchedule = solver.solve(solvedAudienciaSchedule);

    }
}
