package org.optaplanner.examples.audienciaTimeGrain.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.persistence.ExcelReader;
import org.optaplanner.examples.audienciaTimeGrain.persistence.OldScheduler;

import java.io.File;
import java.time.LocalDate;

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


        String fileName = "OldResults1.xlsx";
        excelReader = new OldScheduler();
        excelReader.writer(solvedAudienciaSchedule, new File("data/" + fileName), LocalDate.of(2018,11,19), LocalDate.of(2018, 12, 9));

        fileName = "OldResults2.xlsx";
        excelReader = new OldScheduler();
        solvedAudienciaSchedule = excelReader.read(excelFile);
        solvedAudienciaSchedule = solver.solve(solvedAudienciaSchedule);
        excelReader.writer(solvedAudienciaSchedule, new File("data/" + fileName), LocalDate.of(2018,12,9), LocalDate.of(2018, 12, 29));

        fileName = "OldResults3.xlsx";
        excelReader = new OldScheduler();
        solvedAudienciaSchedule = excelReader.read(excelFile);
        solvedAudienciaSchedule = solver.solve(solvedAudienciaSchedule);
        excelReader.writer(solvedAudienciaSchedule, new File("data/" + fileName), LocalDate.of(2018,12,29), LocalDate.of(2019, 1, 19));

        fileName = "OldResults4.xlsx";
        excelReader = new OldScheduler();
        solvedAudienciaSchedule = excelReader.read(excelFile);
        solvedAudienciaSchedule = solver.solve(solvedAudienciaSchedule);
        excelReader.writer(solvedAudienciaSchedule, new File("data/" + fileName), LocalDate.of(2019,1,19), LocalDate.of(2019, 2, 9));

        fileName = "OldResults5.xlsx";
        excelReader = new OldScheduler();
        solvedAudienciaSchedule = excelReader.read(excelFile);
        solvedAudienciaSchedule = solver.solve(solvedAudienciaSchedule);
        excelReader.writer(solvedAudienciaSchedule, new File("data/" + fileName), LocalDate.of(2019,2,9), LocalDate.of(2019, 2, 28));

        fileName = "OldResults6.xlsx";
        excelReader = new OldScheduler();
        solvedAudienciaSchedule = excelReader.read(excelFile);
        solvedAudienciaSchedule = solver.solve(solvedAudienciaSchedule);
        excelReader.writer(solvedAudienciaSchedule, new File("data/" + fileName), LocalDate.of(2019,2,28), LocalDate.of(2019, 3, 19));

    }
}
