package org.optaplanner.examples.audienciaTimeGrain.persistence;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.helper.JuezTimeGrainRestrictionLoader;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;

public class ExcelSolutionFileIO implements SolutionFileIO<AudienciaSchedule> {

    private ExcelReader excelReader = new ExcelReader();



    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    @Override
    public AudienciaSchedule read(File inputSolutionFile) {
        String[] dateString = inputSolutionFile.getName().split("-");
        excelReader.setDate(LocalDate.of(Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]), Integer.parseInt(dateString[2].substring(0, 2))));
        AudienciaSchedule solucion = excelReader.read(inputSolutionFile);
        XMLImporter xmlImporter = new XMLImporter(solucion, "data/audienciaschedulingBenchmark");
        solucion = xmlImporter.importar();
        JuezTimeGrainRestrictionLoader juezRestrictionLoader = new JuezTimeGrainRestrictionLoader();
        solucion = juezRestrictionLoader.loadRestrictions(solucion);
        return solucion;
    }

    @Override
    public void write(AudienciaSchedule solution, File outputSolutionFile) {
        XMLExporter xmlExporter = new XMLExporter("data/audienciaschedulingBenchmark/");
        try {
            xmlExporter.write(solution);
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
