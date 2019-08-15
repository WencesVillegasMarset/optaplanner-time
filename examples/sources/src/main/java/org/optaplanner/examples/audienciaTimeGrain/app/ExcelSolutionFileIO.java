package org.optaplanner.examples.audienciaTimeGrain.app;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;

public class ExcelSolutionFileIO implements SolutionFileIO<AudienciaSchedule> {

    private ExcelReader excelReader = new ExcelReader();


    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    @Override
    public AudienciaSchedule read(File inputSolutionFile) {
        AudienciaSchedule solucion = excelReader.read(inputSolutionFile);
        XMLImporter xmlImporter = new XMLImporter(solucion, "data/audienciaschedulingBenchmark");
        solucion = xmlImporter.importar();
        return solucion;
    }

    @Override
    public void write(AudienciaSchedule solution, File outputSolutionFile) {
        XMLExporter xmlExporter = new XMLExporter("data/audienciaschedulingBenchmark/");
        try {
            xmlExporter.write(solution);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
