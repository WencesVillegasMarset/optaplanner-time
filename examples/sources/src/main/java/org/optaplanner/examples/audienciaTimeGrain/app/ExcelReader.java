package org.optaplanner.examples.audienciaTimeGrain.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;

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
        return new AudienciaSchedulingXlsxReader(workbook).read();
    }

    private static class AudienciaSchedulingXlsxReader extends AbstractXlsxReader<AudienciaSchedule>{

        AudienciaSchedulingXlsxReader (XSSFWorkbook workbook){ super(workbook, Main.SOLVER_CONFIG);}

        public AudienciaSchedule read(){
            solution = new AudienciaSchedule();
            readConfiguration();
            readDayList();
            readRoomList();
            readJuezList();
            readDefensorList();
            readFiscalList();
            readTipoList();
            readAudienciaList();
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
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.START_AND_END_ON_SAME_DAY, hardScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DONT_GO_IN_OVERTIME, hardScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_JUEZ, hardScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME, hardScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_FISCAL, hardScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_DEFENSOR, hardScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(hardScore)), "");

            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS, softScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(softScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE, softScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(softScore)), "");

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
                if (!VALID_NAME_PATTERN.matcher(juez.getNombre()).matches()) {
                    throw new IllegalStateException(
                            currentPosition() + ": The person name (" + juez.getNombre()
                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
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
                defensor.setNombreDefensor(nextStringCell().getStringCellValue());
                defensor.setIdDefensor((int)nextNumericCell().getNumericCellValue());
                if (!VALID_NAME_PATTERN.matcher(defensor.getNombreDefensor()).matches()) {
                    throw new IllegalStateException(
                            currentPosition() + ": The person name (" + defensor.getNombreDefensor()
                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
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
                if (!VALID_NAME_PATTERN.matcher(fiscal.getNombreFiscal()).matches()) {
                    throw new IllegalStateException(
                            currentPosition() + ": The person name (" + fiscal.getNombreFiscal()
                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
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
                if (!VALID_NAME_PATTERN.matcher(tipo.getNombreTipo()).matches()) {
                    throw new IllegalStateException(
                            currentPosition() + ": The person name (" + tipo.getNombreTipo()
                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
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
            long dayId = 0L, timeGrainId = 0L;
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
                    day.setIdDay(dayId++);
                    day.setDayOfYear(diaLeido);
                    dayList.add(day);
//                    System.out.println("Día " + day.getDateString());
                }
                LocalTime startTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                int startMinuteOfDay = startTime.getHour() * 60 + startTime.getMinute();
                int endMinuteOfDay = endTime.getHour() * 60 + endTime.getMinute();
                for (int i = 0; (endMinuteOfDay - startMinuteOfDay) > i * TimeGrain.GRAIN_LENGTH_IN_MINUTES; i++) {
                    int timeGrainStartingMinuteOfDay = i * TimeGrain.GRAIN_LENGTH_IN_MINUTES + startMinuteOfDay;
                    TimeGrain timeGrain = new TimeGrain();
                    timeGrain.setIdTimeGrain((int)timeGrainId);
                    timeGrain.setGrainIndex((int) timeGrainId++);
                    timeGrain.setDay(day);
                    timeGrain.setStartingMinuteOfDay(timeGrainStartingMinuteOfDay);
                    timeGrainList.add(timeGrain);
//                    System.out.println(timeGrain.getDateTimeString());
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

        }

        private void readAudienciaList(){

        }

    }






    @Override
    public void write(AudienciaSchedule audienciaSchedule, File file) {

    }
}
