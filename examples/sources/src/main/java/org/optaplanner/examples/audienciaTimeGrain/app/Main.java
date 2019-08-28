package org.optaplanner.examples.audienciaTimeGrain.app;


import org.optaplanner.core.api.solver.Solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Scanner;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;
import org.optaplanner.examples.audienciaTimeGrain.persistence.ExcelReader;
import org.optaplanner.examples.audienciaTimeGrain.persistence.XMLExporter;
import org.optaplanner.examples.audienciaTimeGrain.persistence.XMLImporter;

import javax.xml.bind.JAXBException;


public class Main {
    public static final String SOLVER_CONFIG = "org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml";

    public static final String DATA_DIR_NAME = "data/audienciascheduling";

    public static void main(String[] args) {


        String fechaDeseada;
        boolean fileExists = false;
        AudienciaSchedule solvedAudienciaSchedule = null;
        ExcelReader excelReader = new ExcelReader();
        do {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese la fecha que desea calendarizar (dd-mm-yyyy)\n");
            fechaDeseada = scanner.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate fechaDeseadaLocalDate = LocalDate.parse(fechaDeseada, formatter);
            File excelFile = new File("data/unsolved/" + fechaDeseadaLocalDate.getYear() + "-" + fechaDeseadaLocalDate.getMonthValue() + "-" + fechaDeseadaLocalDate.getDayOfMonth() + ".xlsx");
            if(excelFile.exists()){
                solvedAudienciaSchedule = excelReader.read(excelFile);
                fileExists = true;
            } else {
                System.out.print("Ingrese una fecha valida\n");
            }
        } while (!fileExists);



        boolean correctInputXML = false;
        String xmlInput;
        do {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Desea importar soluciones anteriores? S/N\n");
            xmlInput = scanner.nextLine();
            if(xmlInput.equals("S")  || xmlInput.equals("s")){
                XMLImporter xmlImporter = new XMLImporter(solvedAudienciaSchedule, DATA_DIR_NAME);
                solvedAudienciaSchedule = xmlImporter.importar();
                correctInputXML = true;
            } else if (xmlInput.equals("N")  || xmlInput.equals("n")){
                System.out.print("No se cargaran soluciones previas\n");
                correctInputXML = true;
            } else {
                System.out.print("Ingrese un caracter valido (S/N)\n");
            }
        } while (!correctInputXML);

        SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource("org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml");
        Solver<AudienciaSchedule> solver = solverFactory.buildSolver();

        solvedAudienciaSchedule = solver.solve(solvedAudienciaSchedule);

        boolean correctInput = false;
        String excelInput;
        do {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Desea generar un informe en Excel? S/N\n");
            excelInput = scanner.nextLine();
            if(excelInput.equals("S")  || excelInput.equals("s")){
                String fileName;
                int counter = 1;
                do{
                    fileName = "Excel-result-" + counter + ".xlsx";
                    counter++;
                }while (new File("data/excel/" + fileName).exists());

                excelReader.write(solvedAudienciaSchedule, new File("data/excel/" + fileName));
                correctInput = true;
            } else if (excelInput.equals("N")  || excelInput.equals("n")){
                System.out.print("No se generará un informe en Excel\n");
                correctInput = true;
            } else {
                System.out.print("Ingrese un caracter valido (S/N)\n");
            }
        } while (!correctInput);


        boolean isDone = false;
        String input;
        do {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Desea guardar la solucion? S/N\n");
            input = scanner.nextLine();
            if(input.equals("S")  || input.equals("s")){
                XMLExporter xmlExporter = new XMLExporter(DATA_DIR_NAME + "/");
                try {
                    xmlExporter.write(solvedAudienciaSchedule);
                } catch (JAXBException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                isDone = true;
            } else if (input.equals("N")  || input.equals("n")){
                System.out.print("La solucion no sera guardada\n");
                isDone = true;
            } else {
                System.out.print("Ingrese un caracter valido (S/N)\n");
            }
        } while (!isDone);


    }

}
