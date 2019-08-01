package org.optaplanner.examples.audienciaTimeGrain.app;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import java.io.File;

public class ExcelSolutionFileIO implements SolutionFileIO<AudienciaSchedule> {

    private ExcelReader excelReader = new ExcelReader();


    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    @Override
    public AudienciaSchedule read(File inputSolutionFile) {
        return excelReader.read(inputSolutionFile);
    }

    @Override
    public void write(AudienciaSchedule solution, File outputSolutionFile) {
        excelReader.write(solution, outputSolutionFile);
    }
}
