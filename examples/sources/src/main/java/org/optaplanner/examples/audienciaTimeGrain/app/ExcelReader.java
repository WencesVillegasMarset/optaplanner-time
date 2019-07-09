package org.optaplanner.examples.audienciaTimeGrain.app;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.*;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class ExcelReader extends AbstractXlsxSolutionFileIO<AudienciaSchedule>{

    @Override
    public AudienciaSchedule read(File file) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AudienciaSchedule resuelto = new AudienciaSchedulingXlsxReader(workbook).read();
        return resuelto;
    }

    private static class AudienciaSchedulingXlsxReader extends AbstractXlsxReader<AudienciaSchedule>{

        AudienciaSchedulingXlsxReader (XSSFWorkbook workbook){ super(workbook, Main.SOLVER_CONFIG);}

        public AudienciaSchedule read(){
            solution = new AudienciaSchedule();
            readConfiguration();
            readDayList();
            readRoomList();
            readJuezList();
            readFiscalList();
            readDefensorList();
            readTipoList();
            readAudienciaList();
            AudienciaSchedule solution = solve();
            return solution;
        }

        private void readConfiguration(){
            nextSheet("Configuration");
            nextRow();
            nextRow(true);
            readHeaderCell("Constraint");
            readHeaderCell("Weight");
            readHeaderCell("Description");

            AudienciaScheduleConstraintConfiguration constraintConfiguration = new AudienciaScheduleConstraintConfiguration();
            constraintConfiguration.setId(0L);

            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.ROOM_CONFLICT, hardScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.START_AND_END_ON_SAME_DAY, hardScore -> constraintConfiguration.setStartAndEndOnSameDay(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DONT_GO_IN_OVERTIME, hardScore -> constraintConfiguration.setDontGoInOvertime(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_JUEZ, hardScore -> constraintConfiguration.setDontConflictJuez(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME, hardScore -> constraintConfiguration.setDontConflictRoomTime(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_FISCAL, hardScore -> constraintConfiguration.setDontConflictFiscal(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_DEFENSOR, hardScore -> constraintConfiguration.setDontConflictDefensor(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_USE_BREAKS, hardScore -> constraintConfiguration.setDontUseBreaks(HardMediumSoftScore.ofHard(hardScore)), "");

            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS, softScore -> constraintConfiguration.setOneTimeGrainBreakBetweenTwoConsecutiveMeetings(HardMediumSoftScore.ofSoft(softScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE, softScore -> constraintConfiguration.setDoAllMeetingsAsSoonAsPossible(HardMediumSoftScore.ofSoft(softScore)), "");

//            System.out.println(constraintConfiguration.getRoomConflict());
//            System.out.println(constraintConfiguration.getStartAndEndOnSameDay());
//            System.out.println(constraintConfiguration.getDontGoInOvertime());
//            System.out.println(constraintConfiguration.getDontConflictJuez());
//            System.out.println(constraintConfiguration.getDontConflictRoomTime());
//            System.out.println(constraintConfiguration.getDontConflictFiscal());
//            System.out.println(constraintConfiguration.getDontConflictDefensor());
//            System.out.println(constraintConfiguration.getDontUseBreaks());
//            System.out.println(constraintConfiguration.getOneTimeGrainBreakBetweenTwoConsecutiveMeetings());
//            System.out.println(constraintConfiguration.getDoAllMeetingsAsSoonAsPossible());

            solution.setConstraintConfiguration(constraintConfiguration);
        }

        private void readJuezList(){
            nextSheet("Jueces");
            nextRow(false);
            readHeaderCell("Nombre Completo");
            readHeaderCell("Id");
            List<Juez> juezList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
//            System.out.println("Jueces: ");
            while (nextRow()) {
                Juez juez = new Juez();
                juez.setNombre(nextStringCell().getStringCellValue());
                juez.setIdJuez((int)nextNumericCell().getNumericCellValue());
//                if (!VALID_NAME_PATTERN.matcher(juez.getNombre()).matches()) {
//                    throw new IllegalStateException(
//                            currentPosition() + ": The person name (" + juez.getNombre()
//                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
//                }
                juezList.add(juez);
//                System.out.println(juez.getNombre() + " con id numero " + juez.getIdJuez());
            }
            solution.setJuezList(juezList);
        }

        private void readDefensorList(){
            nextSheet("Defensores");
            nextRow(false);
            readHeaderCell("Nombre Completo");
            readHeaderCell("Id");
            List<Defensor> defensorList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
//            System.out.println("Defensores: ");
            while (nextRow()) {
                Defensor defensor = new Defensor();
                String nombredefensor = nextStringCell().getStringCellValue();
                defensor.setNombreDefensor(nombredefensor);
                String id = Base64.getEncoder().encodeToString(nombredefensor.getBytes());
                defensor.setIdDefensor(id);
                defensor.setIdNegocio((int)nextNumericCell().getNumericCellValue());
//                if (!VALID_NAME_PATTERN.matcher(defensor.getNombreDefensor()).matches()) {
//                    throw new IllegalStateException(
//                            currentPosition() + ": The person name (" + defensor.getNombreDefensor()
//                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
//                }
                defensorList.add(defensor);
//                System.out.println(defensor.getNombreDefensor() + " con id numero " + defensor.getIdDefensor());
            }
            solution.setDefensorList(defensorList);
        }

        private void readFiscalList(){
            nextSheet("Fiscales");
            nextRow(false);
            readHeaderCell("Nombre Completo");
            readHeaderCell("Id");
            List<Fiscal> fiscalList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
//            System.out.println("Fiscales: ");
            while (nextRow()) {
                Fiscal fiscal = new Fiscal();
                fiscal.setNombreFiscal(nextStringCell().getStringCellValue());
                fiscal.setIdFiscal((int)nextNumericCell().getNumericCellValue());
//                if (!VALID_NAME_PATTERN.matcher(fiscal.getNombreFiscal()).matches()) {
//                    throw new IllegalStateException(
//                            currentPosition() + ": The person name (" + fiscal.getNombreFiscal()
//                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
//                }
                fiscalList.add(fiscal);
//                System.out.println(fiscal.getNombreFiscal() + " con id numero " + fiscal.getIdFiscal());
            }
            solution.setFiscalList(fiscalList);
        }

        private void readTipoList(){
            nextSheet("Tipos de Audiencia");
            nextRow(false);
            readHeaderCell("Tipo");
            readHeaderCell("Id");
            List<Tipo> tipoList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
//            System.out.println("Tipos: ");
            while (nextRow()){
                Tipo tipo = new Tipo();
                tipo.setNombreTipo(nextStringCell().getStringCellValue());
                tipo.setIdTipo((int)nextNumericCell().getNumericCellValue());
//                if (!VALID_NAME_PATTERN.matcher(tipo.getNombreTipo()).matches()) {
//                    throw new IllegalStateException(
//                            currentPosition() + ": The person name (" + tipo.getNombreTipo()
//                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
//                }
                tipoList.add(tipo);
//                System.out.println(tipo.getNombreTipo() + " con id numero " + tipo.getIdTipo());
            }
            solution.setTipoList(tipoList);
        }

        private void readDayList(){
            nextSheet("Días");
            nextRow(false);
            readHeaderCell("Día");
            readHeaderCell("Inicio");
            readHeaderCell("Fin");
            List<Day> dayList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            List<TimeGrain> timeGrainList = new ArrayList<>();
            int dayId = 0, timeGrainId = 0;
            while (nextRow()) {
                int diaLeido = LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER).getDayOfYear();
                Day day = null;
                for(Day writtenDay : dayList){
                    if(writtenDay.getDayOfYear() == diaLeido){
                        day = writtenDay;
                    }
                }
                if(day==null){
                    day = new Day();
                    day.setIdDay(dayId);
                    day.setDayOfYear(diaLeido);
                    dayList.add(day);
//                    System.out.println("Día " + day.getDateString() + day.getIdDay());
                }
                dayId++;
                LocalTime startTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                int startMinuteOfDay = startTime.getHour() * 60 + startTime.getMinute();
                int endMinuteOfDay = endTime.getHour() * 60 + endTime.getMinute();
                for (int i = 0; (endMinuteOfDay - startMinuteOfDay) > i * TimeGrain.GRAIN_LENGTH_IN_MINUTES; i++) {
                    int timeGrainStartingMinuteOfDay = i * TimeGrain.GRAIN_LENGTH_IN_MINUTES + startMinuteOfDay;
                    TimeGrain timeGrain = new TimeGrain();
                    timeGrain.setIdTimeGrain(timeGrainId);
                    timeGrain.setGrainIndex(timeGrainId);
                    timeGrain.setDay(day);
                    timeGrain.setStartingMinuteOfDay(timeGrainStartingMinuteOfDay);
                    timeGrainList.add(timeGrain);
                    timeGrainId++;
//                    System.out.println(timeGrain.getDateTimeString() + " " + timeGrain.getIdTimeGrain() + " " + timeGrain.getGrainIndex());
                }
            }
            solution.setDayList(dayList);
            solution.setTimeGrainList(timeGrainList);
        }

        private void readRoomList(){
            nextSheet("Salas");
            nextRow(false);
            readHeaderCell("Sala");
            readHeaderCell("Id");
            List<Room> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            while (nextRow()){
                Room room = new Room();
                room.setNombreRoom(nextStringCell().getStringCellValue());
                room.setIdRoom((int)nextNumericCell().getNumericCellValue());
                roomList.add(room);
//                System.out.println(room.getNombreRoom() + " con id numero " + room.getIdRoom());
            }
            solution.setRoomList(roomList);
//            for(Room room :solution.getRoomList()){
//                System.out.println(room.getNombreRoom() + " " + room.getIdRoom());
//            }

        }

        private void readAudienciaList(){
            nextSheet("Audiencias");
            nextRow(false);
            readHeaderCell("Id");
            readHeaderCell("Duración");
            readHeaderCell("Tipo");
            readHeaderCell("Juez");
            readHeaderCell("Defensor");
            readHeaderCell("Nombre Defensor");
            readHeaderCell("Fiscal");

            List<Audiencia> audienciaList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            List<AudienciaAssignment> audienciaAssignmentList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            while(nextRow()){
                Audiencia audiencia = new Audiencia();
                AudienciaAssignment audienciaAssignment = new AudienciaAssignment();
                int id = (int)nextNumericCell().getNumericCellValue();
                audiencia.setIdAudiencia(id);
                audienciaAssignment.setId(id);
                readAudienciaDuration(audiencia);
                int tipoRead = (int)nextNumericCell().getNumericCellValue();
//                System.out.println(tipoRead);
                int juezRead = (int)nextNumericCell().getNumericCellValue();
//                System.out.println(juezRead);
                int defensorRead = (int)nextNumericCell().getNumericCellValue();
//                System.out.println(defensorRead);
                String defensorNombre =  nextStringCell().getStringCellValue();
//                System.out.println(defensorNombre);
                defensorNombre = Base64.getEncoder().encodeToString(defensorNombre.getBytes());
//                System.out.println(defensorNombre);
                int fiscalRead = (int)nextNumericCell().getNumericCellValue();
//                System.out.println(fiscalRead);
                if(containsTipo(solution.getTipoList(), tipoRead)){
                    for (Tipo tipo : solution.getTipoList()) {
                        if (tipo.getIdTipo() == tipoRead){
                            audiencia.setTipo(tipo);
                            break;
                        }
                    }
                } else {
                    throw new IllegalStateException(
                            currentPosition() + ": The tipo with id (" + tipoRead
                                    + ") does not exist.");
                }
                if(containsJuez(solution.getJuezList(), juezRead)){
                    for (Juez juez : solution.getJuezList()) {
                        if (juez.getIdJuez() == juezRead){
                            audiencia.setJuez(juez);
                            break;
                        }
                    }
                } else {
                    throw new IllegalStateException(
                            currentPosition() + ": The juez with id (" + juezRead
                                    + ") does not exist.");
                }
                if(containsDefensor(solution.getDefensorList(), defensorNombre)){
                    for (Defensor defensor : solution.getDefensorList()) {
                        if (defensor.getIdDefensor().equals(defensorNombre)){
                            audiencia.setDefensor(defensor);
                            break;
                        }
                    }
                } else {
                    throw new IllegalStateException(
                            currentPosition() + ": The defensor with id (" + defensorNombre
                                    + ") does not exist.");
                }
                if(containsFiscal(solution.getFiscalList(), fiscalRead)){
                    for (Fiscal fiscal : solution.getFiscalList()) {
                        if (fiscal.getIdFiscal() == fiscalRead){
                            audiencia.setFiscal(fiscal);
                            break;
                        }
                    }
                } else {
                    throw new IllegalStateException(
                            currentPosition() + ": The fiscal with id (" + fiscalRead
                                    + ") does not exist.");
                }

                audienciaList.add(audiencia);
                audienciaAssignment.setAudiencia(audiencia);
                audienciaAssignmentList.add(audienciaAssignment);
//                System.out.println(audiencia.getNumTimeGrains() + " " + audiencia.getDefensor().getNombreDefensor() + audiencia.getFiscal().getNombreFiscal() + audiencia.getJuez().getIdJuez());
                }

            solution.setAudienciaList(audienciaList);
            solution.setAudienciaAssignmentList(audienciaAssignmentList);
            }

        private boolean containsTipo(final List<Tipo> list, final int numero){
            return list.stream().anyMatch(o -> o.getIdTipo() == numero);
        }

        private boolean containsJuez(final List<Juez> list, final int numero){
            return list.stream().anyMatch(o -> o.getIdJuez() == numero);
        }

        private boolean containsDefensor(final List<Defensor> list, final String numero){
            return list.stream().anyMatch(o -> o.getIdDefensor().equals(numero));
        }

        private boolean containsFiscal(final List<Fiscal> list, final int numero){
            return list.stream().anyMatch(o -> o.getIdFiscal() == numero);
        }


        private void readAudienciaDuration(Audiencia audiencia) {
            String durationDouble = nextCell().getStringCellValue();
//            System.out.println(durationDouble);
            String[] time = durationDouble.split ( ":" );
            int hour = Integer.parseInt ( time[0].trim() );
            int min = Integer.parseInt ( time[1].trim() );
            int totalMinutes = 60 * hour + min;
            if (totalMinutes <= 0 || totalMinutes != Math.floor(totalMinutes)) {
                throw new IllegalStateException(
                        currentPosition() + ": The audiencia with id (" + audiencia.getIdAudiencia()
                                + ")'s has a duration (" + durationDouble + ") that isn't a strictly positive integer number.");
            }
            if (totalMinutes % TimeGrain.GRAIN_LENGTH_IN_MINUTES != 0) {
                throw new IllegalStateException(
                        currentPosition() + ": The audiencia with id (" + audiencia.getIdAudiencia()
                                + ") has a duration (" + durationDouble + ") that isn't a multiple of "
                                + TimeGrain.GRAIN_LENGTH_IN_MINUTES + ".");
            }
            audiencia.setNumTimeGrains(totalMinutes / TimeGrain.GRAIN_LENGTH_IN_MINUTES);
        }

        private AudienciaSchedule solve(){
            SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource("org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml");
            Solver<AudienciaSchedule> solver = solverFactory.buildSolver();

            AudienciaSchedule solvedAudienciaSchedule = solver.solve(solution);
//            System.out.println(solvedAudienciaSchedule);
            for(AudienciaAssignment audienciaAssignment : solvedAudienciaSchedule.getAudienciaAssignmentList()){
                System.out.println("Audiencia número " + audienciaAssignment.getAudiencia().getIdAudiencia() + " desde " + audienciaAssignment.getStartingTimeGrain().getDateTimeString() + " hasta " + audienciaAssignment.getFinishingTimeString() + " in room number " + audienciaAssignment.getRoom().getNombreRoom());
            }
//            System.out.println(solver.explainBestScore());
            return solvedAudienciaSchedule;
        }
    }

    @Override
    public void write(AudienciaSchedule solution, File outputScheduleFile) {
        try (FileOutputStream out = new FileOutputStream(outputScheduleFile)) {
            Workbook workbook = new AudienciaSchedulingXlsxWriter(solution).write();
            workbook.write(out);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed writing outputScheduleFile (" + outputScheduleFile
                    + ") for schedule (" + solution + ").", e);
        }
    }

    private class AudienciaSchedulingXlsxWriter extends AbstractXlsxWriter<AudienciaSchedule> {

        AudienciaSchedulingXlsxWriter(AudienciaSchedule solution) {
            super(solution, Main.SOLVER_CONFIG);
        }

        @Override
        public Workbook write() {
            workbook = new XSSFWorkbook();
            creationHelper = workbook.getCreationHelper();
            createStyles();
            writeRoomsView();
            writeJuezView();
            writeDefensorView();
            writeFiscalView();
            writePrintedFormView();
            writeScoreView(justificationList -> justificationList.stream()
                    .filter(o -> o instanceof AudienciaAssignment).map(o -> ((AudienciaAssignment) o).toString())
                    .collect(joining(", ")));
            return workbook;
        }

        private void writeRoomsView(){
            nextSheet("Rooms view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Room");
            writeTimeGrainHoursHeaders();
            for (Room room : solution.getRoomList()) {
                nextRow();
                currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
                nextCell().setCellValue(room.getNombreRoom());
                List<AudienciaAssignment> roomAudienciaAssignmentList = solution.getAudienciaAssignmentList().stream().filter(audienciaAssignment -> audienciaAssignment.getRoom() == room).collect(toList());
                writeAudienciaAssignmentList(roomAudienciaAssignmentList);
            }
            autoSizeColumns();
            autoSizeColumnOne();
        }

        private void writeTimeGrainDaysHeaders() {
            Day previousTimeGrainDay = null;
            int mergeStart = -1;

            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                Day timeGrainDay = timeGrain.getDay();
                if (timeGrainDay.equals(previousTimeGrainDay)) {
                    nextHeaderCell("");
                } else {
                    if (previousTimeGrainDay != null) {
                        currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                    }
                    nextHeaderCell(DAY_FORMATTER.format(
                            LocalDate.ofYearDay(Year.now().getValue(), timeGrainDay.getDayOfYear())));
                    previousTimeGrainDay = timeGrainDay;
                    mergeStart = currentColumnNumber;
                }
            }
            if (previousTimeGrainDay != null) {
                currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
            }
        }

        private void writeTimeGrainHoursHeaders() {
            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                LocalTime startTime = LocalTime.ofSecondOfDay(timeGrain.getStartingMinuteOfDay() * 60);
                nextHeaderCell(TIME_FORMATTER.format(startTime));
            }
        }

        private void writeAudienciaAssignmentList(List<AudienciaAssignment> audienciaAssignmentList) {
            String[] filteredConstraintNames = {
                    AudienciaScheduleConstraintConfiguration.ROOM_CONFLICT, AudienciaScheduleConstraintConfiguration.DONT_GO_IN_OVERTIME, AudienciaScheduleConstraintConfiguration.START_AND_END_ON_SAME_DAY, AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_DEFENSOR,
                    AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_FISCAL, AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_JUEZ, AudienciaScheduleConstraintConfiguration.DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME, AudienciaScheduleConstraintConfiguration.DO_NOT_USE_BREAKS,

                    AudienciaScheduleConstraintConfiguration.DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE, AudienciaScheduleConstraintConfiguration.ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS
            };
            int mergeStart = -1;
            int previousAudienciaRemainingTimeGrains = 0;
            boolean mergingPreviousAudienciaList = false;

            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                List<AudienciaAssignment> timeGrainAudienciaAssignmentList = audienciaAssignmentList.stream()
                        .filter(audienciaAssignment -> audienciaAssignment.getStartingTimeGrain() == timeGrain)
                        .collect(toList());
                if (timeGrainAudienciaAssignmentList.isEmpty() && mergingPreviousAudienciaList && previousAudienciaRemainingTimeGrains > 0) {
                    previousAudienciaRemainingTimeGrains--;
                    nextCell();
                } else {
                    if (mergingPreviousAudienciaList && mergeStart < currentColumnNumber) {
                        currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                    }
                    nextAudienciaAssignmentListCell(timeGrainAudienciaAssignmentList, audienciaAssignment -> audienciaAssignment.getAudiencia().getIdAudiencia() + "\n  ", Arrays.asList(filteredConstraintNames));
                    mergingPreviousAudienciaList = !timeGrainAudienciaAssignmentList.isEmpty();
                    mergeStart = currentColumnNumber;
                    previousAudienciaRemainingTimeGrains = getLongestDurationInGrains(timeGrainAudienciaAssignmentList) - 1;
                }
            }

            if (mergingPreviousAudienciaList && mergeStart < currentColumnNumber) {
                currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
            }
        }

        private int getLongestDurationInGrains(List<AudienciaAssignment> audienciaAssignmentList) {
            int longestDurationInGrains = 1;
            for (AudienciaAssignment audienciaAssignment : audienciaAssignmentList) {
                if (audienciaAssignment.getAudiencia().getNumTimeGrains() > longestDurationInGrains) {
                    longestDurationInGrains = audienciaAssignment.getAudiencia().getNumTimeGrains();
                }
            }
            return longestDurationInGrains;
        }

        void nextAudienciaAssignmentListCell(List<AudienciaAssignment> audienciaAssignmentList, Function<AudienciaAssignment, String> stringFunction, List<String> filteredConstraintNames) {
            if (audienciaAssignmentList == null) {
                audienciaAssignmentList = Collections.emptyList();
            }
            HardMediumSoftScore score = audienciaAssignmentList.stream()
                    .map(indictmentMap::get).filter(Objects::nonNull)
                    .flatMap(indictment -> indictment.getConstraintMatchSet().stream())
                    // Filter out filtered constraints
                    .filter(constraintMatch -> filteredConstraintNames == null
                            || filteredConstraintNames.contains(constraintMatch.getConstraintName()))
                    .map(constraintMatch -> (HardMediumSoftScore) constraintMatch.getScore())
                    // Filter out positive constraints
                    .filter(indictmentScore -> !(indictmentScore.getHardScore() >= 0 && indictmentScore.getSoftScore() >= 0))
                    .reduce(Score::add).orElse(HardMediumSoftScore.ZERO);

            XSSFCell cell = getXSSFCellOfScore(score);

            if (!audienciaAssignmentList.isEmpty()) {
                ClientAnchor anchor = creationHelper.createClientAnchor();
                anchor.setCol1(cell.getColumnIndex());
                anchor.setCol2(cell.getColumnIndex() + 4);
                anchor.setRow1(currentRow.getRowNum());
                anchor.setRow2(currentRow.getRowNum() + 4);
                Comment comment = currentDrawing.createCellComment(anchor);
                String commentString = getAudienciaAssignmentListString(audienciaAssignmentList);
                comment.setString(creationHelper.createRichTextString(commentString));
                cell.setCellComment(comment);
            }
            cell.setCellValue(audienciaAssignmentList.stream().map(stringFunction).collect(joining("\n")));
            currentRow.setHeightInPoints(Math.max(currentRow.getHeightInPoints(), audienciaAssignmentList.size() * currentSheet.getDefaultRowHeightInPoints()));
        }

        private XSSFCell getXSSFCellOfScore(HardMediumSoftScore score) {
            XSSFCell cell;
            if (!score.isFeasible()) {
                cell = nextCell(hardPenaltyStyle);
            } else if (score.getMediumScore() < 0) {
                cell = nextCell(mediumPenaltyStyle);
            } else if (score.getSoftScore() < 0) {
                cell = nextCell(softPenaltyStyle);
            } else {
                cell = nextCell(wrappedStyle);
            }
            return cell;
        }

        private String getAudienciaAssignmentListString(List<AudienciaAssignment> audienciaAssignmentList) {
            StringBuilder commentString = new StringBuilder(audienciaAssignmentList.size() * 200);
            for (AudienciaAssignment audienciaAssignment : audienciaAssignmentList) {
                commentString.append("Date and Time: ").append(audienciaAssignment.getStartingTimeGrain().getDateTimeString()).append("\n")
                        .append("Duration: ").append(audienciaAssignment.getAudiencia().getNumTimeGrains() * TimeGrain.GRAIN_LENGTH_IN_MINUTES).append(" minutes.\n")
                        .append("Room: ").append(audienciaAssignment.getRoom().getNombreRoom()).append("\n");

                Indictment indictment = indictmentMap.get(audienciaAssignment);
                if (indictment != null) {
                    commentString.append("\n").append(indictment.getScore().toShortString())
                            .append(" total");
                    Set<ConstraintMatch> constraintMatchSet = indictment.getConstraintMatchSet();
                    List<String> constraintNameList = constraintMatchSet.stream()
                            .map(ConstraintMatch::getConstraintName).distinct().collect(toList());
                    for (String constraintName : constraintNameList) {
                        List<ConstraintMatch> filteredConstraintMatchList = constraintMatchSet.stream()
                                .filter(constraintMatch -> constraintMatch.getConstraintName().equals(constraintName))
                                .collect(toList());
                        HardMediumSoftScore sum = filteredConstraintMatchList.stream()
                                .map(constraintMatch -> (HardMediumSoftScore) constraintMatch.getScore())
                                .reduce(HardMediumSoftScore::add)
                                .orElse(HardMediumSoftScore.ZERO);
                        String justificationTalkCodes = filteredConstraintMatchList.stream()
                                .flatMap(constraintMatch -> constraintMatch.getJustificationList().stream())
                                .filter(justification -> justification instanceof AudienciaAssignment && justification != audienciaAssignment)
                                .distinct().map(o -> Long.toString(((AudienciaAssignment) o).getAudiencia().getIdAudiencia()))
                                .collect(joining(", "));
                        commentString.append("\n    ").append(sum.toShortString())
                                .append(" for ").append(filteredConstraintMatchList.size())
                                .append(" ").append(constraintName).append("s")
                                .append("\n        ").append(justificationTalkCodes);
                    }
                }
                commentString.append("\n\n");
            }
            return commentString.toString();
        }

        private void writeJuezView(){
            nextSheet("Juez view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Juez");
            writeTimeGrainHoursHeaders();
            for(Juez juez : solution.getJuezList()){
                writeJuezAudienciaList(juez);
            }
            autoSizeColumns();
            autoSizeColumnOne();
        }

        private void writeJuezAudienciaList(Juez juez){
            nextRow();
            currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
            nextHeaderCell(juez.getNombre());

            List<Audiencia> juezAudienciaList;

            juezAudienciaList = solution.getAudienciaList().stream().filter(audiencia -> audiencia.getJuez().equals(juez)).collect(toList());

            List<AudienciaAssignment> juezAudienciaAssignmentList = solution.getAudienciaAssignmentList().stream()
                    .filter(audienciaAssignment -> juezAudienciaList.contains(audienciaAssignment.getAudiencia()))
                    .collect(toList());
            writeAudienciaAssignmentList(juezAudienciaAssignmentList);
        }


        private void writeFiscalView(){
            nextSheet("Fiscal view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Fiscal");
            writeTimeGrainHoursHeaders();
            for(Fiscal fiscal : solution.getFiscalList()){
                writeFiscalAudienciaList(fiscal);
            }
            autoSizeColumns();
            autoSizeColumnOne();
        }

        private void writeFiscalAudienciaList(Fiscal fiscal){
            nextRow();
            currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
            nextHeaderCell(fiscal.getNombreFiscal());

            List<Audiencia> fiscalAudienciaList;

            fiscalAudienciaList = solution.getAudienciaList().stream().filter(audiencia -> audiencia.getFiscal().equals(fiscal)).collect(toList());

            List<AudienciaAssignment> fiscalAudienciaAssignmentList = solution.getAudienciaAssignmentList().stream()
                    .filter(audienciaAssignment -> fiscalAudienciaList.contains(audienciaAssignment.getAudiencia()))
                    .collect(toList());
            writeAudienciaAssignmentList(fiscalAudienciaAssignmentList);
        }

        private void writeDefensorView(){
            nextSheet("Defensor view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Defensor");
            writeTimeGrainHoursHeaders();
            for(Defensor defensor : solution.getDefensorList()){
                writeDefensorAudienciaList(defensor);
            }
            autoSizeColumns();
            autoSizeColumnOne();
        }

        private void writeDefensorAudienciaList(Defensor defensor){
            nextRow();
            currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
            nextHeaderCell(defensor.getNombreDefensor());

            List<Audiencia> defensorAudienciaList;

            defensorAudienciaList = solution.getAudienciaList().stream().filter(audiencia -> audiencia.getDefensor().equals(defensor)).collect(toList());

            List<AudienciaAssignment> defensorAudienciaAssignmentList = solution.getAudienciaAssignmentList().stream()
                    .filter(audienciaAssignment -> defensorAudienciaList.contains(audienciaAssignment.getAudiencia()))
                    .collect(toList());
            writeAudienciaAssignmentList(defensorAudienciaAssignmentList);
        }

        private void writePrintedFormView(){

        }

        private void autoSizeColumns(){
            for (int i = 1; i < headerCellCount; i++) {
                currentSheet.setColumnWidth(i, 2000);
            }
        }

        private void autoSizeColumnOne(){
            currentSheet.setColumnWidth(0, 6000);
        }

    }

}
